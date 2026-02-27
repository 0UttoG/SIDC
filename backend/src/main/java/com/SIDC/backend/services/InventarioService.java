package com.SIDC.backend.services;

import com.SIDC.backend.dto.*;
import com.SIDC.backend.entities.*;
import com.SIDC.backend.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // READ: Obtener listado para el Dashboard
    // Dentro de InventarioService.java

    public List<InventarioResponseDTO> obtenerListadoInventario() {
        List<Object[]> resultados = existenciaRepository.obtenerInventarioCompleto();
        List<InventarioResponseDTO> listado = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        for (Object[] fila : resultados) {
            try {
                // Usamos una forma más segura de extraer números para evitar ClassCastException
                Long idBodega = fila[0] != null ? ((Number) fila[0]).longValue() : 0L;
                Long idProducto = fila[1] != null ? ((Number) fila[1]).longValue() : 0L;
                Long idLote = fila[2] != null ? ((Number) fila[2]).longValue() : 0L;

                String nombre = fila[3] != null ? fila[3].toString() : "Sin nombre";
                String codigoLote = fila[4] != null ? fila[4].toString() : "S/L";
                Integer stock = fila[5] != null ? ((Number) fila[5]).intValue() : 0;

                // Manejo seguro de fechas (PostgreSQL Date -> java.sql.Date -> LocalDate)
                LocalDate vencimiento = hoy;
                if (fila[6] != null) {
                    if (fila[6] instanceof java.sql.Date sqlDate) {
                        vencimiento = sqlDate.toLocalDate();
                    } else if (fila[6] instanceof java.sql.Timestamp sqlTimestamp) {
                        vencimiento = sqlTimestamp.toLocalDateTime().toLocalDate();
                    }
                }

                // Lógica de estados
                String estado = "Stock Óptimo";
                long diasParaVencer = ChronoUnit.DAYS.between(hoy, vencimiento);

                if (diasParaVencer <= 30 && diasParaVencer >= 0) {
                    estado = "Próximo a Vencer";
                } else if (diasParaVencer < 0) {
                    estado = "Vencido";
                } else if (stock <= 10) {
                    estado = "Bajo Stock";
                }

                listado.add(new InventarioResponseDTO(
                        idBodega, idProducto, idLote, nombre, codigoLote, stock, vencimiento, estado
                ));

            } catch (Exception e) {
                // Si una fila falla, la saltamos y mostramos el error en consola en lugar de tumbar la app
                System.err.println("Error procesando fila de inventario: " + e.getMessage());
            }
        }
        return listado;
    }

    // CREATE: Registrar Nuevo Producto + Lote + Existencia
    @Transactional
    public void registrarNuevoLote(NuevoLoteDTO dto) {
        // 1. Crear Producto
        Producto producto = new Producto();
        producto.setNombre(dto.nombreProducto());
        producto.setPrecioBase(dto.precio());
        // IdCategoria lo dejamos nulo o por defecto por ahora
        producto = productoRepository.save(producto);

        // 2. Crear Lote
        Lote lote = new Lote();
        lote.setIdProducto(producto.getId());
        lote.setCodigoLote(dto.codigoLote());
        lote.setFechaVencimiento(dto.fechaVencimiento());
        lote = loteRepository.save(lote);

        // 3. Crear Existencia inicial
        Existencia existencia = new Existencia();
        ExistenciaId id = new ExistenciaId(dto.idBodega() != null ? dto.idBodega() : 1L, producto.getId(), lote.getId());
        existencia.setId(id);
        existencia.setStockActual(dto.stock());
        existenciaRepository.save(existencia);
    }

    // UPDATE: Ajustar Stock (Entradas y Salidas)
    @Transactional
    public void ajustarStock(AjusteStockDTO dto) {
        ExistenciaId id = new ExistenciaId(dto.idBodega(), dto.idProducto(), dto.idLote());
        Existencia existencia = existenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Existencia no encontrada"));

        if ("Entrada".equalsIgnoreCase(dto.tipoMovimiento())) {
            existencia.setStockActual(existencia.getStockActual() + dto.cantidad());
        } else if ("Salida".equalsIgnoreCase(dto.tipoMovimiento())) {
            if (existencia.getStockActual() < dto.cantidad()) {
                throw new RuntimeException("Violación: No hay stock suficiente para la salida.");
            }
            existencia.setStockActual(existencia.getStockActual() - dto.cantidad());
        }

        existenciaRepository.save(existencia);
    }
}