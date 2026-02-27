package com.SIDC.backend.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String mensajeError = "Error de validación en la base de datos.";

        // Extraer el mensaje específico del trigger de PostgreSQL
        Throwable rootCause = ex.getRootCause();

        if (rootCause instanceof SQLException sqlEx) {
            String sqlMessage = sqlEx.getMessage();

            // Buscamos la palabra clave que pusiste en los triggers de PostgreSQL
            if (sqlMessage != null && sqlMessage.contains("Violación:")) {
                mensajeError = sqlMessage.substring(sqlMessage.indexOf("Violación:"));
                mensajeError = mensajeError.split("\n")[0]; // Nos quedamos solo con la línea principal
            }
        }

        ErrorResponse error = new ErrorResponse(
                mensajeError,
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Fallback para cualquier otro error general
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                "Error interno del servidor: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ... tu código anterior ...

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}