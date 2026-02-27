package com.SIDC.backend.dto;
import java.math.BigDecimal;

public record ReporteVentasRutaDTO(
        String rutaZona,
        String vendedorAsignado,
        Integer cantPedidos,
        BigDecimal totalVendido
) {}