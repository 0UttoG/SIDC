import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

// ==========================================
// DTOs (Modelos de Datos)
// ==========================================

export interface ProductoVentaDTO {
  idBodega: number;
  idProducto: number;
  idLote: number;
  nombre: string;
  lote: string;
  vencimiento: string;
  stock: number;
  precio: number;
}

export interface DetalleVentaDTO {
  idBodega: number;
  idProducto: number;
  idLote: number;
  cantidad: number;
  precioUnitario: number;
}

export interface VentaRequestDTO {
  idCliente: number;
  idVendedor: number;
  idRuta: number;
  esCredito: boolean;
  detalles: DetalleVentaDTO[];
}

// ==========================================
// SERVICIO DE VENTAS
// ==========================================

@Injectable({
  providedIn: 'root'
})
export class VentasService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api';

  // PASO 1: Obtener el catálogo real filtrado
  getCatalogo(): Observable<ProductoVentaDTO[]> {
    return this.http.get<ProductoVentaDTO[]>(`${this.baseUrl}/inventario/catalogo-ventas`);
  }

  // PASO 2: Procesar la Facturación
  procesarVenta(venta: VentaRequestDTO): Observable<any> {
    return this.http.post(`${this.baseUrl}/ventas`, venta).pipe(
      catchError(this.manejarError)
    );
  }

  // Capturador de Errores (Como pidió Nestor)
  private manejarError(error: HttpErrorResponse) {
    let mensajeError = 'Error desconocido en el servidor';
    if (error.error instanceof ErrorEvent) {
      // Error del lado del cliente
      mensajeError = `Error: ${error.error.message}`;
    } else {
      // Error del lado del backend (ej. 400 Violación de Ruta o Trigger)
      if (error.status === 400 && error.error && error.error.mensaje) {
        mensajeError = error.error.mensaje;
      } else {
        mensajeError = `Código de error: ${error.status}\nMensaje: ${error.message}`;
      }
    }
    return throwError(() => new Error(mensajeError));
  }
}