import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.html',
})
export class App {
  // Lógica de navegación
  vistaActual: string = 'dashboard';

  cambiarVista(vista: string) {
    this.vistaActual = vista;
  }

  // ==========================================
  // DATOS ESTÁTICOS (MOCK DATA) BASADOS EN TU SQL
  // ==========================================
  
  // Tabla: clientes
  clientes = [
    { id: 1, nombre: 'Supermercado El Sol', canal: 'Supermercado', limite_credito: 5000.00, saldo_actual: 1500.00, id_ruta: 1 },
    { id: 2, nombre: 'Farmacia Salud', canal: 'Farmacia', limite_credito: 800.00, saldo_actual: 0.00, id_ruta: 2 }
  ];

  // Tablas unidas: productos + lotes + existencias
  productosInventario = [
    { id_producto: 1, id_lote: 101, nombre: 'Arroz Premium 5kg', precio_base: 6.50, stock_actual: 50, vencimiento: '2026-12-01' },
    { id_producto: 2, id_lote: 102, nombre: 'Aceite Vegetal 1L', precio_base: 2.25, stock_actual: 5, vencimiento: '2026-05-15' } // Ojo: Stock bajo
  ];

  // ==========================================
  // LÓGICA DEL CARRITO DE VENTAS
  // ==========================================
  clienteVentaSeleccionado: any = null;
  carrito: any[] = [];
  esCredito: boolean = false;

  seleccionarCliente(cliente: any) {
    this.clienteVentaSeleccionado = cliente;
  }

  agregarAlCarrito(producto: any) {
    // Detalle lógico: No dejar agregar si no hay stock (Regla de tu SQL)
    if (producto.stock_actual <= 0) {
      alert('Error lógico: No puedes agregar productos sin stock.');
      return;
    }

    const itemExistente = this.carrito.find(item => item.id_producto === producto.id_producto);
    
    if (itemExistente) {
      if(itemExistente.cantidad < producto.stock_actual) {
        itemExistente.cantidad++;
        itemExistente.subtotal = itemExistente.cantidad * itemExistente.precio_base;
      } else {
        alert('Has alcanzado el límite de stock para este lote.');
      }
    } else {
      this.carrito.push({
        ...producto,
        cantidad: 1,
        subtotal: producto.precio_base
      });
    }
  }

  eliminarDelCarrito(index: number) {
    this.carrito.splice(index, 1);
  }

  get totalVenta() {
    return this.carrito.reduce((acc, item) => acc + item.subtotal, 0);
  }

  facturar() {
    if (!this.clienteVentaSeleccionado || this.carrito.length === 0) {
      alert('Debes seleccionar un cliente y tener productos en el carrito.');
      return;
    }
    // Lógica para validar crédito según tu SQL
    if (this.esCredito && (this.clienteVentaSeleccionado.saldo_actual + this.totalVenta > this.clienteVentaSeleccionado.limite_credito)) {
      alert(`Violación: El cliente excede su límite de crédito. Disponible: $${this.clienteVentaSeleccionado.limite_credito - this.clienteVentaSeleccionado.saldo_actual}`);
      return;
    }

    alert('¡Venta facturada con éxito en el Frontend! (Lista para enviar a Supabase)');
    this.carrito = [];
    this.clienteVentaSeleccionado = null;
    this.esCredito = false;
  }
}