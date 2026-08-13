package com.obysoft.escalafacil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.obysoft.escalafacil.entity.Bombeiro;
import com.obysoft.escalafacil.enumeration.StatusBombeiro;

public interface BombeiroRepository extends JpaRepository<Bombeiro, Long> {

        boolean existsByMatriculaIgnoreCase(String matricula);

        boolean existsByEmailIgnoreCase(String email);

        List<Bombeiro> findByStatusOrderByNomeCompletoAsc(StatusBombeiro status);

        boolean existsByMatriculaIgnoreCaseAndIdNot(
                        String matricula,
                        Long id);

        boolean existsByEmailIgnoreCaseAndIdNot(
                        String email,
                        Long id);
}