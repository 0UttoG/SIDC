import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-ventas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ventas.html',
})
export class Ventas implements OnInit {
  
  clientes: any[] = []; 
  
  productosInventario = [
    { id_producto: 1, id_lote: '101', id_bodega: 1, nombre: 'Arroz Premium 5kg', precio_base: 6.50, stock_actual: 50, vencimiento: '2026-12-01' },
    { id_producto: 2, id_lote: '102', id_bodega: 1, nombre: 'Aceite Vegetal 1L', precio_base: 2.25, stock_actual: 5, vencimiento: '2026-05-15' },
    { id_producto: 3, id_lote: '103', id_bodega: 1, nombre: 'Frijol Rojo 1kg', precio_base: 1.80, stock_actual: 120, vencimiento: '2026-03-10' }
  ];

  clienteVentaSeleccionado: any = null;
  carrito: any[] = [];
  esCredito: boolean = false;

  mostrarModalCliente: boolean = false;
  
  // Agregamos 'correo' al objeto inicial tal como pidió Nestor
  nuevoCliente: any = { 
    nombre: '', 
    telefono: '', 
    correo: '', // <--- CAMBIO AQUÍ
    canal: '', 
    limiteCredito: null, 
    idRuta: 1 
  };

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.cargarClientes();
  }

  cargarClientes() {
    this.http.get<any[]>('http://localhost:8080/api/clientes').subscribe({
      next: (data) => {
        this.clientes = data;
      },
      error: (err) => {
        console.error('El backend no está encendido o hay un error:', err);
      }
    });
  }

  abrirModalCliente() { this.mostrarModalCliente = true; }
  
  cerrarModalCliente() { 
    this.mostrarModalCliente = false; 
    // Limpiamos el objeto incluyendo el correo
    this.nuevoCliente = { nombre: '', telefono: '', correo: '', canal: '', limiteCredito: null, idRuta: 1 }; 
  }

  guardarCliente() {
    // Agregamos la validación del correo para que sea obligatorio
    if (!this.nuevoCliente.nombre || !this.nuevoCliente.canal || !this.nuevoCliente.correo || this.nuevoCliente.limiteCredito === null) {
      alert('Por favor llena el nombre, canal, correo y límite de crédito.'); return;
    }

    // Armamos el JSON incluyendo el campo 'correo' para que Nestor lo reciba
    const clienteDTO = {
      nombre: this.nuevoCliente.nombre,
      telefono: this.nuevoCliente.telefono,
      correo: this.nuevoCliente.correo, // <--- CAMBIO AQUÍ
      idRuta: Number(this.nuevoCliente.idRuta),
      limiteCredito: this.nuevoCliente.limiteCredito,
      canal: this.nuevoCliente.canal
    };

    this.http.post('http://localhost:8080/api/clientes', clienteDTO).subscribe({
      next: (res: any) => {
        alert('¡Cliente creado exitosamente en Supabase!');
        this.cargarClientes(); 
        this.cerrarModalCliente();
      },
      error: (err) => {
        if (err.status === 400 && err.error && err.error.mensaje) {
          alert('Error: ' + err.error.mensaje);
        } else {
          alert('Error de conexión con el servidor.');
        }
      }
    });
  }

  // Lógica del Carrito
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
    if (!this.clienteVentaSeleccionado || this.carrito.length === 0) { 
      alert('Selecciona cliente y productos.'); return; 
    }

    const ventaRequest = {
      idCliente: this.clienteVentaSeleccionado.id,
      idVendedor: 1, 
      idRuta: this.clienteVentaSeleccionado.idRuta || 1,
      esCredito: this.esCredito,
      detalles: this.carrito.map(item => ({
        idBodega: item.id_bodega,
        idProducto: item.id_producto,
        idLote: Number(item.id_lote),
        cantidad: item.cantidad,
        precioUnitario: item.precio_base
      }))
    };

    this.http.post('http://localhost:8080/api/ventas', ventaRequest).subscribe({
      next: (res) => {
        alert('¡Factura registrada!');
        this.carrito = []; 
        this.clienteVentaSeleccionado = null; 
        this.esCredito = false;
      },
      error: (err) => {
        if (err.status === 400 && err.error && err.error.mensaje) {
          alert('⛔ Error de Negocio: ' + err.error.mensaje);
        } else {
          alert('Error de conexión con el backend.');
        }
      }
    });
  }
}