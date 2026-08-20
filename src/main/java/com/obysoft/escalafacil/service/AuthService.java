package com.obysoft.escalafacil.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.obysoft.escalafacil.dto.LoginRequest;
import com.obysoft.escalafacil.dto.LoginResponse;
import com.obysoft.escalafacil.entity.Usuario;
import com.obysoft.escalafacil.repository.UsuarioRepository;
import com.obysoft.escalafacil.security.JwtService;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            request.senha()
                    )
            );
        } catch (AuthenticationException exception) {
            throw new IllegalArgumentException(
                    "E-mail ou senha inválidos."
            );
        }

        Usuario usuario = usuarioRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "E-mail ou senha inválidos."
                        )
                );

        String token = jwtService.gerarToken(usuario);

        return new LoginResponse(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil(),
                usuario.getBombeiro() == null ? null : usuario.getBombeiro().getId()
        );
    }
}
