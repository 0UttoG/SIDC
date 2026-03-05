// Archivo: src/main/java/com/SIDC/backend/repositories/DashboardRepository.java
package com.SIDC.backend.repositories;

import com.SIDC.backend.entities.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DashboardRepository extends JpaRepository<Venta, Long> {

    // --- 1. CONSULTAS PARA LAS TARJETAS KPI ---

    @Query(value = "SELECT COALESCE(SUM(total), 0) FROM ventas WHERE DATE(fecha_venta) = CURRENT_DATE", nativeQuery = true)
    BigDecimal obtenerVentasDelDia();

    @Query(value = "SELECT COUNT(*) FROM lotes l JOIN existencias e ON l.id = e.id_lote WHERE l.fecha_vencimiento <= CURRENT_DATE + 30 AND e.stock_actual > 0", nativeQuery = true)
    Integer obtenerCantidadProductosPorVencer();

    // Asumiendo que en tu tabla 'productos' hay un campo 'precio' o similar. Ajusta el nombre si se llama distinto (ej. precio_unitario)
// Gráfico de Valor del Inventario
    @Query(value = "SELECT COALESCE(SUM(p.precio_base * e.stock_actual), 0) FROM existencias e JOIN productos p ON e.id_producto = p.id", nativeQuery = true)
    BigDecimal obtenerValorTotalInventario();

    @Query(value = "SELECT COALESCE(SUM(saldo_actual), 0) FROM clientes", nativeQuery = true)
    BigDecimal obtenerRiesgoCreditoTotal();

    @Query(value = "SELECT COUNT(DISTINCT id_ruta) FROM ventas WHERE DATE(fecha_venta) = CURRENT_DATE", nativeQuery = true)
    Integer obtenerRutasActivasHoy();


    // --- 2. CONSULTAS PARA LOS GRÁFICOS Y LISTAS ---

    // Gráfico de Pastel: Ventas por Ruta (Todo el tiempo o puedes filtrarlo por mes si prefieres)
    @Query(value = "SELECT r.nombre_ruta, COALESCE(SUM(v.total), 0) FROM ventas v JOIN rutas r ON v.id_ruta = r.id GROUP BY r.nombre_ruta", nativeQuery = true)
    List<Object[]> obtenerVentasPorRutaGrafico();



    // Tabla: Últimas 5 Facturas
    @Query(value = "SELECT v.id, c.nombre, v.total, CASE WHEN v.es_credito = TRUE THEN 'Crédito' ELSE 'Contado' END FROM ventas v JOIN clientes c ON v.id_cliente = c.id ORDER BY v.fecha_venta DESC LIMIT 5", nativeQuery = true)
    List<Object[]> obtenerUltimas5Facturas();

    // Gráfico de Barras: Top 5 Productos más vendidos (CORREGIDO: ventas_detalle en plural)
    @Query(value = "SELECT p.nombre, SUM(vd.cantidad) as cant FROM ventas_detalle vd JOIN productos p ON vd.id_producto = p.id GROUP BY p.nombre ORDER BY cant DESC LIMIT 5", nativeQuery = true)
    List<Object[]> obtenerTop5Productos();

    // Tabla: Alertas de Stock Crítico
    // CORREGIDO: Como no tienes campo 'stock_minimo' en la BD, vamos a ponerle que el sistema
    // avise automáticamente cuando cualquier producto tenga 20 unidades o menos (quemamos el número 20).
    @Query(value = "SELECT p.nombre, e.stock_actual, 20 FROM existencias e JOIN productos p ON e.id_producto = p.id WHERE e.stock_actual <= 20", nativeQuery = true)
    List<Object[]> obtenerAlertasStockCritico();
}