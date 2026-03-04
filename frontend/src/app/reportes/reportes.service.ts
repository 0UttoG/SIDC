import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// 1. Interfaz: Ventas por Ruta
export interface ReporteVentasRuta {
  rutaZona: string;
  vendedorAsignado: string;
  cantPedidos: number;
  totalVendido: number;
}

// 2. Interfaz: Próximos a Vencer
export interface ReporteVencimiento {
  producto: string;
  lote: string;
  fechaVencimiento: string;
  diasRestantes: number;
}

// 3. Interfaz: Clientes Morosos
export interface ReporteMoroso {
  cliente: string;
  limiteCredito: number;
  saldoActual: number;
  moraDias: number;
}

@Injectable({
  providedIn: 'root'
})
export class ReportesService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/reportes';

  // ==========================================
  // DATOS PARA LAS TABLAS (JSON)
  // ==========================================
  getVentasPorRuta(): Observable<ReporteVentasRuta[]> {
    return this.http.get<ReporteVentasRuta[]>(`${this.baseUrl}/ventas-ruta`);
  }

  getProductosPorVencer(): Observable<ReporteVencimiento[]> {
    return this.http.get<ReporteVencimiento[]>(`${this.baseUrl}/vencimientos`);
  }

  getClientesMorosos(): Observable<ReporteMoroso[]> {
    return this.http.get<ReporteMoroso[]>(`${this.baseUrl}/morosos`);
  }

  // ==========================================
  // DESCARGA DE ARCHIVOS (BLOB)
  // ==========================================
  getVentasPorRutaPDF(): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/ventas-ruta/pdf`, { responseType: 'blob' });
  }

  getProductosPorVencerPDF(): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/vencimientos/pdf`, { responseType: 'blob' });
  }

  getClientesMorososPDF(): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/morosos/pdf`, { responseType: 'blob' });
  }
}