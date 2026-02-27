package com.SIDC.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ExistenciaId implements Serializable {

    @Column(name = "id_bodega")
    private Long idBodega;

    @Column(name = "id_producto")
    private Long idProducto;

    @Column(name = "id_lote")
    private Long idLote;

    public ExistenciaId() {}

    public ExistenciaId(Long idBodega, Long idProducto, Long idLote) {
        this.idBodega = idBodega;
        this.idProducto = idProducto;
        this.idLote = idLote;
    }

    // Getters y Setters
    public Long getIdBodega() { return idBodega; }
    public void setIdBodega(Long idBodega) { this.idBodega = idBodega; }

    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }

    public Long getIdLote() { return idLote; }
    public void setIdLote(Long idLote) { this.idLote = idLote; }

    // ¡Obligatorio para llaves compuestas en JPA!
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExistenciaId that = (ExistenciaId) o;
        return Objects.equals(idBodega, that.idBodega) &&
                Objects.equals(idProducto, that.idProducto) &&
                Objects.equals(idLote, that.idLote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idBodega, idProducto, idLote);
    }
}