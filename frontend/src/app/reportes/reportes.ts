import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reportes.html',
})
export class Reportes {
  // Controla qué reporte se está viendo
  reporteActivo: string = 'ventas'; // puede ser: ventas, stock, morosos

  cambiarReporte(reporte: string) {
    this.reporteActivo = reporte;
  }

  // ==========================================
  // DATOS SIMULADOS PARA LOS 3 REPORTES
  // ==========================================
  ventasMes = [
    { fecha: '2026-02-25', factura: 'FAC-001', cliente: 'Supermercado El Sol', total: 840.50 },
    { fecha: '2026-02-26', factura: 'FAC-002', cliente: 'Farmacia Salud', total: 1100.00 },
    { fecha: '2026-02-27', factura: 'FAC-003', cliente: 'Tienda La Esquina', total: 320.00 }
  ];

  bajoStock = [
    { lote: '102', nombre: 'Aceite Vegetal 1L', stock: 5, minimo: 15 }
  ];

  clientesMorosos = [
    { nombre: 'Supermercado El Sol', limite: 5000.00, saldo: 1500.00 },
    { nombre: 'Abarrotes Don Julio', limite: 1000.00, saldo: 950.00 }
  ];
}