import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-ventas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ventas.html',
})
export class Ventas {
  // Datos estáticos
  clientes = [
    { id: 1, nombre: 'Supermercado El Sol', canal: 'Supermercado', limite_credito: 5000.00, saldo_actual: 1500.00 },
    { id: 2, nombre: 'Farmacia Salud', canal: 'Farmacia', limite_credito: 800.00, saldo_actual: 0.00 }
  ];

  productosInventario = [
    { id_producto: 1, id_lote: '101', nombre: 'Arroz Premium 5kg', precio_base: 6.50, stock_actual: 50, vencimiento: '2026-12-01' },
    { id_producto: 2, id_lote: '102', nombre: 'Aceite Vegetal 1L', precio_base: 2.25, stock_actual: 5, vencimiento: '2026-05-15' },
    { id_producto: 3, id_lote: '103', nombre: 'Frijol Rojo 1kg', precio_base: 1.80, stock_actual: 120, vencimiento: '2026-03-10' }
  ];

  // Lógica del Carrito
  clienteVentaSeleccionado: any = null;
  carrito: any[] = [];
  esCredito: boolean = false;

  seleccionarCliente(cliente: any) { this.clienteVentaSeleccionado = cliente; }

  agregarAlCarrito(producto: any) {
    if (producto.stock_actual <= 0) { alert('Error: Sin stock.'); return; }
    const itemExistente = this.carrito.find(item => item.id_producto === producto.id_producto);
    if (itemExistente) {
      if(itemExistente.cantidad < producto.stock_actual) {
        itemExistente.cantidad++;
        itemExistente.subtotal = itemExistente.cantidad * itemExistente.precio_base;
      } else { alert('Límite de stock alcanzado.'); }
    } else {
      this.carrito.push({ ...producto, cantidad: 1, subtotal: producto.precio_base });
    }
  }

  eliminarDelCarrito(index: number) { this.carrito.splice(index, 1); }

  get totalVenta() { return this.carrito.reduce((acc, item) => acc + item.subtotal, 0); }

  facturar() {
    if (!this.clienteVentaSeleccionado || this.carrito.length === 0) { alert('Selecciona cliente y productos.'); return; }
    if (this.esCredito && (this.clienteVentaSeleccionado.saldo_actual + this.totalVenta > this.clienteVentaSeleccionado.limite_credito)) {
      alert(`Error: Cliente excede límite de crédito.`); return;
    }
    alert('¡Venta facturada exitosamente!');
    this.carrito = []; this.clienteVentaSeleccionado = null; this.esCredito = false;
  }
}