package pe.edu.cibertec.solicitudservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.cibertec.solicitudservice.dto.ResumenCrediticioClienteDTO;
import pe.edu.cibertec.solicitudservice.dto.SolicitudPrestamoResponseDTO;
import pe.edu.cibertec.solicitudservice.service.SolicitudService;

@RestController
@RequestMapping("/api/v1/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @GetMapping("/{idSolicitud}")
    public ResponseEntity<SolicitudPrestamoResponseDTO> obtenerSolicitud(@PathVariable Long idSolicitud) {
        return ResponseEntity.ok(solicitudService.obtenerSolicitud(idSolicitud));
    }

    @GetMapping("/evaluar-y-aprobar/{idSolicitud}")
    public ResponseEntity<SolicitudPrestamoResponseDTO> evaluarYAprobar(@PathVariable Long idSolicitud) {
        return ResponseEntity.ok(solicitudService.evaluarYAprobar(idSolicitud));
    }

    @GetMapping("/resumen-cliente/{clienteId}")
    public ResponseEntity<ResumenCrediticioClienteDTO> resumenCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(solicitudService.resumenCliente(clienteId));
    }
}
