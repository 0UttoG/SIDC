package com.SIDC.backend.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mapeados como Long para que compiles sin depender de tu compañero
    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(name = "id_vendedor")
    private Long idVendedor;

    @Column(name = "id_ruta")
    private Long idRuta;

    // La base de datos pone la fecha por defecto (CURRENT_TIMESTAMP)
    @Column(name = "fecha_venta", insertable = false, updatable = false)
    private LocalDateTime fechaVenta;

    private BigDecimal total = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", insertable = false)
    private EstadoDoc estado;

    @Column(name = "es_credito")
    private Boolean esCredito = false;

    // Relación 1 a N con el detalle de la venta
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VentaDetalle> detalles = new ArrayList<>();

    // Constructor vacío requerido por JPA
    public Venta() {}

    // Tip para Pop!_OS/IDE: Usa Alt + Insert para generar rápidamente
    // todos los Getters y Setters aquí debajo (si no estás usando Lombok).
}