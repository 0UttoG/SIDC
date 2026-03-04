// Archivo: src/main/java/com/SIDC/backend/entities/Cliente.java
package com.SIDC.backend.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String telefono;

    // Campo agregado para facturación y envío de comprobantes
    private String correo;

    // Se maneja como String para compatibilidad con el frontend.
    // Importante: La columna en la DB debe ser de tipo TEXT o VARCHAR.
    private String direccion;

    private Boolean activo = true;

    @Column(name = "id_ruta")
    private Long idRuta;

    @Column(name = "limite_credito")
    private BigDecimal limiteCredito;

    @Column(name = "saldo_actual")
    private BigDecimal saldoActual = BigDecimal.ZERO;

    // Mapeo especial para el tipo ENUM 'tipo_canal' de PostgreSQL
    @Enumerated(EnumType.STRING)
    @Column(name = "canal", columnDefinition = "tipo_canal")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private TipoCanal canal;

    public Cliente() {}

    // --- GETTERS Y SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Long getIdRuta() { return idRuta; }
    public void setIdRuta(Long idRuta) { this.idRuta = idRuta; }

    public BigDecimal getLimiteCredito() { return limiteCredito; }
    public void setLimiteCredito(BigDecimal limiteCredito) { this.limiteCredito = limiteCredito; }

    public BigDecimal getSaldoActual() { return saldoActual; }
    public void setSaldoActual(BigDecimal saldoActual) { this.saldoActual = saldoActual; }

    public TipoCanal getCanal() { return canal; }
    public void setCanal(TipoCanal canal) { this.canal = canal; }
}