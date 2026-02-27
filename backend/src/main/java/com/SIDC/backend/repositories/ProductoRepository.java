package com.SIDC.backend.repositories;

import com.SIDC.backend.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Busca un producto ignorando mayúsculas/minúsculas para evitar duplicados
    Optional<Producto> findByNombreIgnoreCase(String nombre);
}