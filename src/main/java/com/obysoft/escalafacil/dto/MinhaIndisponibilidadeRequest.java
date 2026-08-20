package com.obysoft.escalafacil.dto;

import java.time.LocalDate;
import com.obysoft.escalafacil.enumeration.TipoIndisponibilidade;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MinhaIndisponibilidadeRequest(
        @NotNull TipoIndisponibilidade tipo,
        @NotNull LocalDate dataInicio,
        @NotNull LocalDate dataFim,
        Boolean negociavel,
        @Size(max = 255) String motivo) {
}
