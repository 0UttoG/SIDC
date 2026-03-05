// Archivo: src/main/java/com/SIDC/backend/dto/AlertaStockDTO.java
package com.SIDC.backend.dto;

public record AlertaStockDTO(String nombreProducto, Integer stockActual, Integer stockMinimo) {}