import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router'; // <-- Traemos el Router oficial

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterModule], // <-- Ya no importamos las vistas 1x1, el Router se encarga
  templateUrl: './app.html',
})
export class App {
  private router = inject(Router);

  // Método mágico para pintar tu menú según la URL real
  esRutaActiva(ruta: string): boolean {
    return this.router.url.includes(ruta);
  }

  // Método para cambiar el título de arriba automáticamente
  get tituloActual(): string {
    if (this.esRutaActiva('/ventas')) return 'Nueva Venta';
    if (this.esRutaActiva('/inventario')) return 'Control de Inventario y Lotes';
    if (this.esRutaActiva('/reportes')) return 'Generación de Reportes';
    return 'Resumen General'; // Dashboard por defecto
  }
}