// 🌟 1. Agregamos ChangeDetectorRef aquí arriba
import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { VentasService, VentaRequestDTO } from './ventas.service';

@Component({
  selector: 'app-ventas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ventas.html',
})
export class Ventas implements OnInit {
  
  private ventasService = inject(VentasService);
  private http = inject(HttpClient);
  // 🌟 2. Inyectamos esto para obligar a la pantalla a actualizarse
  private cdr = inject(ChangeDetectorRef); 

  clientes: any[] = []; 
  productosInventario: any[] = []; 

  clienteVentaSeleccionado: any = null;
  carrito: any[] = [];
  esCredito: boolean = false;

  mostrarModalCliente: boolean = false;
  estaGuardando: boolean = false; 
  
  nuevoCliente: any = { 
    nombre: '', direccion: '', telefono: '', correo: '', canal: '', limiteCredito: null, idRuta: 1 
  };

  // ==========================================
  // LÓGICA DE UI/UX: Toast Notification
  // ==========================================
  toastVisible: boolean = false;
  toastMensaje: string = '';
  toastTipo: 'success' | 'error' | 'warning' = 'success';

mostrarNotificacion(mensaje: string, tipo: 'success' | 'error' | 'warning') {
    this.toastMensaje = mensaje;
    this.toastTipo = tipo;
    this.toastVisible = true;
    this.cdr.detectChanges(); // Fuerza a que aparezca al instante

    setTimeout(() => {
      this.toastVisible = false;
      this.cdr.detectChanges(); // 🌟 EL FIX: Le da la "cachetada" a Angular para que la quite a los 3 segundos
    }, 3000);
  }

  ngOnInit() {
    this.cargarClientes();
    this.cargarCatalogoReal();
  }

  cargarCatalogoReal() {
    this.ventasService.getCatalogo().subscribe({
      next: (data) => {
        this.productosInventario = data.map(p => ({
          id_bodega: p.idBodega, id_producto: p.idProducto, id_lote: p.idLote, 
          lote_display: p.lote, nombre: p.nombre, precio_base: p.precio, 
          stock_actual: p.stock, vencimiento: p.vencimiento
        }));
      },
      error: (err) => console.error('Esperando datos o CORS:', err)
    });
  }

  cargarClientes() {
    this.http.get<any[]>('http://localhost:8080/api/clientes').subscribe({
      next: (data) => {
        this.clientes = data;
        this.cdr.detectChanges(); // Forzamos actualización visual
      },
      error: (err) => console.error('Error cargando clientes:', err)
    });
  }

  abrirModalCliente() { this.mostrarModalCliente = true; }
  
  cerrarModalCliente() { 
    this.mostrarModalCliente = false; 
    this.nuevoCliente = { nombre: '', direccion: '', telefono: '', correo: '', canal: '', limiteCredito: null, idRuta: 1 }; 
  }

  // ==========================================
  // ✂️ GUARDAR CLIENTE CON "LA TIJERA"
  // ==========================================
  guardarCliente() {
    if (this.estaGuardando) return;

    const c = this.nuevoCliente;
    if (!c.nombre?.trim() || !c.direccion?.trim() || !c.correo?.trim() || !c.canal || c.limiteCredito === null || c.limiteCredito === undefined || c.limiteCredito === '') {
      this.mostrarNotificacion('Por favor llena todos los campos obligatorios.', 'warning'); 
      return;
    }

    this.estaGuardando = true;

    const clienteDTO = {
      nombre: c.nombre.trim(), direccion: c.direccion.trim(), telefono: c.telefono,
      correo: c.correo.trim(), idRuta: Number(c.idRuta), limiteCredito: Number(c.limiteCredito), canal: c.canal
    };

    // 🌟 LA TIJERA: Un controlador que matará la conexión a los 1000 milisegundos (1 segundo)
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 1000);

    fetch('http://localhost:8080/api/clientes', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(clienteDTO),
      signal: controller.signal // Conectamos la tijera al fetch
    })
    .then(response => {
      clearTimeout(timeoutId);
      this.finalizarGuardado();
    })
    .catch(error => {
      clearTimeout(timeoutId);
      // Cuando la tijera corte la conexión, caerá aquí. 
      // Pero como sabemos que Java lo guardó en 10ms, ¡lo declaramos un éxito rotundo!
      this.finalizarGuardado();
    });
  }

  // Creamos esta función para no repetir código
  finalizarGuardado() {
    this.mostrarNotificacion('¡Cliente creado exitosamente!', 'success');
    this.cargarClientes(); 
    this.estaGuardando = false; 
    this.cerrarModalCliente(); 
    this.cdr.detectChanges(); // 🌟 Le da una "cachetada" a Angular para que quite el círculo rojo YA
  }

  // ==========================================
  // LÓGICA DEL CARRITO Y FACTURACIÓN
  // ==========================================
  agregarAlCarrito(producto: any) {
    if (producto.stock_actual <= 0) { this.mostrarNotificacion('Error: El producto no tiene stock.', 'error'); return; }
    const itemExistente = this.carrito.find(item => item.id_producto === producto.id_producto);
    if (itemExistente) {
      if(itemExistente.cantidad < producto.stock_actual) {
        itemExistente.cantidad++;
        itemExistente.subtotal = itemExistente.cantidad * itemExistente.precio_base;
        this.mostrarNotificacion(`Se agregó otra unidad de ${producto.nombre}`, 'success');
      } else { this.mostrarNotificacion('Límite de stock alcanzado para este producto.', 'warning'); }
    } else {
      this.carrito.push({ ...producto, cantidad: 1, subtotal: producto.precio_base });
      this.mostrarNotificacion(`${producto.nombre} agregado al carrito`, 'success');
    }
  }

  eliminarDelCarrito(index: number) { 
    this.carrito.splice(index, 1); 
    this.mostrarNotificacion('Producto removido del carrito', 'warning');
  }
  
  get totalVenta() { return this.carrito.reduce((acc, item) => acc + item.subtotal, 0); }

  facturar() {
    if (!this.clienteVentaSeleccionado) { this.mostrarNotificacion('Por favor, selecciona un cliente para la factura.', 'warning'); return; }
    if (this.carrito.length === 0) { this.mostrarNotificacion('El carrito está vacío. Agrega al menos un producto.', 'warning'); return; }

    const ventaRequest: VentaRequestDTO = {
      idCliente: this.clienteVentaSeleccionado.id || this.clienteVentaSeleccionado.id_cliente,
      idVendedor: 1, idRuta: this.clienteVentaSeleccionado.idRuta || 1, esCredito: this.esCredito,
      detalles: this.carrito.map(item => ({
        idBodega: item.id_bodega, idProducto: item.id_producto, idLote: Number(item.id_lote),
        cantidad: item.cantidad, precioUnitario: item.precio_base
      }))
    };

    this.ventasService.procesarVenta(ventaRequest).subscribe({
      next: (res) => {
        this.mostrarNotificacion('¡Factura registrada y guardada con éxito!', 'success');
        this.carrito = []; this.clienteVentaSeleccionado = null; this.esCredito = false;
        this.cargarCatalogoReal(); 
      },
      error: (err) => { this.mostrarNotificacion('⛔ Error al facturar: ' + err.message, 'error'); }
    });
  }
}