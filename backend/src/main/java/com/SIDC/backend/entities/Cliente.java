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
    private Boolean activo = true;

    @Column(name = "id_ruta")
    private Long idRuta;

    @Column(name = "limite_credito")
    private BigDecimal limiteCredito;

    @Column(name = "saldo_actual")
    private BigDecimal saldoActual = BigDecimal.ZERO;

    // ¡Esta es la magia para que PostgreSQL lo acepte!
// Dentro de Cliente.java
    @Enumerated(EnumType.STRING)
    @Column(name = "canal", columnDefinition = "tipo_canal") // Forzamos el tipo de la BD
    @JdbcTypeCode(SqlTypes.NAMED_ENUM) // Esto es clave para Hibernate 6+
    private TipoCanal canal;

    public Cliente() {}

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

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