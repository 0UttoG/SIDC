package com.SIDC.backend.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ventas_detalle")
public class VentaDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    @Column(name = "id_bodega")
    private Long idBodega;

    @Column(name = "id_producto")
    private Long idProducto;

    @Column(name = "id_lote")
    private Long idLote;

    private Integer cantidad;

    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;

    // ¡Clave! Bloqueamos la inserción porque PostgreSQL lo calcula automáticamente
    @Column(name = "subtotal", insertable = false, updatable = false)
    private BigDecimal subtotal;

    // Constructor vacío requerido por JPA
    public VentaDetalle() {}

    // Genera los Getters y Setters aquí debajo
}