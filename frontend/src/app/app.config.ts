import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideClientHydration } from '@angular/platform-browser';
// 1. Importamos withFetch desde @angular/common/http
import { provideHttpClient, withFetch } from '@angular/common/http';
// 🌟 Importamos la configuración global de los gráficos
import { provideCharts, withDefaultRegisterables } from 'ng2-charts';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideClientHydration(),
    // 2. Le agregamos withFetch() aquí adentro. ¡Esta es la magia!
    provideHttpClient(withFetch()),
    // 3. Registramos los gráficos para que funcionen en toda la app
    provideCharts(withDefaultRegisterables())
  ]
};