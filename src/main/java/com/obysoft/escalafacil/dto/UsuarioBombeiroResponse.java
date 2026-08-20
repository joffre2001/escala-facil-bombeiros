package com.obysoft.escalafacil.dto;

import com.obysoft.escalafacil.enumeration.PerfilUsuario;

public record UsuarioBombeiroResponse(
        Long usuarioId,
        Long bombeiroId,
        String nome,
        String email,
        PerfilUsuario perfil,
        boolean ativo) {
}