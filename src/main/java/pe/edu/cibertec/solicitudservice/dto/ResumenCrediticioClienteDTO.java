package pe.edu.cibertec.solicitudservice.dto;

import java.util.List;

public record ResumenCrediticioClienteDTO(
        Long clienteId,
        String nombreCliente,
        String dni,
        ScoringRiesgoDTO scoringActual,
        List<SolicitudPrestamoResponseDTO> historialSolicitudes,
        String estadoCrediticioGlobal
) {
}
