package com.obysoft.escalafacil.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.obysoft.escalafacil.entity.Indisponibilidade;

public interface IndisponibilidadeRepository
        extends JpaRepository<Indisponibilidade, Long> {

    List<Indisponibilidade>
            findByBombeiroIdOrderByDataInicioAsc(Long bombeiroId);

    boolean existsByBombeiroIdAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
            Long bombeiroId,
            LocalDate dataFim,
            LocalDate dataInicio
    );

    boolean existsByBombeiroIdAndDataInicioLessThanEqualAndDataFimGreaterThanEqualAndIdNot(
            Long bombeiroId,
            LocalDate dataFim,
            LocalDate dataInicio,
            Long id
    );
}