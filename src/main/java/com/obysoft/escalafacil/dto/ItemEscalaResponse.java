package com.obysoft.escalafacil.dto;

import java.time.LocalDateTime;

public record ItemEscalaResponse(Long id, Long bombeiroId, String bombeiroNome,
        LocalDateTime inicioPlantao, LocalDateTime fimPlantao,
        boolean conflito, String observacao) {
}