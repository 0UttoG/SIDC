package com.SIDC.backend.dto;
import java.time.LocalDate;

public record ReporteVencimientoDTO(
        String producto,
        String lote,
        LocalDate fechaVencimiento,
        Long diasRestantes
) {}