// Archivo: src/main/java/com/SIDC/backend/dto/DashboardResponseDTO.java
package com.SIDC.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponseDTO(
        BigDecimal ventasDelDia,
        Integer productosPorVencer,
        BigDecimal valorInventario,
        BigDecimal riesgoCreditoTotal,
        Integer rutasActivasHoy,
        List<GraficoRutaDTO> ventasPorRuta,
        List<TopProductoDTO> topProductos,
        List<FacturaRecienteDTO> ultimasFacturas,
        List<AlertaStockDTO> alertasStock
) {}