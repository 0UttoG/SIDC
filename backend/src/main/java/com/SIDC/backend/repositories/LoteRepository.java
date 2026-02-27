package com.SIDC.backend.repositories;

import com.SIDC.backend.entities.Lote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoteRepository extends JpaRepository<Lote, Long> {
}