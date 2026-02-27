package com.SIDC.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "existencias")
public class Existencia {

    @EmbeddedId
    private ExistenciaId id;

    @Column(name = "stock_actual")
    private Integer stockActual;

    public Existencia() {}

    public ExistenciaId getId() { return id; }
    public void setId(ExistenciaId id) { this.id = id; }

    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }
}