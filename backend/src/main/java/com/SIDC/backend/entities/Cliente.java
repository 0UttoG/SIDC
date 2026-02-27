package com.SIDC.backend.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "clientes")
public class Cliente {

    // --- Campos heredados de 'personas' ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String telefono;

    private Boolean activo = true;

    // --- Campos propios de 'clientes' ---
    @Column(name = "id_ruta")
    private Long idRuta;

    @Column(name = "limite_credito")
    private BigDecimal limiteCredito;

    @Column(name = "saldo_actual")
    private BigDecimal saldoActual = BigDecimal.ZERO;

    // Usamos EnumType.STRING para que Java lo mande como texto a la BD
    @Enumerated(EnumType.STRING)
    @Column(name = "canal")
    private TipoCanal canal;

    public Cliente() {}

    // ¡Recuerda generar los Getters y Setters aquí! (Alt + Insert en tu IDE)
}