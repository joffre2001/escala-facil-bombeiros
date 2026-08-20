package com.obysoft.escalafacil.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.obysoft.escalafacil.entity.HistoricoEscala;

public interface HistoricoEscalaRepository extends JpaRepository<HistoricoEscala, Long> {
    List<HistoricoEscala> findByEscalaIdOrderByCriadoEmDesc(Long escalaId);
    List<HistoricoEscala> findAllByOrderByCriadoEmDesc();
}
