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
    public List<InventarioResponseDTO> obtenerListadoInventario() {
        List<Object[]> resultados = existenciaRepository.obtenerInventarioCompleto();
        List<InventarioResponseDTO> listado = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        for (Object[] fila : resultados) {
            Long idBodega = ((Number) fila[0]).longValue();
            Long idProducto = ((Number) fila[1]).longValue();
            Long idLote = ((Number) fila[2]).longValue();
            String nombre = (String) fila[3];
            String codigoLote = (String) fila[4];
            Integer stock = ((Number) fila[5]).intValue();
            LocalDate vencimiento = ((java.sql.Date) fila[6]).toLocalDate();

            // LÓGICA DE ESTADOS PARA EL FRONTEND
            String estado = "Stock Óptimo";
            long diasParaVencer = ChronoUnit.DAYS.between(hoy, vencimiento);

            if (diasParaVencer <= 30 && diasParaVencer >= 0) {
                estado = "Próximo a Vencer";
            } else if (diasParaVencer < 0) {
                estado = "Vencido";
            } else if (stock <= 10) {
                estado = "Bajo Stock";
            }

            listado.add(new InventarioResponseDTO(idBodega, idProducto, idLote, nombre, codigoLote, stock, vencimiento, estado));
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