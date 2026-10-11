package pe.edu.cibertec.solicitudservice.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import pe.edu.cibertec.solicitudservice.dto.ErrorResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> manejarNoEncontrado(RecursoNoEncontradoException ex,
                                                                HttpServletRequest request) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    /** Errores de comunicacion con evaluacion-service (caido, timeout, 5xx). */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponseDTO> manejarFeign(FeignException ex, HttpServletRequest request) {
        return construir(HttpStatus.SERVICE_UNAVAILABLE,
                "Error al comunicarse con evaluacion-service: " + ex.getMessage(), request);
    }

    private ResponseEntity<ErrorResponseDTO> construir(HttpStatus status, String mensaje,
                                                       HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ErrorResponseDTO(LocalDateTime.now(),
                status.value(), status.getReasonPhrase(), mensaje, request.getRequestURI()));
    }
}
