package com.SIDC.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductoVentaDTO(
        Long idBodega,
        Long idProducto,
        Long idLote,
        String nombre,
        String lote,
        LocalDate vencimiento,
        Integer stock,
        BigDecimal precio
) {}