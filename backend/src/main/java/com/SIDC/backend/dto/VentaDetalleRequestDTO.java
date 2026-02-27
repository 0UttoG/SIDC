package com.SIDC.backend.dto;

import java.math.BigDecimal;

public record VentaDetalleRequestDTO(
        Long idBodega,
        Long idProducto,
        Long idLote,
        Integer cantidad,
        BigDecimal precioUnitario
) {}