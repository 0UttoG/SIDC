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

    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(name = "id_vendedor")
    private Long idVendedor;

    @Column(name = "id_ruta")
    private Long idRuta;

    @Column(name = "fecha_venta", insertable = false, updatable = false)
    private LocalDateTime fechaVenta;

    private BigDecimal total = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", insertable = false)
    private EstadoDoc estado;

    @Column(name = "es_credito")
    private Boolean esCredito = false;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VentaDetalle> detalles = new ArrayList<>();

    public Venta() {}

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }

    public Long getIdVendedor() { return idVendedor; }
    public void setIdVendedor(Long idVendedor) { this.idVendedor = idVendedor; }

    public Long getIdRuta() { return idRuta; }
    public void setIdRuta(Long idRuta) { this.idRuta = idRuta; }

    public LocalDateTime getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDateTime fechaVenta) { this.fechaVenta = fechaVenta; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public EstadoDoc getEstado() { return estado; }
    public void setEstado(EstadoDoc estado) { this.estado = estado; }

    public Boolean getEsCredito() { return esCredito; }
    public void setEsCredito(Boolean esCredito) { this.esCredito = esCredito; }

    public List<VentaDetalle> getDetalles() { return detalles; }
    public void setDetalles(List<VentaDetalle> detalles) { this.detalles = detalles; }
}