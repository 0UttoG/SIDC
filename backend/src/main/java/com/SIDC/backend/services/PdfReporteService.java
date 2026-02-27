package com.SIDC.backend.services;

import com.SIDC.backend.dto.ReporteVencimientoDTO;
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

    public void exportarVencimientosPdf(HttpServletResponse response, List<ReporteVencimientoDTO> datos) throws IOException {
        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, response.getOutputStream());

        documento.open();

        // Título
        Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fuenteTitulo.setSize(18);
        fuenteTitulo.setColor(Color.DARK_GRAY);
        Paragraph titulo = new Paragraph("Reporte de Productos Próximos a Vencer", fuenteTitulo);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        documento.add(titulo);
        documento.add(new Paragraph(" ")); // Espacio en blanco

        // Tabla de 4 columnas (igual a tu Angular)
        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{3.5f, 1.5f, 2.5f, 2.5f});

        // Encabezados
        String[] encabezados = {"Producto", "Lote", "Fecha Vencimiento", "Días Restantes"};
        for (String tituloColumna : encabezados) {
            PdfPCell celda = new PdfPCell(new Phrase(tituloColumna, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE)));
            celda.setBackgroundColor(new Color(148, 0, 211)); // Color morado estilo tu UI
            celda.setPadding(8);
            tabla.addCell(celda);
        }

        // Datos
        for (ReporteVencimientoDTO item : datos) {
            tabla.addCell(item.producto());
            tabla.addCell(item.lote());
            tabla.addCell(item.fechaVencimiento().toString());
            tabla.addCell(item.diasRestantes() + " días");
        }

        documento.add(tabla);
        documento.close();
    }
}