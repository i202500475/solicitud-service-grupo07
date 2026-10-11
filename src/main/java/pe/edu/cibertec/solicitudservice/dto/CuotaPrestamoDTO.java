package pe.edu.cibertec.solicitudservice.dto;

import java.time.LocalDate;

public record CuotaPrestamoDTO(
        Integer numeroCuota,
        LocalDate fechaVencimiento,
        Double montoCuota,
        Double interes,
        Double capital,
        String estadoCuota
) {
}
