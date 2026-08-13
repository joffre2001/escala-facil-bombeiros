package com.obysoft.escalafacil.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.obysoft.escalafacil.entity.Escala;

public interface EscalaRepository extends JpaRepository<Escala, Long> {
    List<Escala> findAllByOrderByDataInicioDesc();
    boolean existsByDataInicioLessThanEqualAndDataFimGreaterThanEqual(
            LocalDate dataFim, LocalDate dataInicio);
}