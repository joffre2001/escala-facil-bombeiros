package com.obysoft.escalafacil.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.obysoft.escalafacil.entity.Indisponibilidade;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

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

    List<Indisponibilidade> findByDataInicioLessThanEqualAndDataFimGreaterThanEqual(LocalDate dataFim,
                LocalDate dataInicio);
}