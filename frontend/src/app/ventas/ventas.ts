import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { VentasService, VentaRequestDTO } from './ventas.service'; // <-- Importamos el nuevo servicio

@Component({
  selector: 'app-ventas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ventas.html',
})
export class Ventas implements OnInit {
  
  private ventasService = inject(VentasService);
  private http = inject(HttpClient);

  clientes: any[] = []; 
  
  // ¡Se fueron los datos quemados! Ahora arranca vacío y se llena del backend
  productosInventario: any[] = []; 

  clienteVentaSeleccionado: any = null;
  carrito: any[] = [];
  esCredito: boolean = false;

  mostrarModalCliente: boolean = false;
  
  nuevoCliente: any = { 
    nombre: '', direccion: '', telefono: '', correo: '', canal: '', limiteCredito: null, idRuta: 1 
  };

  ngOnInit() {
    this.cargarClientes();
    this.cargarCatalogoReal(); // <-- Ejecutamos la carga de productos al entrar
  }

  // ==========================================
  // PASO 1: Obtener el Catálogo Real
  // ==========================================
  cargarCatalogoReal() {
    this.ventasService.getCatalogo().subscribe({
      next: (data) => {
        // Adaptamos el DTO de Nestor a los nombres que usa tu HTML
        this.productosInventario = data.map(p => ({
          id_bodega: p.idBodega,
          id_producto: p.idProducto,
          id_lote: p.idLote, 
          lote_display: p.lote,
          nombre: p.nombre,
          precio_base: p.precio,
          stock_actual: p.stock,
          vencimiento: p.vencimiento
        }));
      },
      error: (err) => console.error('Error cargando el catálogo:', err)
    });
  }

  // ==========================================
  // LÓGICA DE CLIENTES (Se mantiene intacta)
  // ==========================================
  cargarClientes() {
    this.http.get<any[]>('http://localhost:8080/api/clientes').subscribe({
      next: (data) => this.clientes = data,
      error: (err) => console.error('Error cargando clientes:', err)
    });
  }

  abrirModalCliente() { this.mostrarModalCliente = true; }
  
  cerrarModalCliente() { 
    this.mostrarModalCliente = false; 
    this.nuevoCliente = { nombre: '', direccion: '', telefono: '', correo: '', canal: '', limiteCredito: null, idRuta: 1 }; 
  }

  guardarCliente() {
    if (!this.nuevoCliente.nombre || !this.nuevoCliente.direccion || !this.nuevoCliente.canal || !this.nuevoCliente.correo || this.nuevoCliente.limiteCredito === null) {
      alert('Por favor llena el nombre, dirección, canal, correo y límite de crédito.'); return;
    }

    const clienteDTO = {
      nombre: this.nuevoCliente.nombre,
      direccion: this.nuevoCliente.direccion,
      telefono: this.nuevoCliente.telefono,
      correo: this.nuevoCliente.correo,
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
      error: (err) => alert('Error al guardar cliente.')
    });
  }

  // ==========================================
  // LÓGICA DEL CARRITO
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
  // PASO 2: Procesar la Facturación Real
  // ==========================================
  facturar() {
    if (!this.clienteVentaSeleccionado || this.carrito.length === 0) { 
      alert('Selecciona cliente y productos.'); return; 
    }

    // Armamos el objeto exacto que Nestor pidió
    const ventaRequest: VentaRequestDTO = {
      idCliente: this.clienteVentaSeleccionado.id || this.clienteVentaSeleccionado.id_cliente,
      idVendedor: 1, // Vendedor genérico o asigando a la ruta
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

    // Usamos el servicio que acabás de crear
    this.ventasService.procesarVenta(ventaRequest).subscribe({
      next: (res) => {
        alert('¡Factura registrada con éxito!');
        this.carrito = []; 
        this.clienteVentaSeleccionado = null; 
        this.esCredito = false;
        this.cargarCatalogoReal(); // Recargamos para que el stock de la pantalla baje automáticamente
      },
      error: (err) => {
        // Aquí capturamos la "Violación de Ruta" o cualquier Trigger de la DB
        alert('⛔ Error: ' + err.message);
      }
    });
  }
}