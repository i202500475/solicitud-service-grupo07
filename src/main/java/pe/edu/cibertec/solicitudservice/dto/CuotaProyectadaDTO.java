package pe.edu.cibertec.solicitudservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CuotaProyectadaDTO {
    private Integer numeroCuota;
    private LocalDate fechaVencimiento;
    private BigDecimal montoCuota;
    private BigDecimal interes;
    private BigDecimal amortizacion;
    private BigDecimal saldoPendiente;
}
