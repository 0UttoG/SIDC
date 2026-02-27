package com.SIDC.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "bodegas")
public class Bodega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    public Bodega() {}

    // Recuerda generar Getters y Setters
}