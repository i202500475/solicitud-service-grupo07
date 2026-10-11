package pe.edu.cibertec.solicitudservice.controller;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Propiedades obtenidas del repositorio Comercial (patron solicitud-*).
 * Con @RefreshScope el bean se recrea tras POST /actuator/refresh.
 */
@RefreshScope
@RestController
@RequestMapping("/api/v1/solicitudes")
public class ConfiguracionController {

    @Value("${fintech.tasa-interes-base}")
    private BigDecimal tasaInteresBase;

    @Value("${fintech.monto-maximo-prestamo}")
    private BigDecimal montoMaximoPrestamo;

    @Value("${fintech.mensaje-comercial}")
    private String mensajeComercial;

    @Value("${fintech.comision-desembolso}")
    private BigDecimal comisionDesembolso;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @GetMapping("/configuracion")
    public ResponseEntity<Map<String, Object>> obtenerConfiguracion() {
        Map<String, Object> configuracion = new LinkedHashMap<>();
        configuracion.put("tasa-interes-base", tasaInteresBase);
        configuracion.put("monto-maximo-prestamo", montoMaximoPrestamo);
        configuracion.put("mensaje-comercial", mensajeComercial);
        configuracion.put("comision-desembolso", comisionDesembolso);
        configuracion.put("datasource.url", datasourceUrl);
        return ResponseEntity.ok(configuracion);
    }
}
