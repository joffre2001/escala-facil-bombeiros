package com.obysoft.escalafacil.dto;
import java.time.LocalDateTime;
import com.obysoft.escalafacil.enumeration.StatusTroca;
public record SolicitacaoTrocaResponse(Long id, Long escalaId, String escalaNome, Long itemEscalaId,
        LocalDateTime inicioPlantao, Long solicitanteId, String solicitanteNome, Long substitutoId,
        String substitutoNome, StatusTroca status, String motivo, LocalDateTime criadoEm) {}
