package pe.edu.cibertec.solicitudservice.repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import pe.edu.cibertec.solicitudservice.model.Solicitud;

/**
 * Repositorio en memoria con solicitudes de prueba.
 */
@Repository
public class SolicitudRepository {

    private static final String EN_EVALUACION = "EN_EVALUACION";

    private final Map<Long, Solicitud> solicitudes = new ConcurrentHashMap<>();

    public SolicitudRepository() {
        guardar(new Solicitud(1L, "FL-2026-0001", LocalDateTime.of(2026, 10, 1, 9, 0), 2001L, 12000.00, 24, EN_EVALUACION));
        guardar(new Solicitud(2L, "FL-2026-0002", LocalDateTime.of(2026, 10, 2, 11, 30), 2002L, 6000.00, 12, EN_EVALUACION));
        guardar(new Solicitud(3L, "FL-2026-0003", LocalDateTime.of(2026, 10, 3, 16, 15), 2002L, 15000.00, 36, EN_EVALUACION));
        guardar(new Solicitud(4L, "FL-2026-0004", LocalDateTime.of(2026, 10, 4, 10, 45), 2003L, 2000.00, 12, EN_EVALUACION));
        guardar(new Solicitud(5L, "FL-2026-0005", LocalDateTime.of(2026, 10, 5, 14, 20), 2004L, 3000.00, 18, EN_EVALUACION));
    }

    public Optional<Solicitud> findById(Long idSolicitud) {
        return Optional.ofNullable(solicitudes.get(idSolicitud));
    }

    public List<Solicitud> findByClienteId(Long clienteId) {
        return solicitudes.values().stream()
                .filter(s -> s.getClienteId().equals(clienteId))
                .sorted(Comparator.comparing(Solicitud::getIdSolicitud))
                .toList();
    }

    public Solicitud guardar(Solicitud solicitud) {
        solicitudes.put(solicitud.getIdSolicitud(), solicitud);
        return solicitud;
    }
}
