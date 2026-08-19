package com.obysoft.escalafacil.dto;

import jakarta.validation.constraints.NotNull;

public record TrocarBombeiroRequest(
        @NotNull(message = "O bombeiro é obrigatório.")
        Long bombeiroId) {
}