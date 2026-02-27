package com.SIDC.backend.dto;

public record AjusteStockDTO(
        Long idBodega,
        Long idProducto,
        Long idLote,
        String tipoMovimiento, // "Entrada" o "Salida"
        Integer cantidad
) {}