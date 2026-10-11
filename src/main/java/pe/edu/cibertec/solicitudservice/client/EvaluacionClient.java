package pe.edu.cibertec.solicitudservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import pe.edu.cibertec.solicitudservice.dto.ScoringRiesgoDTO;

/**
 * Cliente declarativo hacia evaluacion-service. El nombre se resuelve en
 * Eureka y Spring Cloud LoadBalancer elige la instancia.
 */
@FeignClient(name = "evaluacion-service-grupo07", path = "/api/v1/evaluaciones")
public interface EvaluacionClient {

    @GetMapping("/scoring/{clienteId}")
    ScoringRiesgoDTO obtenerScoring(@PathVariable("clienteId") Long clienteId);
}
