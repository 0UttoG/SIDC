// Archivo: src/main/java/com/SIDC/backend/repositories/ExistenciaRepository.java
package com.SIDC.backend.repositories;

import com.SIDC.backend.entities.Existencia;
import com.SIDC.backend.entities.ExistenciaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ExistenciaRepository extends JpaRepository<Existencia, ExistenciaId> {

    // 1. Método para el Dashboard (Filtra solo productos activos)
    @Query(value = "SELECT e.id_bodega, e.id_producto, e.id_lote, p.nombre, l.codigo_lote, e.stock_actual, l.fecha_vencimiento, p.precio_base " +
            "FROM existencias e " +
            "JOIN productos p ON e.id_producto = p.id " +
            "JOIN lotes l ON e.id_lote = l.id " +
            "WHERE p.activo = true", nativeQuery = true)
    List<Object[]> obtenerInventarioCompleto();

    // 2. Método para Ventas (Filtra stock > 0, no vencidos Y activos. CORREGIDO: > CURRENT_DATE)
    @Query(value = "SELECT e.id_bodega, e.id_producto, e.id_lote, p.nombre, l.codigo_lote, e.stock_actual, l.fecha_vencimiento, p.precio_base " +
            "FROM existencias e " +
            "JOIN productos p ON e.id_producto = p.id " +
            "JOIN lotes l ON e.id_lote = l.id " +
            "WHERE e.stock_actual > 0 AND l.fecha_vencimiento > CURRENT_DATE AND p.activo = true", nativeQuery = true)
    List<Object[]> obtenerCatalogoVentasRaw();
}