package com.SIDC.backend.repositories;

import com.SIDC.backend.entities.Existencia;
import com.SIDC.backend.entities.ExistenciaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ExistenciaRepository extends JpaRepository<Existencia, ExistenciaId> {

    // Consulta nativa para cruzar Existencias, Productos y Lotes
    @Query(value = "SELECT e.id_bodega, e.id_producto, e.id_lote, p.nombre, l.codigo_lote, e.stock_actual, l.fecha_vencimiento " +
            "FROM existencias e " +
            "JOIN productos p ON e.id_producto = p.id " +
            "JOIN lotes l ON e.id_lote = l.id", nativeQuery = true)
    List<Object[]> obtenerInventarioCompleto();
}