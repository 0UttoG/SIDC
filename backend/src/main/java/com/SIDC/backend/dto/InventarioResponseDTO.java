package com.SIDC.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InventarioResponseDTO(
        Long idBodega,
        Long idProducto,
        Long idLote,
        String nombreProducto,
        String codigoLote,
        Integer stock,
        LocalDate fechaVencimiento,
        String estado,
        BigDecimal precioBase // <-- NUEVO CAMPO
) {}