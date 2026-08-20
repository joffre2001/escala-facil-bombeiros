package com.obysoft.escalafacil.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record DecisaoRequest(boolean aprovar, @NotBlank @Size(max=255) String motivo) {}
