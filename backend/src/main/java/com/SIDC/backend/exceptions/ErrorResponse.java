package com.SIDC.backend.exceptions;

import java.time.LocalDateTime;

public record ErrorResponse(
        String mensaje,
        int status,
        LocalDateTime timestamp
) {}