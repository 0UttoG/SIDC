// Archivo: src/main/java/com/SIDC/backend/services/PdfReporteService.java
package com.SIDC.backend.services;

import com.SIDC.backend.dto.ReporteMorosoDTO;
import com.SIDC.backend.dto.ReporteVencimientoDTO;
import com.SIDC.backend.dto.ReporteVentasRutaDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

@Service
public class PdfReporteService {

    private final Color COLOR_MORADO = new Color(148, 0, 211);

    // 1. REPORTE DE VENCIMIENTOS
    public void exportarVencimientosPdf(HttpServletResponse response, List<ReporteVencimientoDTO> datos) throws IOException {
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, response.getOutputStream());
        documento.open();

        agregarTitulo(documento, "Reporte de Productos Próximos a Vencer");

        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{3.5f, 1.5f, 2.5f, 2.5f});

        String[] encabezados = {"Producto", "Lote", "Fecha Vencimiento", "Días Restantes"};
        agregarEncabezados(tabla, encabezados);

        for (ReporteVencimientoDTO item : datos) {
            tabla.addCell(item.producto());
            tabla.addCell(item.lote());
            tabla.addCell(item.fechaVencimiento().toString());
            tabla.addCell(item.diasRestantes() + " días");
        }

        documento.add(tabla);
        documento.close();
    }

    // 2. REPORTE DE VENTAS POR RUTA
    public void exportarVentasRutaPdf(HttpServletResponse response, List<ReporteVentasRutaDTO> datos) throws IOException {
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, response.getOutputStream());
        documento.open();

        agregarTitulo(documento, "Reporte de Ventas por Ruta");

        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{3.0f, 3.0f, 2.0f, 2.0f});

        String[] encabezados = {"Ruta", "Vendedor", "Cant. Pedidos", "Total Generado"};
        agregarEncabezados(tabla, encabezados);

        for (ReporteVentasRutaDTO item : datos) {
            tabla.addCell(item.rutaZona());
            tabla.addCell(item.vendedorAsignado());
            tabla.addCell(String.valueOf(item.cantPedidos()));
            tabla.addCell("$" + item.totalVendido());
        }

        documento.add(tabla);
        documento.close();
    }

    // 3. REPORTE DE CLIENTES MOROSOS
    public void exportarMorososPdf(HttpServletResponse response, List<ReporteMorosoDTO> datos) throws IOException {
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, response.getOutputStream());
        documento.open();

        agregarTitulo(documento, "Reporte de Clientes Morosos");

        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{3.5f, 2.0f, 2.5f, 2.0f});

        String[] encabezados = {"Cliente", "Límite de Crédito", "Saldo Adeudado", "Días de Mora"};
        agregarEncabezados(tabla, encabezados);

        for (ReporteMorosoDTO item : datos) {
            tabla.addCell(item.cliente());
            tabla.addCell("$" + item.limiteCredito());
            tabla.addCell("$" + item.saldoActual());
            tabla.addCell(String.valueOf(item.moraDias()));
        }

        documento.add(tabla);
        documento.close();
    }

    // --- MÉTODOS AUXILIARES PARA NO REPETIR CÓDIGO ---

    private void agregarTitulo(Document documento, String textoTitulo) throws DocumentException {
        Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fuenteTitulo.setSize(18);
        fuenteTitulo.setColor(Color.DARK_GRAY);
        Paragraph titulo = new Paragraph(textoTitulo, fuenteTitulo);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        documento.add(titulo);
        documento.add(new Paragraph(" ")); // Espacio en blanco
    }

    private void agregarEncabezados(PdfPTable tabla, String[] encabezados) {
        for (String tituloColumna : encabezados) {
            PdfPCell celda = new PdfPCell(new Phrase(tituloColumna, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE)));
            celda.setBackgroundColor(COLOR_MORADO);
            celda.setPadding(8);
            tabla.addCell(celda);
        }
    }
}