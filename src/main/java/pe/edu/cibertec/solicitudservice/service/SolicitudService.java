package pe.edu.cibertec.solicitudservice.service;

import pe.edu.cibertec.solicitudservice.dto.ResumenCrediticioClienteDTO;
import pe.edu.cibertec.solicitudservice.dto.SolicitudPrestamoResponseDTO;

public interface SolicitudService {

    SolicitudPrestamoResponseDTO obtenerSolicitud(Long idSolicitud);

    SolicitudPrestamoResponseDTO evaluarYAprobar(Long idSolicitud);

    ResumenCrediticioClienteDTO resumenCliente(Long clienteId);
}
