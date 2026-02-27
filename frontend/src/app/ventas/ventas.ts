import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http'; // Importamos el cliente HTTP

@Component({
  selector: 'app-ventas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ventas.html',
})
export class Ventas implements OnInit {
  
  clientes: any[] = []; // Ahora empezará vacío y se llenará desde la Base de Datos
  
  // Dejamos los productos de prueba, pero les agregamos "id_bodega" para la venta
  productosInventario = [
    { id_producto: 1, id_lote: '101', id_bodega: 1, nombre: 'Arroz Premium 5kg', precio_base: 6.50, stock_actual: 50, vencimiento: '2026-12-01' },
    { id_producto: 2, id_lote: '102', id_bodega: 1, nombre: 'Aceite Vegetal 1L', precio_base: 2.25, stock_actual: 5, vencimiento: '2026-05-15' },
    { id_producto: 3, id_lote: '103', id_bodega: 1, nombre: 'Frijol Rojo 1kg', precio_base: 1.80, stock_actual: 120, vencimiento: '2026-03-10' }
  ];

  clienteVentaSeleccionado: any = null;
  carrito: any[] = [];
  esCredito: boolean = false;

  mostrarModalCliente: boolean = false;
  // Cambiamos a camelCase (limiteCredito y idRuta) para que coincida con el Spring Boot de Nestor
  nuevoCliente: any = { nombre: '', telefono: '', canal: '', limiteCredito: null, idRuta: 1 };

  // Inyectamos el HttpClient en el constructor
  constructor(private http: HttpClient) {}

  // Esto se ejecuta apenas abres la pantalla de ventas
  ngOnInit() {
    this.cargarClientes();
  }

  // ==========================================
  // CONEXIÓN BACKEND: GET Clientes
  // ==========================================
  cargarClientes() {
    this.http.get<any[]>('http://localhost:8080/api/clientes').subscribe({
      next: (data) => {
        this.clientes = data; // Guardamos lo que responde el backend
      },
      error: (err) => {
        console.error('El backend no está encendido o hay un error:', err);
      }
    });
  }

  // ==========================================
  // CONEXIÓN BACKEND: POST Nuevo Cliente
  // ==========================================
  abrirModalCliente() { this.mostrarModalCliente = true; }
  cerrarModalCliente() { 
    this.mostrarModalCliente = false; 
    this.nuevoCliente = { nombre: '', telefono: '', canal: '', limiteCredito: null, idRuta: 1 }; 
  }

  guardarCliente() {
    if (!this.nuevoCliente.nombre || !this.nuevoCliente.canal || this.nuevoCliente.limiteCredito === null) {
      alert('Por favor llena el nombre, canal y límite de crédito.'); return;
    }

    // Armamos el JSON exacto que pide Nestor
    const clienteDTO = {
      nombre: this.nuevoCliente.nombre,
      telefono: this.nuevoCliente.telefono,
      idRuta: Number(this.nuevoCliente.idRuta),
      limiteCredito: this.nuevoCliente.limiteCredito,
      canal: this.nuevoCliente.canal
    };

    this.http.post('http://localhost:8080/api/clientes', clienteDTO).subscribe({
      next: (res: any) => {
        alert('¡Cliente creado exitosamente en Supabase!');
        this.cargarClientes(); // Recargamos la lista para que aparezca
        this.cerrarModalCliente();
      },
      error: (err) => {
        // Atrapamos el error 400 personalizado de Nestor
        if (err.status === 400 && err.error && err.error.mensaje) {
          alert('Error: ' + err.error.mensaje);
        } else {
          alert('Error de conexión con el servidor (Revisa que Spring Boot esté encendido).');
        }
      }
    });
  }

  // ==========================================
  // LÓGICA DEL CARRITO (Frontend puro)
  // ==========================================
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

  // ==========================================
  // CONEXIÓN BACKEND: POST Venta Completa
  // ==========================================
  facturar() {
    if (!this.clienteVentaSeleccionado || this.carrito.length === 0) { 
      alert('Selecciona cliente y productos.'); return; 
    }

    // Armamos el VentaRequestDTO exacto que pide Nestor
    const ventaRequest = {
      idCliente: this.clienteVentaSeleccionado.id,
      idVendedor: 1, // Vendedor por defecto por ahora
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
        alert('¡Factura registrada! El inventario y los créditos se han actualizado en Supabase.');
        this.carrito = []; 
        this.clienteVentaSeleccionado = null; 
        this.esCredito = false;
      },
      error: (err) => {
        // Aquí es donde el trigger de PostgreSQL brilla. Si falla el stock, saltará esto:
        if (err.status === 400 && err.error && err.error.mensaje) {
          alert('⛔ Error de Negocio: ' + err.error.mensaje);
        } else {
          alert('Error de conexión con el backend.');
        }
      }
    });
  }
}