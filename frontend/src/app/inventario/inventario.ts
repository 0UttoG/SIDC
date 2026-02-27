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

  // Variables para los inputs del HTML
  nuevoProducto: any = { nombre: '', lote: '', precio: null, stock: null, vencimiento: '', id_bodega: 1, id_categoria: 1 };
  
  productoSeleccionado: InventarioResponseDTO | null = null;
  ajuste: any = { tipoMovimiento: 'Entrada', cantidad: null };

  ngOnInit() {
    this.cargarInventario();
  }

  cargarInventario() {
    this.inventarioService.getExistencias().subscribe({
      next: (data) => this.productosInventario = data,
      error: (err) => console.error('Error al cargar inventario:', err)
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
    if (!this.nuevoProducto.nombre || !this.nuevoProducto.lote || !this.nuevoProducto.vencimiento) {
      alert('Llena todos los campos obligatorios'); return;
    }

    // Mapeamos al DTO exacto de Nestor
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
        alert('¡Lote registrado exitosamente!');
        this.cargarInventario();
        this.cerrarModalNuevo();
      },
      error: (err) => {
        if (err.status === 400 && err.error?.mensaje) {
          alert('⛔ Violación de Regla: ' + err.error.mensaje);
        } else {
          alert('Error de conexión con el servidor.');
        }
      }
    });
  }

  // ==========================================
  // MODAL: AJUSTE DE STOCK
  // ==========================================
  abrirModalAjuste(prod: InventarioResponseDTO) { 
    this.productoSeleccionado = prod; // Guardamos el producto entero para tener sus IDs
    this.mostrarModalAjuste = true; 
  }
  
  cerrarModalAjuste() { 
    this.mostrarModalAjuste = false; 
    this.productoSeleccionado = null;
    this.ajuste = { tipoMovimiento: 'Entrada', cantidad: null }; 
  }

  guardarAjuste() {
    if (!this.productoSeleccionado || !this.ajuste.cantidad || this.ajuste.cantidad <= 0) {
      alert('Ingresa una cantidad válida mayor a 0'); return;
    }

    // Mapeamos al DTO de Ajuste
    const payload: AjusteStockDTO = {
      idBodega: this.productoSeleccionado.idBodega,
      idProducto: this.productoSeleccionado.idProducto,
      idLote: this.productoSeleccionado.idLote,
      tipoMovimiento: this.ajuste.tipoMovimiento,
      cantidad: this.ajuste.cantidad
    };

    this.inventarioService.ajustarStock(payload).subscribe({
      next: () => {
        alert('¡Stock actualizado en Supabase!');
        this.cargarInventario();
        this.cerrarModalAjuste();
      },
      error: (err) => {
        if (err.status === 400 && err.error?.mensaje) {
          alert('⛔ Error de Negocio: ' + err.error.mensaje);
        } else {
          alert('Error de conexión con el servidor.');
        }
      }
    });
  }
}