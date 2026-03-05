// Archivo: src/main/java/com/SIDC/backend/dto/ProductoActualizarDTO.java
package com.SIDC.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductoActualizarDTO(
        Long idLote,
        Long idBodega,
        String nombreProducto,
        String codigoLote,
        BigDecimal precio,
        Integer stock,
        LocalDate fechaVencimiento
) {}