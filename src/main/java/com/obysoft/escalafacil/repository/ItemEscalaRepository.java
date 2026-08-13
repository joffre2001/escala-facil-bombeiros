package com.obysoft.escalafacil.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.obysoft.escalafacil.entity.ItemEscala;

public interface ItemEscalaRepository extends JpaRepository<ItemEscala, Long> {

    boolean existsByBombeiroIdAndInicioPlantao(
            Long bombeiroId, LocalDateTime inicioPlantao);
}