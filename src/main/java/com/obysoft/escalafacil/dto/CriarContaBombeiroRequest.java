package com.obysoft.escalafacil.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarContaBombeiroRequest(

        @NotBlank(
                message = "A senha temporária é obrigatória."
        )
        @Size(
                min = 8,
                max = 72,
                message = "A senha deve ter entre 8 e 72 caracteres."
        )
        String senhaTemporaria) {
}