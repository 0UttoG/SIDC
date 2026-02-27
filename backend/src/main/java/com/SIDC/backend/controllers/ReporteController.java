package com.SIDC.backend.controllers;

import com.SIDC.backend.dto.*;
import com.SIDC.backend.services.PdfReporteService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "http://localhost:4200")
public class ReporteController {

    private final PdfReporteService pdfReporteService;

    public ReporteController(PdfReporteService pdfReporteService) {
        this.pdfReporteService = pdfReporteService;
    }

    // 1. ENDPOINTS PARA LLENAR LAS TABLAS DE ANGULAR (JSON)

    @GetMapping("/vencimientos")
    public ResponseEntity<List<ReporteVencimientoDTO>> obtenerVencimientos() {
        // Simulando datos exactos de tu captura de pantalla
        List<ReporteVencimientoDTO> datos = List.of(
                new ReporteVencimientoDTO("Leche Semidescremada 1L", "L-204", LocalDate.now().plusDays(6), 6L),
                new ReporteVencimientoDTO("Queso Crema 200g", "L-501", LocalDate.now().plusDays(13), 13L),
                new ReporteVencimientoDTO("Yogurt Natural 500ml", "L-102", LocalDate.now().plusDays(26), 26L)
        );
        return ResponseEntity.ok(datos);
    }

    @GetMapping("/ventas-ruta")
    public ResponseEntity<List<ReporteVentasRutaDTO>> obtenerVentasPorRuta() {
        List<ReporteVentasRutaDTO> datos = List.of(
                new ReporteVentasRutaDTO("Ruta 01 - Centro", "Juan Pérez", 12, new BigDecimal("1250.75")),
                new ReporteVentasRutaDTO("Ruta 02 - Norte", "María López", 8, new BigDecimal("890.20")),
                new ReporteVentasRutaDTO("Ruta 03 - Sur", "Carlos Ruiz", 15, new BigDecimal("2100.50"))
        );
        return ResponseEntity.ok(datos);
    }

    @GetMapping("/morosos")
    public ResponseEntity<List<ReporteMorosoDTO>> obtenerMorosos() {
        List<ReporteMorosoDTO> datos = List.of(
                new ReporteMorosoDTO("Supermercado El Sol", new BigDecimal("5000.00"), new BigDecimal("1500.00"), 5),
                new ReporteMorosoDTO("Abarrotes Don Julio", new BigDecimal("1000.00"), new BigDecimal("950.00"), 12)
        );
        return ResponseEntity.ok(datos);
    }

    // 2. ENDPOINT PARA DESCARGAR EL PDF (Ejemplo para Vencimientos)

    @GetMapping("/vencimientos/pdf")
    public void descargarPdfVencimientos(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=reporte_vencimientos_" + LocalDate.now() + ".pdf";
        response.setHeader(headerKey, headerValue);

        // Reutilizamos los datos del endpoint JSON
        List<ReporteVencimientoDTO> datos = obtenerVencimientos().getBody();
        pdfReporteService.exportarVencimientosPdf(response, datos);
    }
}