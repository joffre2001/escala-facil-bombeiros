package com.obysoft.escalafacil.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.obysoft.escalafacil.enumeration.StatusEscala;

public record EscalaResponse(Long id, String nome, LocalDate dataInicio,
        LocalDate dataFim, StatusEscala status, LocalDateTime criadaEm,
        int totalDesignacoes, int totalAlertas, List<ItemEscalaResponse> itens) {
}