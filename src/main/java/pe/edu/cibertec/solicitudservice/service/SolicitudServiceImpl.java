package pe.edu.cibertec.solicitudservice.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import feign.FeignException;
import pe.edu.cibertec.solicitudservice.client.EvaluacionClient;
import pe.edu.cibertec.solicitudservice.dto.CuotaPrestamoDTO;
import pe.edu.cibertec.solicitudservice.dto.ResumenCrediticioClienteDTO;
import pe.edu.cibertec.solicitudservice.dto.ScoringRiesgoDTO;
import pe.edu.cibertec.solicitudservice.dto.SolicitudPrestamoResponseDTO;
import pe.edu.cibertec.solicitudservice.exception.RecursoNoEncontradoException;
import pe.edu.cibertec.solicitudservice.model.Cliente;
import pe.edu.cibertec.solicitudservice.model.Solicitud;
import pe.edu.cibertec.solicitudservice.repository.ClienteRepository;
import pe.edu.cibertec.solicitudservice.repository.SolicitudRepository;

/**
 * La tasa de interes anual se toma de fintech.tasa-interes-base (repositorio
 * Comercial). Con @RefreshScope, si la tasa cambia en GitHub y se ejecuta
 * /actuator/refresh, las cuotas proyectadas se recalculan sin reiniciar.
 */
@RefreshScope
@Service
public class SolicitudServiceImpl implements SolicitudService {

    private static final String APROBADO = "APROBADO";
    private static final String RECHAZADO_POR_RIESGO = "RECHAZADO_POR_RIESGO";
    private static final String CUOTA_PENDIENTE = "PENDIENTE";
    private static final Set<String> RIESGOS_ACEPTABLES = Set.of("BAJO", "MEDIO");

    private final SolicitudRepository solicitudRepository;
    private final ClienteRepository clienteRepository;
    private final EvaluacionClient evaluacionClient;
    private final Double tasaInteresAnual;

    public SolicitudServiceImpl(SolicitudRepository solicitudRepository,
                                ClienteRepository clienteRepository,
                                EvaluacionClient evaluacionClient,
                                @Value("${fintech.tasa-interes-base}") Double tasaInteresAnual) {
        this.solicitudRepository = solicitudRepository;
        this.clienteRepository = clienteRepository;
        this.evaluacionClient = evaluacionClient;
        this.tasaInteresAnual = tasaInteresAnual;
    }

    @Override
    public SolicitudPrestamoResponseDTO obtenerSolicitud(Long idSolicitud) {
        return toDTO(buscarSolicitud(idSolicitud));
    }

    @Override
    public SolicitudPrestamoResponseDTO evaluarYAprobar(Long idSolicitud) {
        Solicitud solicitud = buscarSolicitud(idSolicitud);
        ScoringRiesgoDTO scoring = consultarScoring(solicitud.getClienteId());

        boolean dentroDeCapacidad = solicitud.getMontoSolicitado() <= scoring.capacidadMaxEndeudamiento();
        boolean riesgoAceptable = RIESGOS_ACEPTABLES.contains(scoring.nivelRiesgo());

        solicitud.setEstadoSolicitud(dentroDeCapacidad && riesgoAceptable ? APROBADO : RECHAZADO_POR_RIESGO);
        solicitudRepository.guardar(solicitud);
        return toDTO(solicitud);
    }

    @Override
    public ResumenCrediticioClienteDTO resumenCliente(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el cliente " + clienteId));

        ScoringRiesgoDTO scoring = consultarScoring(clienteId);

        List<SolicitudPrestamoResponseDTO> historial = solicitudRepository.findByClienteId(clienteId).stream()
                .map(this::toDTO)
                .toList();

        // Deuda ya comprometida: capital de las solicitudes aprobadas
        double montoAprobado = historial.stream()
                .filter(s -> APROBADO.equals(s.estadoSolicitud()))
                .mapToDouble(SolicitudPrestamoResponseDTO::montoSolicitado)
                .sum();

        // APTO: riesgo BAJO/MEDIO y la deuda aprobada no supera su capacidad maxima
        boolean apto = RIESGOS_ACEPTABLES.contains(scoring.nivelRiesgo())
                && montoAprobado <= scoring.capacidadMaxEndeudamiento();

        return new ResumenCrediticioClienteDTO(
                cliente.clienteId(),
                cliente.nombreCliente(),
                cliente.dni(),
                scoring,
                historial,
                apto ? "APTO" : "NO_APTO");
    }

    // ------------------------------------------------------------------
    // Metodos auxiliares
    // ------------------------------------------------------------------

    private Solicitud buscarSolicitud(Long idSolicitud) {
        return solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la solicitud " + idSolicitud));
    }

    private ScoringRiesgoDTO consultarScoring(Long clienteId) {
        try {
            return evaluacionClient.obtenerScoring(clienteId);
        } catch (FeignException.NotFound ex) {
            throw new RecursoNoEncontradoException(
                    "evaluacion-service no tiene scoring para el cliente " + clienteId);
        }
    }

    private SolicitudPrestamoResponseDTO toDTO(Solicitud s) {
        List<CuotaPrestamoDTO> cuotas = calcularCuotas(s);
        double montoTotalPagar = redondear(cuotas.stream()
                .mapToDouble(CuotaPrestamoDTO::montoCuota)
                .sum());

        return new SolicitudPrestamoResponseDTO(
                s.getIdSolicitud(),
                s.getCodigoSolicitud(),
                s.getFechaSolicitud(),
                s.getClienteId(),
                s.getMontoSolicitado(),
                s.getPlazoMeses(),
                s.getEstadoSolicitud(),
                cuotas,
                tasaInteresAnual,
                montoTotalPagar);
    }

    /**
     * Tabla de amortizacion por metodo frances (cuota fija):
     * cuota = P * i / (1 - (1 + i)^-n), con i = tasa anual / 12.
     */
    private List<CuotaPrestamoDTO> calcularCuotas(Solicitud s) {
        double capitalInicial = s.getMontoSolicitado();
        int n = s.getPlazoMeses();
        double i = tasaInteresAnual / 12;

        double cuotaFija = (i == 0)
                ? capitalInicial / n
                : capitalInicial * i / (1 - Math.pow(1 + i, -n));
        cuotaFija = redondear(cuotaFija);

        LocalDate primerVencimiento = s.getFechaSolicitud().toLocalDate();
        double saldo = capitalInicial;
        List<CuotaPrestamoDTO> cuotas = new ArrayList<>();

        for (int k = 1; k <= n; k++) {
            double interes = redondear(saldo * i);
            // En la ultima cuota se amortiza el saldo restante para cerrar en 0
            double capital = (k == n) ? redondear(saldo) : redondear(cuotaFija - interes);
            double montoCuota = redondear(capital + interes);
            saldo = redondear(saldo - capital);

            cuotas.add(new CuotaPrestamoDTO(k, primerVencimiento.plusMonths(k), montoCuota,
                    interes, capital, CUOTA_PENDIENTE));
        }
        return cuotas;
    }

    private double redondear(double valor) {
        return BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
