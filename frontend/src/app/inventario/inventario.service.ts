import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// 🌟 INTERFAZ ACTUALIZADA CON LOS NUEVOS NOMBRES DE NESTOR
export interface InventarioResponseDTO {
  idBodega: number;
  idProducto: number;
  idLote: number;
  nombreProducto: string; // Antes era 'producto'
  codigoLote: string;     // Antes era 'lote'
  stock: number;          // Antes era 'stockActual'
  fechaVencimiento: string; // Antes era 'vencimiento'
  estado: string;
  precioBase: number;     // 🌟 ¡Al fin llegó el precio!
}

export interface NuevoLoteDTO {
  nombreProducto: string;
  codigoLote: string;
  precio: number;
  stock: number;
  fechaVencimiento: string;
  idBodega: number;
  idCategoria: number;
}

export interface ActualizarProductoDTO {
  idLote: number;
  idBodega: number;
  nombreProducto: string;
  codigoLote: string;
  precio: number;
  stock: number;
  fechaVencimiento: string;
}

@Injectable({
  providedIn: 'root'
})
export class InventarioService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/inventario';

  getExistencias(): Observable<InventarioResponseDTO[]> {
    return this.http.get<InventarioResponseDTO[]>(`${this.apiUrl}/existencias`);
  }

  registrarLote(lote: NuevoLoteDTO): Observable<any> {
    return this.http.post(`${this.apiUrl}/lotes`, lote);
  }

  actualizarProducto(id: number, data: ActualizarProductoDTO): Observable<any> {
    return this.http.put(`${this.apiUrl}/productos/${id}`, data);
  }

  eliminarProducto(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/productos/${id}`);
  }
}