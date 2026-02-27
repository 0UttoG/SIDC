package com.SIDC.backend.repositories;

import com.SIDC.backend.entities.Bodega;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodegaRepository extends JpaRepository<Bodega, Long> {
}