package pe.edu.cibertec.solicitudservice.dto;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String path
) {
}
