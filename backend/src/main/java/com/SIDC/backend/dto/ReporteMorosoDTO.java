package com.SIDC.backend.dto;
import java.math.BigDecimal;

public record ReporteMorosoDTO(
        String cliente,
        BigDecimal limiteCredito,
        BigDecimal saldoActual,
        Integer moraDias
) {}