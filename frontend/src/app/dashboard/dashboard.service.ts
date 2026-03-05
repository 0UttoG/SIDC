import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DashboardData {
  ventasDelDia: number;
  productosPorVencer: number;
  valorInventario: number;
  riesgoCreditoTotal: number;
  rutasActivasHoy: number;
  ventasPorRuta: { nombreRuta: string; totalVendido: number }[];
  topProductos: { nombreProducto: string; cantidadVendida: number }[];
  ultimasFacturas: { idVenta: number; cliente: string; total: number; estadoPago: string }[];
  alertasStock: { nombreProducto: string; stockActual: number; stockMinimo: number }[];
}

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private http = inject(HttpClient);
  private url = 'http://localhost:8080/api/dashboard/resumen';

  getResumen(): Observable<DashboardData> {
    return this.http.get<DashboardData>(this.url);
  }
}