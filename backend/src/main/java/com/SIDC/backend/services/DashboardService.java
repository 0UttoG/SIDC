// Archivo: src/main/java/com/SIDC/backend/services/DashboardService.java
package com.SIDC.backend.services;

import com.SIDC.backend.dto.*;
import com.SIDC.backend.repositories.DashboardRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    public DashboardService(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    public DashboardResponseDTO obtenerResumenDashboard() {
        // 1. Obtener KPIs (Si viene null por alguna razón, ponemos 0 para que no explote)
        BigDecimal ventasDelDia = dashboardRepository.obtenerVentasDelDia();
        if (ventasDelDia == null) ventasDelDia = BigDecimal.ZERO;

        Integer productosPorVencer = dashboardRepository.obtenerCantidadProductosPorVencer();
        if (productosPorVencer == null) productosPorVencer = 0;

        BigDecimal valorInventario = dashboardRepository.obtenerValorTotalInventario();
        if (valorInventario == null) valorInventario = BigDecimal.ZERO;

        BigDecimal riesgoCreditoTotal = dashboardRepository.obtenerRiesgoCreditoTotal();
        if (riesgoCreditoTotal == null) riesgoCreditoTotal = BigDecimal.ZERO;

        Integer rutasActivasHoy = dashboardRepository.obtenerRutasActivasHoy();
        if (rutasActivasHoy == null) rutasActivasHoy = 0;

        // 2. Mapear Gráfico de Rutas
        List<GraficoRutaDTO> ventasPorRuta = dashboardRepository.obtenerVentasPorRutaGrafico().stream()
                .map(row -> new GraficoRutaDTO((String) row[0], (BigDecimal) row[1]))
                .collect(Collectors.toList());

        // 3. Mapear Top Productos
        List<TopProductoDTO> topProductos = dashboardRepository.obtenerTop5Productos().stream()
                .map(row -> new TopProductoDTO((String) row[0], ((Number) row[1]).longValue()))
                .collect(Collectors.toList());

        // 4. Mapear Últimas Facturas
        List<FacturaRecienteDTO> ultimasFacturas = dashboardRepository.obtenerUltimas5Facturas().stream()
                .map(row -> new FacturaRecienteDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (BigDecimal) row[2],
                        (String) row[3]
                )).collect(Collectors.toList());

        // 5. Mapear Alertas de Stock
        List<AlertaStockDTO> alertasStock = dashboardRepository.obtenerAlertasStockCritico().stream()
                .map(row -> new AlertaStockDTO(
                        (String) row[0],
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).intValue()
                )).collect(Collectors.toList());

        // 6. Armar el JSON Final
        return new DashboardResponseDTO(
                ventasDelDia, productosPorVencer, valorInventario, riesgoCreditoTotal, rutasActivasHoy,
                ventasPorRuta, topProductos, ultimasFacturas, alertasStock
        );
    }
}