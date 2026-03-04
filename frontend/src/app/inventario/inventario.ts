import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InventarioService, InventarioResponseDTO, NuevoLoteDTO, AjusteStockDTO } from './inventario.service';

@Component({
  selector: 'app-inventario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inventario.html',
})
export class Inventario implements OnInit {
  private inventarioService = inject(InventarioService);

  productosInventario: InventarioResponseDTO[] = [];

  mostrarModalNuevo: boolean = false;
  mostrarModalAjuste: boolean = false;
  estaGuardando: boolean = false; // <-- Para evitar el doble clic

  nuevoProducto: any = { nombre: '', lote: '', precio: null, stock: null, vencimiento: '', id_bodega: 1, id_categoria: 1 };
  
  productoSeleccionado: InventarioResponseDTO | null = null;
  // Añadimos nombre y precio a la variable para que podás editarlos
  ajuste: any = { tipoMovimiento: 'Entrada', cantidad: null, nuevoNombre: '', nuevoPrecio: null };

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
    setTimeout(() => { this.toastVisible = false; }, 3000);
  }

  ngOnInit() {
    this.cargarInventario();
  }

  cargarInventario() {
    this.inventarioService.getExistencias().subscribe({
      next: (data) => this.productosInventario = data,
      error: (err) => {
        console.error('Error al cargar inventario:', err);
        this.mostrarNotificacion('Error al cargar el inventario del servidor.', 'error');
      }
    });
  }

  // ==========================================
  // MODAL: NUEVO LOTE
  // ==========================================
  abrirModalNuevo() { this.mostrarModalNuevo = true; }
  
  cerrarModalNuevo() { 
    this.mostrarModalNuevo = false; 
    this.nuevoProducto = { nombre: '', lote: '', precio: null, stock: null, vencimiento: '', id_bodega: 1, id_categoria: 1 }; 
  }

  guardarProducto() {
    if (this.estaGuardando) return;

    if (!this.nuevoProducto.nombre || !this.nuevoProducto.lote || !this.nuevoProducto.vencimiento) {
      this.mostrarNotificacion('Llena todos los campos obligatorios', 'warning'); return;
    }

    this.estaGuardando = true;

    const payload: NuevoLoteDTO = {
      nombreProducto: this.nuevoProducto.nombre,
      codigoLote: this.nuevoProducto.lote,
      precio: this.nuevoProducto.precio,
      stock: this.nuevoProducto.stock,
      fechaVencimiento: this.nuevoProducto.vencimiento,
      idBodega: this.nuevoProducto.id_bodega,
      idCategoria: this.nuevoProducto.id_categoria
    };

    const timeoutId = setTimeout(() => {
      if (this.estaGuardando) {
        this.estaGuardando = false;
        this.mostrarNotificacion('El servidor tardó demasiado. Revisa la tabla.', 'warning');
        this.cargarInventario();
        this.cerrarModalNuevo(); 
      }
    }, 5000);

    this.inventarioService.registrarLote(payload).subscribe({
      next: () => {
        clearTimeout(timeoutId);
        this.mostrarNotificacion('¡Lote registrado exitosamente!', 'success');
        this.cargarInventario();
        this.estaGuardando = false;
        this.cerrarModalNuevo();
      },
      error: (err) => {
        clearTimeout(timeoutId);
        this.estaGuardando = false;
        if (err.status === 400 && err.error?.mensaje) {
          this.mostrarNotificacion('⛔ Violación de Regla: ' + err.error.mensaje, 'error');
        } else {
          this.mostrarNotificacion('Error de conexión con el servidor.', 'error');
        }
      }
    });
  }

  // ==========================================
  // MODAL: EDICIÓN Y AJUSTE DE STOCK
  // ==========================================
  abrirModalAjuste(prod: any) { 
    this.productoSeleccionado = prod; 
    // Precargamos los datos para que podás verlos antes de editarlos
    this.ajuste = { 
      tipoMovimiento: 'Entrada', 
      cantidad: 0,
      nuevoNombre: prod.producto,
      nuevoPrecio: prod.precio || 0 
    }; 
    this.mostrarModalAjuste = true; 
  }
  
  cerrarModalAjuste() { 
    this.mostrarModalAjuste = false; 
    this.productoSeleccionado = null;
    this.ajuste = { tipoMovimiento: 'Entrada', cantidad: null, nuevoNombre: '', nuevoPrecio: null }; 
  }

  guardarAjuste() {
    if (this.estaGuardando) return;

    if (!this.productoSeleccionado || !this.ajuste.nuevoNombre) {
      this.mostrarNotificacion('El nombre del producto no puede quedar vacío.', 'warning'); return;
    }

    if (this.ajuste.cantidad < 0) {
      this.mostrarNotificacion('La cantidad de stock no puede ser negativa.', 'warning'); return;
    }

    this.estaGuardando = true;

    // Extendemos el payload original para mandar el Nombre y Precio a Java
    const payload: any = {
      idBodega: this.productoSeleccionado.idBodega,
      idProducto: this.productoSeleccionado.idProducto,
      idLote: this.productoSeleccionado.idLote,
      tipoMovimiento: this.ajuste.tipoMovimiento,
      cantidad: this.ajuste.cantidad || 0,
      nuevoNombre: this.ajuste.nuevoNombre,     // <--- Lo que pidió Nestor
      nuevoPrecio: this.ajuste.nuevoPrecio      // <--- Lo que pidió Nestor
    };

    const timeoutId = setTimeout(() => {
      if (this.estaGuardando) {
        this.estaGuardando = false;
        this.mostrarNotificacion('El servidor tardó demasiado. Revisa si se guardó.', 'warning');
        this.cargarInventario();
        this.cerrarModalAjuste(); 
      }
    }, 5000);

    this.inventarioService.ajustarStock(payload).subscribe({
      next: () => {
        clearTimeout(timeoutId);
        this.mostrarNotificacion('¡Producto y stock actualizados en Supabase!', 'success');
        this.cargarInventario();
        this.estaGuardando = false;
        this.cerrarModalAjuste();
      },
      error: (err) => {
        clearTimeout(timeoutId);
        this.estaGuardando = false;
        if (err.status === 400 && err.error?.mensaje) {
          this.mostrarNotificacion('⛔ Error de Negocio: ' + err.error.mensaje, 'error');
        } else {
          this.mostrarNotificacion('Error de conexión con el servidor.', 'error');
        }
      }
    });
  }
}