package com.SIDC.backend.dto;

import java.time.LocalDate;

public record InventarioResponseDTO(
        Long idBodega,
        Long idProducto,
        Long idLote,
        String producto,
        String lote,
        Integer stockActual,
        LocalDate vencimiento,
        String estado // Aquí enviaremos "Stock Óptimo", "Bajo Stock", etc.
) {}