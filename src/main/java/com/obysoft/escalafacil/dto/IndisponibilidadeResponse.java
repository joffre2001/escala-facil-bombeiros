package com.obysoft.escalafacil.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.obysoft.escalafacil.enumeration.TipoIndisponibilidade;

public record IndisponibilidadeResponse(

        Long id,
        Long bombeiroId,
        String nomeBombeiro,
        TipoIndisponibilidade tipo,
        LocalDate dataInicio,
        LocalDate dataFim,
        boolean negociavel,
        String motivo,
        LocalDateTime criadoEm

) {
}