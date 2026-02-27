package com.SIDC.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record NuevoLoteDTO(
        String nombreProducto,
        String codigoLote,
        BigDecimal precio,
        Integer stock,
        LocalDate fechaVencimiento,
        Long idBodega,
        Long idCategoria // Agregado para soportar el selector de categorías en Angular
) {}