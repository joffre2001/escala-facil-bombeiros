package com.obysoft.escalafacil.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record BombeiroRequest(
        @NotBlank @Size(max = 150) String nomeCompleto,
        @NotBlank @Size(max = 30) String matricula,
        @NotBlank @Email @Size(max = 150) String email,
        @Size(max = 30) String telefone,
        @NotBlank @Size(max = 80) String cargo,
        @Size(max = 80) String equipe,
        @PastOrPresent LocalDate dataAdmissao
) {}
