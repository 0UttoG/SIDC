// Archivo: src/main/java/com/SIDC/backend/exceptions/GlobalExceptionHandler.java
package com.SIDC.backend.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Atrapa los errores de los Triggers de PostgreSQL
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String mensajeError = "Error de validación en la base de datos.";

        // Extraer el mensaje específico del trigger de PostgreSQL
        Throwable rootCause = ex.getRootCause();

        if (rootCause instanceof SQLException sqlEx) {
            String sqlMessage = sqlEx.getMessage();

            // Buscamos la palabra clave "Violación:" que pusiste en tus triggers
            if (sqlMessage != null && sqlMessage.contains("Violación:")) {
                mensajeError = sqlMessage.substring(sqlMessage.indexOf("Violación:"));
                mensajeError = mensajeError.split("\n")[0]; // Nos quedamos solo con la línea principal
            }
        }

        // Devolvemos un Map para que se convierta en JSON {"mensaje": "..."}
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "mensaje", mensajeError,
                "timestamp", LocalDateTime.now()
        ));
    }

    // 2. Atrapa los errores de lógica de negocio (ej. "Cliente no encontrado")
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "mensaje", ex.getMessage(),
                "timestamp", LocalDateTime.now()
        ));
    }

    // 3. Fallback para cualquier otro error imprevisto (Error 500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "mensaje", "Error interno del servidor: " + ex.getMessage(),
                "timestamp", LocalDateTime.now()
        ));
    }
}