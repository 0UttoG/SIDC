package com.SIDC.backend.repositories;

import com.SIDC.backend.entities.Promocion;
import com.SIDC.backend.entities.TipoCanal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.Optional;

public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    @Query("SELECT p FROM Promocion p WHERE p.canalObjetivo = :canal " +
            "AND :hoy BETWEEN p.fechaInicio AND p.fechaFin")
    Optional<Promocion> buscarPromocionActiva(@Param("canal") TipoCanal canal, @Param("hoy") LocalDate hoy);
}