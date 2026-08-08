package com.obysoft.escalafacil.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import com.obysoft.escalafacil.enumeration.StatusBombeiro;

public record BombeiroResponse(Long id, String nomeCompleto, String matricula, String email,
        String telefone, String cargo, String equipe, LocalDate dataAdmissao,
        StatusBombeiro status, OffsetDateTime criadoEm) {}
