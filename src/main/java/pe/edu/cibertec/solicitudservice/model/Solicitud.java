package pe.edu.cibertec.solicitudservice.model;

import java.time.LocalDateTime;

/**
 * Entidad interna de solicitud de prestamo. El estado es mutable porque
 * cambia al evaluarse el riesgo.
 */
public class Solicitud {

    private final Long idSolicitud;
    private final String codigoSolicitud;
    private final LocalDateTime fechaSolicitud;
    private final Long clienteId;
    private final Double montoSolicitado;
    private final Integer plazoMeses;
    private String estadoSolicitud;

    public Solicitud(Long idSolicitud, String codigoSolicitud, LocalDateTime fechaSolicitud, Long clienteId,
                     Double montoSolicitado, Integer plazoMeses, String estadoSolicitud) {
        this.idSolicitud = idSolicitud;
        this.codigoSolicitud = codigoSolicitud;
        this.fechaSolicitud = fechaSolicitud;
        this.clienteId = clienteId;
        this.montoSolicitado = montoSolicitado;
        this.plazoMeses = plazoMeses;
        this.estadoSolicitud = estadoSolicitud;
    }

    public Long getIdSolicitud() { return idSolicitud; }
    public String getCodigoSolicitud() { return codigoSolicitud; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public Long getClienteId() { return clienteId; }
    public Double getMontoSolicitado() { return montoSolicitado; }
    public Integer getPlazoMeses() { return plazoMeses; }
    public String getEstadoSolicitud() { return estadoSolicitud; }

    public void setEstadoSolicitud(String estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
    }
}
