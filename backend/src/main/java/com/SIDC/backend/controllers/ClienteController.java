// Archivo: src/main/java/com/SIDC/backend/controllers/ClienteController.java
package com.SIDC.backend.controllers;

import com.SIDC.backend.entities.Cliente;
import com.SIDC.backend.repositories.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @PostMapping
    public ResponseEntity<?> crearCliente(@RequestBody Cliente cliente) {
        try {
            // Guardamos en la base de datos
            clienteRepository.save(cliente);

            // Retornamos EXACTAMENTE el JSON simple que pidió el frontend
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("mensaje", "Cliente creado con éxito"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "Error al guardar el cliente: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        return ResponseEntity.ok(clienteRepository.findAll());
    }
}