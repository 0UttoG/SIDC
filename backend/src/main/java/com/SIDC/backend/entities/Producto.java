// Archivo: src/main/java/com/SIDC/backend/entities/Producto.java
package com.SIDC.backend.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_categoria")
    private Long idCategoria;

    private String nombre;

    @Column(name = "precio_base")
    private BigDecimal precioBase;

    // 👇 NUEVO CAMPO PARA EL BORRADO LÓGICO (Deshabilitar)
    @Column(name = "activo", columnDefinition = "boolean default true")
    private Boolean activo = true;

    public Producto() {}

    // --- GETTERS Y SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }

    // 👇 NUEVOS GETTERS Y SETTERS PARA 'ACTIVO'
    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}