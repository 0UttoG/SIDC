// Archivo: src/main/java/com/SIDC/backend/controllers/ReporteController.java
package com.SIDC.backend.controllers;

import com.SIDC.backend.dto.*;
import com.SIDC.backend.repositories.ReporteRepository;
import com.SIDC.backend.services.PdfReporteService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "http://localhost:4200")
public class ReporteController {

    private final PdfReporteService pdfReporteService;
    private final ReporteRepository reporteRepository; // 👈 Inyectamos el nuevo repositorio

    public ReporteController(PdfReporteService pdfReporteService, ReporteRepository reporteRepository) {
        this.pdfReporteService = pdfReporteService;
        this.reporteRepository = reporteRepository;
    }

    // 1. ENDPOINTS PARA LLENAR LAS TABLAS DE ANGULAR (JSON)

    @GetMapping("/vencimientos")
    public ResponseEntity<List<ReporteVencimientoDTO>> obtenerVencimientos() {
        List<ReporteVencimientoDTO> datos = reporteRepository.obtenerReporteVencimientos().stream()
                .map(row -> {
                    // Conversión segura: extraemos solo "YYYY-MM-DD" para evitar ClassCastException
                    LocalDate fecha = LocalDate.parse(row[2].toString().substring(0, 10));

                    return new ReporteVencimientoDTO(
                            row[0].toString(), // Producto
                            row[1].toString(), // Lote
                            fecha,             // Fecha_Vencimiento
                            ((Number) row[3]).longValue() // Días restantes (será negativo si ya venció)
                    );
                }).collect(Collectors.toList());
        return ResponseEntity.ok(datos);
    }

    @GetMapping("/ventas-ruta")
    public ResponseEntity<List<ReporteVentasRutaDTO>> obtenerVentasPorRuta() {
        List<ReporteVentasRutaDTO> datos = reporteRepository.obtenerReporteVentasRuta().stream()
                .map(row -> new ReporteVentasRutaDTO(
                        (String) row[0], // Ruta
                        (String) row[1], // Vendedor
                        ((Number) row[2]).intValue(), // Cantidad Ventas
                        (BigDecimal) row[3] // Total
                )).collect(Collectors.toList());
        return ResponseEntity.ok(datos);
    }

    @GetMapping("/morosos")
    public ResponseEntity<List<ReporteMorosoDTO>> obtenerMorosos() {
        List<ReporteMorosoDTO> datos = reporteRepository.obtenerReporteMorosos().stream()
                .map(row -> new ReporteMorosoDTO(
                        (String) row[0], // Cliente
                        (BigDecimal) row[1], // Límite
                        (BigDecimal) row[2], // Saldo Actual
                        ((Number) row[3]).intValue() // Facturas Pendientes (o días de atraso)
                )).collect(Collectors.toList());
        return ResponseEntity.ok(datos);
    }

    // 2. ENDPOINTS PARA DESCARGAR LOS PDFS

    @GetMapping("/vencimientos/pdf")
    public void descargarPdfVencimientos(HttpServletResponse response) throws IOException {
        prepararHeadersPdf(response, "reporte_vencimientos_");
        List<ReporteVencimientoDTO> datos = obtenerVencimientos().getBody();
        pdfReporteService.exportarVencimientosPdf(response, datos);
    }

    // (Asegúrate de tener estos métodos en tu PdfReporteService para generar los otros 2 PDFs)
     @GetMapping("/ventas-ruta/pdf")
    public void descargarPdfVentasRuta(HttpServletResponse response) throws IOException {
        prepararHeadersPdf(response, "reporte_ventas_ruta_");
        List<ReporteVentasRutaDTO> datos = obtenerVentasPorRuta().getBody();
        pdfReporteService.exportarVentasRutaPdf(response, datos);
    }

    @GetMapping("/morosos/pdf")
    public void descargarPdfMorosos(HttpServletResponse response) throws IOException {
        prepararHeadersPdf(response, "reporte_morosos_");
        List<ReporteMorosoDTO> datos = obtenerMorosos().getBody();
        pdfReporteService.exportarMorososPdf(response, datos);
    }


    // Método auxiliar para no repetir código
    private void prepararHeadersPdf(HttpServletResponse response, String prefijo) {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + prefijo + LocalDate.now() + ".pdf";
        response.setHeader(headerKey, headerValue);
    }
}