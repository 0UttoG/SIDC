// Archivo: src/main/java/com/SIDC/backend/dto/FacturaRecienteDTO.java
package com.SIDC.backend.dto;

import java.math.BigDecimal;

public record FacturaRecienteDTO(Long idVenta, String cliente, BigDecimal total, String estadoPago) {}