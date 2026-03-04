import { Routes } from '@angular/router';
import { Dashboard } from './dashboard/dashboard';
import { Ventas } from './ventas/ventas';
import { Inventario } from './inventario/inventario';
import { Reportes } from './reportes/reportes';

export const routes: Routes = [
  // 1. Cuando la ruta esté vacía (localhost:4200/), redirigir al dashboard
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  
  // 2. Las rutas normales de tus módulos
  { path: 'dashboard', component: Dashboard },
  { path: 'ventas', component: Ventas },
  { path: 'inventario', component: Inventario },
  { path: 'reportes', component: Reportes },

  // 3. Por si alguien escribe una ruta que no existe (ej. localhost:4200/perro)
  { path: '**', redirectTo: 'dashboard' } 
];