package com.obysoft.escalafacil.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public record SolicitarTrocaRequest(@NotNull Long itemEscalaId, @NotNull Long substitutoId, @Size(max=255) String motivo) {}
