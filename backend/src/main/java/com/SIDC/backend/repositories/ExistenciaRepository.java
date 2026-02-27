package com.SIDC.backend.repositories;

import com.SIDC.backend.dto.ProductoVentaDTO;
import com.SIDC.backend.entities.Existencia;
import com.SIDC.backend.entities.ExistenciaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ExistenciaRepository extends JpaRepository<Existencia, ExistenciaId> {

    // 1. Tu consulta original para el Dashboard (Listado de Inventario)
    @Query(value = "SELECT e.id_bodega, e.id_producto, e.id_lote, p.nombre, l.codigo_lote, e.stock_actual, l.fecha_vencimiento " +
            "FROM existencias e " +
            "JOIN productos p ON e.id_producto = p.id " +
            "JOIN lotes l ON e.id_lote = l.id", nativeQuery = true)
    List<Object[]> obtenerInventarioCompleto();

    // 2. Nueva consulta para la Vista de Ventas (Tarjetas de Productos)
    // Usamos JPQL para que Spring cree los objetos DTO automáticamente
    @Query("SELECT new com.SIDC.backend.dto.ProductoVentaDTO(" +
            "e.id.idBodega, e.id.idProducto, e.id.idLote, p.nombre, l.codigoLote, l.fechaVencimiento, e.stockActual, p.precioBase) " +
            "FROM Existencia e " +
            "JOIN Producto p ON e.id.idProducto = p.id " +
            "JOIN Lote l ON e.id.idLote = l.id " +
            "WHERE e.stockActual > 0 AND l.fechaVencimiento > CURRENT_DATE")
    List<ProductoVentaDTO> obtenerCatalogoVenta();
}