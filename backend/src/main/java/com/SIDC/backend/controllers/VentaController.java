// Archivo: src/main/java/com/SIDC/backend/controllers/VentaController.java
package com.SIDC.backend.controllers;

import com.SIDC.backend.dto.VentaRequestDTO;
import com.SIDC.backend.entities.Venta;
import com.SIDC.backend.services.VentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "http://localhost:4200")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    public ResponseEntity<?> crearVenta(@RequestBody VentaRequestDTO request) {
        try {
            Venta nuevaVenta = ventaService.procesarVenta(request);
            return new ResponseEntity<>(java.util.Map.of(
                    "mensaje", "Factura generada con éxito",
                    "total", nuevaVenta.getTotal()
            ), HttpStatus.CREATED);
        } catch (Exception e) {
            // Tu GlobalExceptionHandler se encargará de limpiar el mensaje del Trigger
            throw e;
        }
    }
}