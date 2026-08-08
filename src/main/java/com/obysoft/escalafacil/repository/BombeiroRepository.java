package com.obysoft.escalafacil.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.obysoft.escalafacil.entity.Bombeiro;

public interface BombeiroRepository extends JpaRepository<Bombeiro, Long> {

    boolean existsByMatriculaIgnoreCase(String matricula);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByMatriculaIgnoreCaseAndIdNot(
            String matricula,
            Long id
    );

    boolean existsByEmailIgnoreCaseAndIdNot(
            String email,
            Long id
    );
}