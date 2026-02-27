package com.SIDC.backend.controllers;

import com.SIDC.backend.dto.AjusteStockDTO;
import com.SIDC.backend.dto.InventarioResponseDTO;
import com.SIDC.backend.dto.NuevoLoteDTO;
import com.SIDC.backend.entities.Bodega;
import com.SIDC.backend.entities.Categoria;
import com.SIDC.backend.repositories.BodegaRepository;
import com.SIDC.backend.repositories.CategoriaRepository;
import com.SIDC.backend.services.InventarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class InventarioController {

    private final InventarioService inventarioService;
    private final BodegaRepository bodegaRepository;
    private final CategoriaRepository categoriaRepository;

    public InventarioController(InventarioService inventarioService, BodegaRepository bodegaRepository, CategoriaRepository categoriaRepository) {
        this.inventarioService = inventarioService;
        this.bodegaRepository = bodegaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping("/existencias")
    public ResponseEntity<List<InventarioResponseDTO>> obtenerInventario() {
        return ResponseEntity.ok(inventarioService.obtenerListadoInventario());
    }

    @PostMapping("/lotes")
    public ResponseEntity<String> registrarLote(@RequestBody NuevoLoteDTO dto) {
        inventarioService.registrarNuevoLote(dto);
        return new ResponseEntity<>("Lote registrado correctamente", HttpStatus.CREATED);
    }

    @PatchMapping("/ajustes")
    public ResponseEntity<String> ajustarStock(@RequestBody AjusteStockDTO dto) {
        inventarioService.ajustarStock(dto);
        return ResponseEntity.ok("Stock ajustado correctamente");
    }

    // --- NUEVOS ENDPOINTS PARA EL FRONTEND ---

    @GetMapping("/bodegas")
    public ResponseEntity<List<Bodega>> obtenerBodegas() {
        return ResponseEntity.ok(bodegaRepository.findAll());
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<Categoria>> obtenerCategorias() {
        return ResponseEntity.ok(categoriaRepository.findAll());
    }
}