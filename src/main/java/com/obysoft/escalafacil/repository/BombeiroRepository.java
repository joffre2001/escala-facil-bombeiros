package com.obysoft.escalafacil.repository;

import com.obysoft.escalafacil.entity.Bombeiro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BombeiroRepository extends JpaRepository<Bombeiro, Long> {
    boolean existsByMatriculaIgnoreCase(String matricula);
    boolean existsByEmailIgnoreCase(String email);
}
