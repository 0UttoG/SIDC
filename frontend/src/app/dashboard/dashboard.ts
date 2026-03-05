import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService, DashboardData } from './dashboard.service';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData, ChartType } from 'chart.js';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './dashboard.html'
})
export class Dashboard implements OnInit {
  private ds = inject(DashboardService);
  private cdr = inject(ChangeDetectorRef);

  data?: DashboardData;

  // ==========================================
  // CONFIGURACIÓN: Gráfico de Pastel (Rutas)
  // ==========================================
  public pieChartOptions: ChartConfiguration['options'] = { 
    responsive: true,
    plugins: { legend: { position: 'right' } }
  };
  public pieChartData: ChartData<'pie', number[], string | string[]> = { 
    labels: [], 
    datasets: [{ data: [] }] 
  };
  public pieChartType: ChartType = 'pie';

  // ==========================================
  // CONFIGURACIÓN: Gráfico de Barras (Top Productos)
  // ==========================================
  public barChartOptions: ChartConfiguration['options'] = { 
    responsive: true,
    scales: { y: { beginAtZero: true } }
  };
  public barChartData: ChartData<'bar'> = { 
    labels: [], 
    datasets: [{ data: [], label: 'Unidades Vendidas', backgroundColor: '#9400d3' }] 
  };
  public barChartType: ChartType = 'bar';

  ngOnInit() {
    this.cargarDashboard();
  }

  cargarDashboard() {
    this.ds.getResumen().subscribe({
      next: (res) => {
        this.data = res;
        this.prepararGraficos();
        this.cdr.detectChanges(); // 🌟 Despierta a Angular al instante
      },
      error: (err) => console.error('Error al cargar el Dashboard:', err)
    });
  }

  prepararGraficos() {
    if (!this.data) return;

    // Llenar datos del Pastel
    this.pieChartData.labels = this.data.ventasPorRuta.map(r => r.nombreRuta);
    this.pieChartData.datasets[0].data = this.data.ventasPorRuta.map(r => r.totalVendido);

    // Llenar datos de las Barras
    this.barChartData.labels = this.data.topProductos.map(p => p.nombreProducto);
    this.barChartData.datasets[0].data = this.data.topProductos.map(p => p.cantidadVendida);
  }
}