import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Cliente {
  id_cliente?: number;
  nombre: string;
  direccion: string;
  telefono: string;
  correo: string; // <--- Aquí está el campo que pidió Nestor
  id_ruta: number;
  limite_credito: number;
}

@Injectable({
  providedIn: 'root'
})
export class VentasService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/clientes'; // Ajustar según diga Nestor luego

  getClientes(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(this.apiUrl);
  }

  crearCliente(cliente: Cliente): Observable<Cliente> {
    return this.http.post<Cliente>(this.apiUrl, cliente);
  }
}