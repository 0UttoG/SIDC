// Archivo: src/main/java/com/SIDC/backend/repositories/ReporteRepository.java
package com.SIDC.backend.repositories;

import com.SIDC.backend.entities.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Venta, Long> {

    // 1. Productos próximos a vencer (30 días o menos) y que tengan stock
    // 1. Productos próximos a vencer (30 días o menos, INCLUYENDO los ya vencidos)
    @Query(value = "SELECT p.nombre, l.codigo_lote, l.fecha_vencimiento, " +
            "CAST((l.fecha_vencimiento - CURRENT_DATE) AS integer) as dias " +
            "FROM existencias e " +
            "JOIN productos p ON e.id_producto = p.id " +
            "JOIN lotes l ON e.id_lote = l.id " +
            "WHERE l.fecha_vencimiento <= CURRENT_DATE + 30 " +
            "AND e.stock_actual > 0 " +
            "ORDER BY l.fecha_vencimiento ASC", nativeQuery = true)
    List<Object[]> obtenerReporteVencimientos();

    // 2. Ventas totales agrupadas por Ruta y Vendedor
    @Query(value = "SELECT r.nombre_ruta, vd.nombre, COUNT(v.id), COALESCE(SUM(v.total), 0) " +
            "FROM ventas v " +
            "JOIN rutas r ON v.id_ruta = r.id " +
            "JOIN vendedores vd ON v.id_vendedor = vd.id " +
            "GROUP BY r.nombre_ruta, vd.nombre", nativeQuery = true)
    List<Object[]> obtenerReporteVentasRuta();

    // 3. Clientes Morosos (Aquellos con saldo > 0), mostrando cuántas facturas a crédito tienen pendientes
    @Query(value = "SELECT c.nombre, c.limite_credito, c.saldo_actual, " +
            "(SELECT COUNT(*) FROM ventas v WHERE v.id_cliente = c.id AND v.es_credito = TRUE AND v.estado = 'Pendiente') as pendientes " +
            "FROM clientes c " +
            "WHERE c.saldo_actual > 0 " +
            "ORDER BY c.saldo_actual DESC", nativeQuery = true)
    List<Object[]> obtenerReporteMorosos();
}