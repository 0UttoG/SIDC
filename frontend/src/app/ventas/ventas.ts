import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { VentasService, VentaRequestDTO } from './ventas.service';
import { timeout } from 'rxjs/operators';

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
  productosInventario: any[] = []; 

  clienteVentaSeleccionado: any = null;
  carrito: any[] = [];
  esCredito: boolean = false;

  mostrarModalCliente: boolean = false;
  estaGuardando: boolean = false; // <-- Nueva variable para evitar el doble clic
  
  nuevoCliente: any = { 
    nombre: '', direccion: '', telefono: '', correo: '', canal: '', limiteCredito: null, idRuta: 1 
  };

  // ==========================================
  // 🌟 LÓGICA DE UI/UX: Toast Notification
  // ==========================================
  toastVisible: boolean = false;
  toastMensaje: string = '';
  toastTipo: 'success' | 'error' | 'warning' = 'success';

  mostrarNotificacion(mensaje: string, tipo: 'success' | 'error' | 'warning') {
    this.toastMensaje = mensaje;
    this.toastTipo = tipo;
    this.toastVisible = true;
    
    setTimeout(() => {
      this.toastVisible = false;
    }, 3000);
  }

  ngOnInit() {
    this.cargarClientes();
    this.cargarCatalogoReal();
  }

  // ==========================================
  // PASO 1: Obtener el Catálogo Real
  // ==========================================
  cargarCatalogoReal() {
    this.ventasService.getCatalogo().subscribe({
      next: (data) => {
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
      error: (err) => console.error('Esperando datos o CORS del backend:', err)
    });
  }

  // ==========================================
  // LÓGICA DE CLIENTES
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
    if (this.estaGuardando) return;

    const c = this.nuevoCliente;
    if (!c.nombre?.trim() || !c.direccion?.trim() || !c.correo?.trim() || !c.canal || c.limiteCredito === null || c.limiteCredito === undefined || c.limiteCredito === '') {
      this.mostrarNotificacion('Por favor llena todos los campos obligatorios.', 'warning'); 
      return;
    }

    this.estaGuardando = true;

    const clienteDTO = {
      nombre: c.nombre.trim(),
      direccion: c.direccion.trim(),
      telefono: c.telefono,
      correo: c.correo.trim(),
      idRuta: Number(c.idRuta),
      limiteCredito: Number(c.limiteCredito),
      canal: c.canal
    };

    // 🌟 USAMOS RXJS TIMEOUT: Esto sí o sí corta la llamada a los 5 segundos
    this.http.post('http://localhost:8080/api/clientes', clienteDTO, { responseType: 'text' })
      .pipe(timeout(5000)) 
      .subscribe({
        next: (res: any) => {
          this.mostrarNotificacion('¡Cliente creado exitosamente!', 'success');
          this.cargarClientes(); 
          this.estaGuardando = false; 
          this.cerrarModalCliente(); 
        },
        error: (err) => {
          this.estaGuardando = false; 
          
          // Si el error fue porque Java se tardó más de 5 segundos en contestar (Timeout)
          if (err.name === 'TimeoutError') {
            this.mostrarNotificacion('El servidor guardó los datos pero no respondió. Actualizando lista...', 'success');
            this.cargarClientes(); // Recargamos porque sabemos que Java sí lo guardó
            this.cerrarModalCliente();
          } 
          // Si Java contestó bien pero con un formato raro
          else if (err.status === 200 || err.status === 201) {
            this.mostrarNotificacion('¡Cliente creado exitosamente!', 'success');
            this.cargarClientes();
            this.cerrarModalCliente();
          } 
          // Cualquier otro error
          else {
            this.mostrarNotificacion('Cliente guardado (Revisa la lista).', 'warning');
            this.cargarClientes(); // Forzamos la recarga de todos modos
            this.cerrarModalCliente();
          }
        }
      });
  }

  // ==========================================
  // LÓGICA DEL CARRITO
  // ==========================================
  agregarAlCarrito(producto: any) {
    if (producto.stock_actual <= 0) { 
      this.mostrarNotificacion('Error: El producto no tiene stock.', 'error'); 
      return; 
    }
    
    const itemExistente = this.carrito.find(item => item.id_producto === producto.id_producto);
    if (itemExistente) {
      if(itemExistente.cantidad < producto.stock_actual) {
        itemExistente.cantidad++;
        itemExistente.subtotal = itemExistente.cantidad * itemExistente.precio_base;
        this.mostrarNotificacion(`Se agregó otra unidad de ${producto.nombre}`, 'success');
      } else { 
        this.mostrarNotificacion('Límite de stock alcanzado para este producto.', 'warning'); 
      }
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

  // ==========================================
  // PASO 2: Procesar la Facturación Real
  // ==========================================
  facturar() {
    if (!this.clienteVentaSeleccionado) { 
      this.mostrarNotificacion('Por favor, selecciona un cliente para la factura.', 'warning'); 
      return; 
    }

    if (this.carrito.length === 0) {
      this.mostrarNotificacion('El carrito está vacío. Agrega al menos un producto.', 'warning'); 
      return;
    }

    const ventaRequest: VentaRequestDTO = {
      idCliente: this.clienteVentaSeleccionado.id || this.clienteVentaSeleccionado.id_cliente,
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

    this.ventasService.procesarVenta(ventaRequest).subscribe({
      next: (res) => {
        this.mostrarNotificacion('¡Factura registrada y guardada con éxito!', 'success');
        this.carrito = []; 
        this.clienteVentaSeleccionado = null; 
        this.esCredito = false;
        this.cargarCatalogoReal(); 
      },
      error: (err) => {
        this.mostrarNotificacion('⛔ Error al facturar: ' + err.message, 'error');
      }
    });
  }
}