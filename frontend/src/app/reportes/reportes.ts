import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReportesService, ReporteVentasRuta, ReporteVencimiento, ReporteMoroso } from './reportes.service';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reportes.html'
})
export class Reportes implements OnInit {
  
  private reportesService = inject(ReportesService);
  private cdr = inject(ChangeDetectorRef);

  ventasRuta: ReporteVentasRuta[] = [];
  vencimientos: ReporteVencimiento[] = [];
  morosos: ReporteMoroso[] = [];

  reporteActivo: string = 'rutas'; 

  // Estados para bloquear los botones mientras se genera el PDF
  descargandoRutas: boolean = false;
  descargandoVencimientos: boolean = false;
  descargandoMorosos: boolean = false;

  // ==========================================
  // LÓGICA DE UI/UX: Toast Notification
  // ==========================================
  toastVisible: boolean = false;
  toastMensaje: string = '';
  toastTipo: 'success' | 'error' | 'warning' = 'success';

  mostrarNotificacion(mensaje: string, tipo: 'success' | 'error' | 'warning') {
    this.toastMensaje = mensaje;
    this.toastTipo = tipo;
    this.toastVisible = true;
    this.cdr.detectChanges();
    setTimeout(() => { this.toastVisible = false; this.cdr.detectChanges(); }, 3000);
  }

  ngOnInit() {
    this.cargarTodosLosReportes();
  }

  cargarTodosLosReportes() {
    this.reportesService.getVentasPorRuta().subscribe({
      next: (data) => {
        this.ventasRuta = data;
        this.cdr.detectChanges(); // 🌟 LA MAGIA: Despierta a Angular para que muestre la tabla de Rutas
      },
      error: (err) => console.error('Error al cargar ventas por ruta:', err)
    });

    this.reportesService.getProductosPorVencer().subscribe({
      next: (data) => {
        this.vencimientos = data;
        this.cdr.detectChanges(); // 🌟 Lo mismo para Vencimientos
      },
      error: (err) => console.error('Error al cargar vencimientos:', err)
    });

    this.reportesService.getClientesMorosos().subscribe({
      next: (data) => {
        this.morosos = data;
        this.cdr.detectChanges(); // 🌟 Lo mismo para Morosos
      },
      error: (err) => console.error('Error al cargar morosos:', err)
    });
  }

  cambiarReporte(reporte: string) {
    this.reporteActivo = reporte;
  }

  // ==========================================
  // LÓGICA DE DESCARGA DE PDFs (Con Blob)
  // ==========================================

  descargarPDFRutas() {
    if (this.descargandoRutas) return;
    this.descargandoRutas = true;
    this.mostrarNotificacion('Generando PDF de Rutas...', 'warning');

    this.reportesService.getVentasPorRutaPDF().subscribe({
      next: (blob) => {
        this.procesarDescarga(blob, 'Reporte_Ventas_Por_Ruta.pdf');
        this.descargandoRutas = false;
      },
      error: (err) => {
        this.mostrarNotificacion('Error al generar el PDF de Rutas', 'error');
        this.descargandoRutas = false;
      }
    });
  }

  descargarPDFVencimientos() {
    if (this.descargandoVencimientos) return;
    this.descargandoVencimientos = true;
    this.mostrarNotificacion('Generando PDF de Vencimientos...', 'warning');

    this.reportesService.getProductosPorVencerPDF().subscribe({
      next: (blob) => {
        this.procesarDescarga(blob, 'Reporte_Proximos_A_Vencer.pdf');
        this.descargandoVencimientos = false;
      },
      error: (err) => {
        this.mostrarNotificacion('Error al generar el PDF de Vencimientos', 'error');
        this.descargandoVencimientos = false;
      }
    });
  }

  descargarPDFMorosos() {
    if (this.descargandoMorosos) return;
    this.descargandoMorosos = true;
    this.mostrarNotificacion('Generando PDF de Morosos...', 'warning');

    this.reportesService.getClientesMorososPDF().subscribe({
      next: (blob) => {
        this.procesarDescarga(blob, 'Reporte_Clientes_Morosos.pdf');
        this.descargandoMorosos = false;
      },
      error: (err) => {
        this.mostrarNotificacion('Error al generar el PDF de Morosos', 'error');
        this.descargandoMorosos = false;
      }
    });
  }

  // Función reutilizable que crea la etiqueta <a> oculta y fuerza la descarga
  private procesarDescarga(blob: Blob, nombreArchivo: string) {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = nombreArchivo;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
    this.mostrarNotificacion(`¡${nombreArchivo} descargado!`, 'success');
  }
}