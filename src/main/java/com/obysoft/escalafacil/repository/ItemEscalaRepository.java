package com.obysoft.escalafacil.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.obysoft.escalafacil.entity.ItemEscala;

public interface ItemEscalaRepository
        extends JpaRepository<ItemEscala, Long> {

    Optional<ItemEscala> findByIdAndEscalaId(
            Long itemId,
            Long escalaId
    );

    boolean existsByBombeiroIdAndInicioPlantao(
            Long bombeiroId,
            LocalDateTime inicioPlantao
    );

    boolean existsByBombeiroId(Long bombeiroId);

    boolean existsByBombeiroIdAndInicioPlantaoBetweenAndIdNot(
            Long bombeiroId,
            LocalDateTime inicio,
            LocalDateTime fim,
            Long itemId
    );
}
