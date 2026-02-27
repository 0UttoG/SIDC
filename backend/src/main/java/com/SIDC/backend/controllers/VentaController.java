package com.SIDC.backend.controllers;

import com.SIDC.backend.dto.VentaRequestDTO;
import com.SIDC.backend.entities.Venta;
import com.SIDC.backend.services.VentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "http://localhost:4200") // Permite peticiones desde el Angular de tu compa
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    public ResponseEntity<Venta> crearVenta(@RequestBody VentaRequestDTO request) {
        Venta nuevaVenta = ventaService.procesarVenta(request);
        return new ResponseEntity<>(nuevaVenta, HttpStatus.CREATED);
    }
}