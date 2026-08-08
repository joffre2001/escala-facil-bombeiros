package com.obysoft.escalafacil.dto;

import com.obysoft.escalafacil.enumeration.PerfilUsuario;

public record LoginResponse(

        String token,
        String tipo,
        Long usuarioId,
        String nome,
        String email,
        PerfilUsuario perfil

) {
}