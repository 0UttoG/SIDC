import { Component, OnInit, inject } from '@angular/core';
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

  // Aquí guardamos los datos que manda Nestor
  ventasRuta: ReporteVentasRuta[] = [];
  vencimientos: ReporteVencimiento[] = [];
  morosos: ReporteMoroso[] = [];

  // ⭐ Corregido: Ahora se llama igual que en tu HTML y arranca en 'rutas'
  reporteActivo: string = 'rutas'; 

  ngOnInit() {
    this.cargarTodosLosReportes();
  }

  cargarTodosLosReportes() {
    // 1. Cargar Ventas por Ruta
    this.reportesService.getVentasPorRuta().subscribe({
      next: (data) => this.ventasRuta = data,
      error: (err) => console.error('Error al cargar ventas por ruta:', err)
    });

    // 2. Cargar Próximos a Vencer
    this.reportesService.getProductosPorVencer().subscribe({
      next: (data) => this.vencimientos = data,
      error: (err) => console.error('Error al cargar vencimientos:', err)
    });

    // 3. Cargar Morosos
    this.reportesService.getClientesMorosos().subscribe({
      next: (data) => this.morosos = data,
      error: (err) => console.error('Error al cargar morosos:', err)
    });
  }

  // ⭐ Funcionalidad Estrella: Descarga de PDF
  descargarPDF() {
    // Abre la ruta del backend en una pestaña nueva para forzar la descarga
    window.open('http://localhost:8080/api/reportes/vencimientos/pdf', '_blank');
  }

  // ⭐ Corregido: Ahora se llama igual que en tu HTML
  cambiarReporte(reporte: string) {
    this.reporteActivo = reporte;
  }
}