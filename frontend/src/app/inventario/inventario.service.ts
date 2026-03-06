import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface InventarioResponseDTO {
  idBodega: number;
  idProducto: number;
  idLote: number;
  producto: string;
  lote: string;
  stockActual: number;
  vencimiento: string;
  estado: string;
  precio?: number; // Lo agregamos para que el modal lo reconozca
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

// Actualizamos la interfaz para que coincida con el PUT de Nestor
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

  // NUEVO: Método para el PUT completo
  actualizarProducto(id: number, data: ActualizarProductoDTO): Observable<any> {
    return this.http.put(`${this.apiUrl}/productos/${id}`, data);
  }

  // NUEVO: Método para el DELETE Lógico
  eliminarProducto(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/productos/${id}`);
  }
}