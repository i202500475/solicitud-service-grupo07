package pe.edu.cibertec.solicitudservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.cibertec.solicitudservice.dto.SolicitudPrestamoResponseDTO;
import pe.edu.cibertec.solicitudservice.service.SolicitudService;

@RestController
@RequestMapping("/api/v1/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;

    @GetMapping("/{idSolicitud}")
    public ResponseEntity<SolicitudPrestamoResponseDTO> obtenerProyeccionSolicitud(
            @PathVariable Long idSolicitud) {
        SolicitudPrestamoResponseDTO response = solicitudService.calcularProyeccionSolicitud(idSolicitud);
        return ResponseEntity.ok(response);
    }


}
