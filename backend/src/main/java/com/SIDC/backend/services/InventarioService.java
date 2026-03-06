// Archivo: src/main/java/com/SIDC/backend/services/InventarioService.java
package com.SIDC.backend.services;

import com.SIDC.backend.dto.*;
import com.SIDC.backend.entities.*;
import com.SIDC.backend.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class InventarioService {

    private final ExistenciaRepository existenciaRepository;
    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;

    public InventarioService(ExistenciaRepository existenciaRepository, ProductoRepository productoRepository, LoteRepository loteRepository) {
        this.existenciaRepository = existenciaRepository;
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
    }

    // ==========================================
    // READ: Dashboard (Ahora incluye el precio)
    // ==========================================
    public List<InventarioResponseDTO> obtenerListadoInventario() {
        List<Object[]> resultados = existenciaRepository.obtenerInventarioCompleto();
        List<InventarioResponseDTO> listado = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        for (Object[] fila : resultados) {
            try {
                String nombre = fila[3] != null ? fila[3].toString() : "Sin nombre";
                String codigoLote = fila[4] != null ? fila[4].toString() : "S/L";
                Integer stock = fila[5] != null ? ((Number) fila[5]).intValue() : 0;
                LocalDate vencimiento = convertirAFuncionLocalDate(fila[6]);

                // Lógica de estados
                String estado = "Stock Óptimo";
                long diasParaVencer = ChronoUnit.DAYS.between(hoy, vencimiento);
                if (diasParaVencer <= 30 && diasParaVencer >= 0) estado = "Próximo a Vencer";
                else if (diasParaVencer < 0) estado = "Vencido";
                else if (stock <= 10) estado = "Bajo Stock";

                // Extraemos el precio base (el nuevo campo en la posición 7)
                BigDecimal precio = fila[7] != null ? new BigDecimal(fila[7].toString()) : BigDecimal.ZERO;

                listado.add(new InventarioResponseDTO(
                        ((Number) fila[0]).longValue(), ((Number) fila[1]).longValue(),
                        ((Number) fila[2]).longValue(), nombre, codigoLote, stock, vencimiento, estado, precio
                ));
            } catch (Exception e) {
                System.err.println("Error dashboard: " + e.getMessage());
            }
        }
        return listado;
    }

    // ==========================================
    // READ: Catálogo de Ventas
    // ==========================================
    public List<ProductoVentaDTO> obtenerCatalogoVentas() {
        List<Object[]> resultados = existenciaRepository.obtenerCatalogoVentasRaw();
        List<ProductoVentaDTO> catalogo = new ArrayList<>();

        for (Object[] fila : resultados) {
            try {
                catalogo.add(new ProductoVentaDTO(
                        ((Number) fila[0]).longValue(), ((Number) fila[1]).longValue(),
                        ((Number) fila[2]).longValue(), fila[3].toString(), fila[4].toString(),
                        convertirAFuncionLocalDate(fila[6]), ((Number) fila[5]).intValue(),
                        new BigDecimal(fila[7].toString())
                ));
            } catch (Exception e) {
                System.err.println("Error catálogo ventas: " + e.getMessage());
            }
        }
        return catalogo;
    }

    private LocalDate convertirAFuncionLocalDate(Object objetoFecha) {
        if (objetoFecha == null) return LocalDate.now();
        if (objetoFecha instanceof java.sql.Date sqlDate) return sqlDate.toLocalDate();
        if (objetoFecha instanceof java.sql.Timestamp ts) return ts.toLocalDateTime().toLocalDate();
        return LocalDate.parse(objetoFecha.toString());
    }

    // ==========================================
    // CREATE: Registrar Nuevo Lote
    // ==========================================
    @Transactional
    public void registrarNuevoLote(NuevoLoteDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.nombreProducto());
        producto.setPrecioBase(dto.precio());
        producto = productoRepository.save(producto);

        Lote lote = new Lote();
        lote.setIdProducto(producto.getId());
        lote.setCodigoLote(dto.codigoLote());
        lote.setFechaVencimiento(dto.fechaVencimiento());
        lote = loteRepository.save(lote);

        Existencia existencia = new Existencia();
        existencia.setId(new ExistenciaId(dto.idBodega() != null ? dto.idBodega() : 1L, producto.getId(), lote.getId()));
        existencia.setStockActual(dto.stock());
        existenciaRepository.save(existencia);
    }

    // ==========================================
    // UPDATE: Ajustar Stock
    // ==========================================
    @Transactional
    public void ajustarStock(AjusteStockDTO dto) {
        ExistenciaId id = new ExistenciaId(dto.idBodega(), dto.idProducto(), dto.idLote());
        Existencia existencia = existenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Existencia no encontrada"));

        if ("Entrada".equalsIgnoreCase(dto.tipoMovimiento())) {
            existencia.setStockActual(existencia.getStockActual() + dto.cantidad());
        } else if ("Salida".equalsIgnoreCase(dto.tipoMovimiento())) {
            if (existencia.getStockActual() < dto.cantidad()) {
                throw new RuntimeException("Violación: No hay stock suficiente.");
            }
            existencia.setStockActual(existencia.getStockActual() - dto.cantidad());
        }
        existenciaRepository.save(existencia);
    }

    // ==========================================
    // UPDATE - Actualizar información Completa (Cero tolerancia a errores)
    // ==========================================
    @Transactional
    public void actualizarProducto(Long idProducto, ProductoActualizarDTO dto) {

        // 0. Validación estricta: Si falta el ID del lote o bodega, cortamos la ejecución de raíz.
        if (dto.idLote() == null || dto.idBodega() == null) {
            throw new RuntimeException("Error: El frontend no está enviando el idLote o el idBodega.");
        }

        // 1. Actualizamos la tabla PRODUCTOS
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        producto.setNombre(dto.nombreProducto());
        producto.setPrecioBase(dto.precio());
        productoRepository.save(producto);

        // 2. Actualizamos la tabla LOTES (Ya sin el if condicional)
        Lote lote = loteRepository.findById(dto.idLote())
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));
        lote.setCodigoLote(dto.codigoLote());
        lote.setFechaVencimiento(dto.fechaVencimiento());
        loteRepository.save(lote);

        // 3. Actualizamos la tabla EXISTENCIAS (Ya sin el if condicional)
        ExistenciaId existenciaId = new ExistenciaId(dto.idBodega(), idProducto, dto.idLote());
        Existencia existencia = existenciaRepository.findById(existenciaId)
                .orElseThrow(() -> new RuntimeException("Existencia no encontrada"));
        existencia.setStockActual(dto.stock());
        existenciaRepository.save(existencia);
    }

    // ==========================================
    // DELETE LÓGICO - Deshabilitar Producto
    // ==========================================
    @Transactional
    public void deshabilitarProducto(Long idProducto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setActivo(false);
        productoRepository.save(producto);
    }
}