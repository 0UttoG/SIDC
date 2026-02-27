import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reportes.html',
})
export class Reportes {
  reporteActivo: string = 'rutas'; // rutas, vencimiento, morosos

  cambiarReporte(reporte: string) {
    this.reporteActivo = reporte;
  }

  // 1. VENTAS POR RUTA
  ventasPorRuta = [
    { ruta: 'Ruta 01 - Centro', vendedor: 'Juan Pérez', totalVendido: 1250.75, pedidos: 12 },
    { ruta: 'Ruta 02 - Norte', vendedor: 'María López', totalVendido: 890.20, pedidos: 8 },
    { ruta: 'Ruta 03 - Sur', vendedor: 'Carlos Ruiz', totalVendido: 2100.50, pedidos: 15 }
  ];

  // 2. PRODUCTOS PRÓXIMOS A VENCER (Menos de 30 días)
  proximosAVencer = [
    { producto: 'Leche Semidescremada 1L', lote: 'L-204', fechaVencimiento: '2026-03-05', diasRestantes: 6 },
    { producto: 'Queso Crema 200g', lote: 'L-501', fechaVencimiento: '2026-03-12', diasRestantes: 13 },
    { producto: 'Yogurt Natural 500ml', lote: 'L-102', fechaVencimiento: '2026-03-25', diasRestantes: 26 }
  ];

  // 3. CLIENTES MOROSOS
  clientesMorosos = [
    { nombre: 'Supermercado El Sol', limite: 5000.00, saldo: 1500.00, diasMora: 5 },
    { nombre: 'Abarrotes Don Julio', limite: 1000.00, saldo: 950.00, diasMora: 12 }
  ];
}