package com.obysoft.escalafacil.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record CancelarTurnoRequest(@NotBlank @Size(max=255) String motivo) {}
