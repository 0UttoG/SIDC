import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Dashboard } from './dashboard/dashboard';
import { Ventas } from './ventas/ventas';
import { Inventario } from './inventario/inventario';
import { Reportes } from './reportes/reportes'; // <-- Nueva importación

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, Dashboard, Ventas, Inventario, Reportes], // <-- Añadido aquí
  templateUrl: './app.html',
})
export class App {
  vistaActual: string = 'reportes'; // Que cargue reportes de una vez al entrar

  cambiarVista(vista: string) {
    this.vistaActual = vista;
  }
}