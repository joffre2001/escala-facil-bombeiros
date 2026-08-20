package com.obysoft.escalafacil.dto;
import java.time.LocalDateTime;
public record HistoricoEscalaResponse(Long id, Long escalaId, Long itemEscalaId, String acao, String descricao,
        String atorEmail, String atorPerfil, LocalDateTime criadoEm) {}
