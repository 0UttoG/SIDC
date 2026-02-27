import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-inventario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inventario.html',
})
export class Inventario {
  // Datos
  productosInventario = [
    { id_producto: 1, id_lote: '101', nombre: 'Arroz Premium 5kg', precio_base: 6.50, stock_actual: 50, vencimiento: '2026-12-01', estado: 'Bueno' },
    { id_producto: 2, id_lote: '102', nombre: 'Aceite Vegetal 1L', precio_base: 2.25, stock_actual: 5, vencimiento: '2026-05-15', estado: 'Bajo Stock' },
    { id_producto: 3, id_lote: '103', nombre: 'Frijol Rojo 1kg', precio_base: 1.80, stock_actual: 120, vencimiento: '2026-03-10', estado: 'Por Vencer' }
  ];

  // ==========================================
  // MODAL 1: CREAR PRODUCTO (CRUD - C)
  // ==========================================
  mostrarModalProducto: boolean = false;
  nuevoProducto: any = { nombre: '', id_lote: '', precio_base: null, stock_actual: null, vencimiento: '' };

  abrirModalProducto() { this.mostrarModalProducto = true; }
  cerrarModalProducto() { this.mostrarModalProducto = false; this.nuevoProducto = { nombre: '', id_lote: '', precio_base: null, stock_actual: null, vencimiento: '' }; }

  guardarProducto() {
    if (!this.nuevoProducto.nombre || !this.nuevoProducto.id_lote || !this.nuevoProducto.precio_base || !this.nuevoProducto.stock_actual || !this.nuevoProducto.vencimiento) {
      alert('Llena todos los campos obligatorios.'); return;
    }
    let estadoCalc = this.nuevoProducto.stock_actual <= 10 ? 'Bajo Stock' : 'Bueno';
    this.productosInventario.push({
      id_producto: Math.floor(Math.random() * 1000),
      id_lote: this.nuevoProducto.id_lote,
      nombre: this.nuevoProducto.nombre,
      precio_base: this.nuevoProducto.precio_base,
      stock_actual: this.nuevoProducto.stock_actual,
      vencimiento: this.nuevoProducto.vencimiento,
      estado: estadoCalc
    });
    alert('¡Producto creado exitosamente!');
    this.cerrarModalProducto();
  }

  // ==========================================
  // MODAL 2: AJUSTAR STOCK (CRUD - U)
  // ==========================================
  mostrarModalAjuste: boolean = false;
  productoAAjustar: any = null;
  tipoMovimiento: string = 'entrada';
  cantidadAjuste: number = 0;

  abrirModalAjuste(producto: any) {
    this.productoAAjustar = producto;
    this.cantidadAjuste = 0;
    this.tipoMovimiento = 'entrada';
    this.mostrarModalAjuste = true;
  }

  cerrarModalAjuste() {
    this.mostrarModalAjuste = false;
    this.productoAAjustar = null;
  }

  guardarAjuste() {
    if (this.cantidadAjuste <= 0) { alert('La cantidad debe ser mayor a 0'); return; }
    if (this.tipoMovimiento === 'salida' && this.cantidadAjuste > this.productoAAjustar.stock_actual) {
      alert('Error: No puedes sacar más stock del que existe.'); return;
    }

    if (this.tipoMovimiento === 'entrada') {
      this.productoAAjustar.stock_actual += this.cantidadAjuste;
    } else {
      this.productoAAjustar.stock_actual -= this.cantidadAjuste;
    }
    this.productoAAjustar.estado = this.productoAAjustar.stock_actual <= 10 ? 'Bajo Stock' : 'Bueno';
    
    alert(`¡Inventario actualizado!`);
    this.cerrarModalAjuste();
  }
}