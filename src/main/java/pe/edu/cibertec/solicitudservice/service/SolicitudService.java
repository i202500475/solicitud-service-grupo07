package pe.edu.cibertec.solicitudservice.service;

import org.springframework.stereotype.Service;
import pe.edu.cibertec.solicitudservice.dto.CuotaProyectadaDTO;
import pe.edu.cibertec.solicitudservice.dto.SolicitudPrestamoResponseDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SolicitudService {

    public SolicitudPrestamoResponseDTO calcularProyeccionSolicitud(Long idSolicitud) {
        // Valores simulados de la solicitud
        BigDecimal montoSolicitado = new BigDecimal("10000.00");
        Integer numeroCuotas = 12;
        BigDecimal tasaInteresAnual = new BigDecimal("18.00"); // 18% anual
        Long idEmpleado = 101L;

        // Tasa efectiva mensual i = (1 + TEA)^(1/12) - 1  o aproximada nominal: (TasaAnual / 12) / 100
        BigDecimal tasaMensualDecimal = tasaInteresAnual.divide(new BigDecimal("1200"), 8, RoundingMode.HALF_UP);

        // Cálculo de cuota fija mensual usando la fórmula del método francés:
        // C = P * [ i * (1 + i)^n ] / [ (1 + i)^n - 1 ]
        BigDecimal unoMasI = BigDecimal.ONE.add(tasaMensualDecimal);
        BigDecimal factorPower = unoMasI.pow(numeroCuotas);

        BigDecimal numerador = montoSolicitado.multiply(tasaMensualDecimal).multiply(factorPower);
        BigDecimal denominador = factorPower.subtract(BigDecimal.ONE);
        BigDecimal montoCuotaFija = numerador.divide(denominador, 2, RoundingMode.HALF_UP);

        List<CuotaProyectadaDTO> listaCuotas = new ArrayList<>();
        BigDecimal saldoPendiente = montoSolicitado;
        BigDecimal montoTotalPagar = BigDecimal.ZERO;
        LocalDate fechaInicio = LocalDate.now();

        for (int i = 1; i <= numeroCuotas; i++) {
            BigDecimal interesCuota = saldoPendiente.multiply(tasaMensualDecimal).setScale(2, RoundingMode.HALF_UP);
            BigDecimal amortizacionCuota = montoCuotaFija.subtract(interesCuota);

            // Ajuste para la última cuota para evitar descuadres por redondeo
            if (i == numeroCuotas) {
                amortizacionCuota = saldoPendiente;
                montoCuotaFija = amortizacionCuota.add(interesCuota);
                saldoPendiente = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            } else {
                saldoPendiente = saldoPendiente.subtract(amortizacionCuota).setScale(2, RoundingMode.HALF_UP);
            }

            montoTotalPagar = montoTotalPagar.add(montoCuotaFija);

            CuotaProyectadaDTO cuota = CuotaProyectadaDTO.builder()
                    .numeroCuota(i)
                    .fechaVencimiento(fechaInicio.plusMonths(i))
                    .montoCuota(montoCuotaFija)
                    .interes(interesCuota)
                    .amortizacion(amortizacionCuota)
                    .saldoPendiente(saldoPendiente)
                    .build();

            listaCuotas.add(cuota);
        }

        return SolicitudPrestamoResponseDTO.builder()
                .idSolicitud(idSolicitud)
                .idEmpleado(idEmpleado)
                .montoSolicitado(montoSolicitado)
                .numeroCuotas(numeroCuotas)
                .tasaInteresAnual(tasaInteresAnual)
                .montoTotalPagar(montoTotalPagar.setScale(2, RoundingMode.HALF_UP))
                .listaCuotasProyectadas(listaCuotas)
                .build();
    }
}
