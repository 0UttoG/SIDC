import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InventarioService, InventarioResponseDTO, NuevoLoteDTO, ActualizarProductoDTO } from './inventario.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-inventario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inventario.html',
})
export class Inventario implements OnInit {
  private inventarioService = inject(InventarioService);
  private cdr = inject(ChangeDetectorRef);

  productosInventario: InventarioResponseDTO[] = [];

  mostrarModalNuevo: boolean = false;
  mostrarModalAjuste: boolean = false;
  estaGuardando: boolean = false; 

  nuevoProducto: any = { nombre: '', lote: '', precio: null, stock: null, vencimiento: '', id_bodega: 1, id_categoria: 1 };
  
  productoSeleccionado: InventarioResponseDTO | null = null;
  ajuste: any = { nuevoNombre: '', nuevoPrecio: null, nuevoLote: '', nuevaFecha: '', stockFinal: 0 };

  toastVisible: boolean = false;
  toastMensaje: string = '';
  toastTipo: 'success' | 'error' | 'warning' = 'success';

  mostrarNotificacion(mensaje: string, tipo: 'success' | 'error' | 'warning') {
    this.toastMensaje = mensaje;
    this.toastTipo = tipo;
    this.toastVisible = true;
    this.cdr.detectChanges();

    setTimeout(() => { 
      this.toastVisible = false; 
      this.cdr.detectChanges();
    }, 3000);
  }

  ngOnInit() {
    this.cargarInventario();
  }

  cargarInventario() {
    this.inventarioService.getExistencias().subscribe({
      next: (data) => {
        this.productosInventario = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar inventario:', err);
        this.mostrarNotificacion('Error al cargar el inventario.', 'error');
      }
    });
  }

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

    if (this.nuevoProducto.stock < 0) {
      this.mostrarNotificacion('El stock inicial no puede ser negativo.', 'warning'); 
      return;
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

    this.inventarioService.registrarLote(payload).subscribe({
      next: () => {
        this.mostrarNotificacion('¡Lote registrado exitosamente!', 'success');
        this.cargarInventario();
        this.estaGuardando = false;
        this.cerrarModalNuevo();
      },
      error: (err) => {
        this.estaGuardando = false;
        this.cdr.detectChanges();
        this.mostrarNotificacion(err.error?.mensaje || 'Error de conexión', 'error');
      }
    });
  }

abrirModalAjuste(prod: any) { 
    // 🕵️‍♂️ EL CHISMOSO: Esto va a imprimir en tu navegador el JSON exacto
    console.log("Datos exactos que manda Java:", prod);

    this.productoSeleccionado = prod; 
    this.ajuste = { 
      nuevoNombre: prod.producto,
      // 🌟 TRAMPA TRIPLE: Buscamos las 3 formas en las que Java pudo mandarlo
      nuevoPrecio: prod.precio || prod.precioBase || prod.precio_base || null, 
      nuevoLote: prod.lote,
      nuevaFecha: prod.vencimiento,
      stockFinal: prod.stockActual
    }; 
    this.mostrarModalAjuste = true; 
  }
  
  cerrarModalAjuste() { 
    this.mostrarModalAjuste = false; 
    this.productoSeleccionado = null;
    this.ajuste = { nuevoNombre: '', nuevoPrecio: null, nuevoLote: '', nuevaFecha: '', stockFinal: 0 }; 
  }

  guardarAjuste() {
    if (this.estaGuardando) return;
    if (!this.productoSeleccionado || !this.ajuste.nuevoNombre || !this.ajuste.nuevoLote || !this.ajuste.nuevaFecha) {
      this.mostrarNotificacion('Faltan datos obligatorios.', 'warning'); return;
    }

    // 🌟 CANDADO: Evita mandar precios vacíos o en cero
    if (this.ajuste.nuevoPrecio === null || this.ajuste.nuevoPrecio <= 0) {
      this.mostrarNotificacion('El precio unitario debe ser mayor a $0.', 'warning'); 
      return;
    }

    if (this.ajuste.stockFinal < 0) {
      this.mostrarNotificacion('El stock final no puede ser menor a cero.', 'warning'); 
      return;
    }

    this.estaGuardando = true;

    const payload: ActualizarProductoDTO = {
      idLote: this.productoSeleccionado.idLote,
      idBodega: this.productoSeleccionado.idBodega,
      nombreProducto: this.ajuste.nuevoNombre,
      codigoLote: this.ajuste.nuevoLote,
      precio: this.ajuste.nuevoPrecio,
      stock: this.ajuste.stockFinal,
      fechaVencimiento: this.ajuste.nuevaFecha
    };

    this.inventarioService.actualizarProducto(this.productoSeleccionado.idProducto, payload).subscribe({
      next: () => {
        this.mostrarNotificacion('¡Producto actualizado exitosamente!', 'success');
        this.cargarInventario();
        this.estaGuardando = false;
        this.cerrarModalAjuste();
      },
      error: (err) => {
        this.estaGuardando = false;
        this.cdr.detectChanges(); // 🌟 MAGIA: Destraba el botón de inmediato si hay error
        this.mostrarNotificacion(err.error?.mensaje || 'Error al actualizar', 'error');
      }
    });
  }

  eliminarProducto() {
    if (!this.productoSeleccionado) return;

    Swal.fire({
      title: '¿Estás seguro?',
      text: "Este producto ya no aparecerá para la venta. Esta acción no borra facturas pasadas.",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#ef4444',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.inventarioService.eliminarProducto(this.productoSeleccionado!.idProducto).subscribe({
          next: () => {
            this.mostrarNotificacion('Producto eliminado del catálogo.', 'success');
            this.cargarInventario();
            this.cerrarModalAjuste();
          },
          error: (err) => {
            this.mostrarNotificacion(err.error?.mensaje || 'No se pudo eliminar el producto.', 'error');
          }
        });
      }
    });
  }
}