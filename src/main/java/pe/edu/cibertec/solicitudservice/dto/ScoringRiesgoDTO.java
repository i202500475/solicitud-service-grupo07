package pe.edu.cibertec.solicitudservice.dto;

public record ScoringRiesgoDTO(
        Long clienteId,
        Integer puntajeCrediticio,
        String nivelRiesgo,
        Double capacidadMaxEndeudamiento,
        String dictamenRiesgo
) {
}
