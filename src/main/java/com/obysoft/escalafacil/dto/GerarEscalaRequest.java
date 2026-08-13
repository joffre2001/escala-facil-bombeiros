package com.obysoft.escalafacil.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GerarEscalaRequest(
        @NotBlank @Size(max = 120) String nome,
        @NotNull @FutureOrPresent LocalDate dataInicio,
        @NotNull LocalDate dataFim,
        @Min(1) @Max(20) int quantidadePorPlantao) {
}