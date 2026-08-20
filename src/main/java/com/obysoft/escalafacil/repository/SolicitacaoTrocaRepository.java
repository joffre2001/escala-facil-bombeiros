package com.obysoft.escalafacil.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.obysoft.escalafacil.entity.SolicitacaoTroca;
import com.obysoft.escalafacil.enumeration.StatusTroca;

public interface SolicitacaoTrocaRepository extends JpaRepository<SolicitacaoTroca, Long> {
    boolean existsByItemEscalaIdAndStatusIn(Long itemId, List<StatusTroca> status);
    List<SolicitacaoTroca> findByItemEscalaIdAndStatusIn(Long itemId, List<StatusTroca> status);
    List<SolicitacaoTroca> findBySolicitanteIdOrSubstitutoIdOrderByCriadoEmDesc(Long solicitanteId, Long substitutoId);
    List<SolicitacaoTroca> findAllByOrderByCriadoEmDesc();
}
