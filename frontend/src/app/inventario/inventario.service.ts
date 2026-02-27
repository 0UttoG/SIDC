import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// --- INTERFACES ESTRICTAS (Contratos de Nestor) ---
export interface InventarioResponseDTO {
  idBodega: number;
  idProducto: number;
  idLote: number;
  producto: string;
  lote: string;
  stockActual: number;
  vencimiento: string;
  estado: string;
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

export interface AjusteStockDTO {
  idBodega: number;
  idProducto: number;
  idLote: number;
  tipoMovimiento: string;
  cantidad: number;
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

  ajustarStock(ajuste: AjusteStockDTO): Observable<any> {
    return this.http.patch(`${this.apiUrl}/ajustes`, ajuste);
  }
}