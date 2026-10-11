package pe.edu.cibertec.solicitudservice.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SolicitudPrestamoResponseDTO(
        Long idSolicitud,
        String codigoSolicitud,
        LocalDateTime fechaSolicitud,
        Long clienteId,
        Double montoSolicitado,
        Integer plazoMeses,
        String estadoSolicitud,
        List<CuotaPrestamoDTO> listaCuotasProyectadas,
        Double tasaInteresAnual,
        Double montoTotalPagar
) {
}
