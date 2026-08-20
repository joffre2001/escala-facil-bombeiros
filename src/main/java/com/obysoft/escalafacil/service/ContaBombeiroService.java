package com.obysoft.escalafacil.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.obysoft.escalafacil.dto.UsuarioBombeiroResponse;
import com.obysoft.escalafacil.entity.Bombeiro;
import com.obysoft.escalafacil.entity.Usuario;
import com.obysoft.escalafacil.enumeration.StatusBombeiro;
import com.obysoft.escalafacil.exception.RecursoNaoEncontradoException;
import com.obysoft.escalafacil.exception.RegraNegocioException;
import com.obysoft.escalafacil.repository.BombeiroRepository;
import com.obysoft.escalafacil.repository.UsuarioRepository;

@Service
public class ContaBombeiroService {

    private final BombeiroRepository bombeiroRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public ContaBombeiroService(
            BombeiroRepository bombeiroRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.bombeiroRepository =
                bombeiroRepository;

        this.usuarioRepository =
                usuarioRepository;

        this.passwordEncoder =
                passwordEncoder;
    }

    @Transactional
    public UsuarioBombeiroResponse criarConta(
            Long bombeiroId,
            String senhaTemporaria) {

        Bombeiro bombeiro = bombeiroRepository
                .findById(bombeiroId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Bombeiro não encontrado."
                        )
                );

        if (bombeiro.getStatus()
                != StatusBombeiro.ATIVO) {

            throw new RegraNegocioException(
                    "Somente bombeiros ativos "
                            + "podem receber uma conta."
            );
        }

        if (usuarioRepository
                .existsByBombeiroId(bombeiroId)) {

            throw new RegraNegocioException(
                    "Este bombeiro já possui uma conta."
            );
        }

        if (usuarioRepository
                .existsByEmailIgnoreCase(
                        bombeiro.getEmail()
                )) {

            throw new RegraNegocioException(
                    "Já existe um usuário com o e-mail "
                            + bombeiro.getEmail() + "."
            );
        }

        String senhaCriptografada =
                passwordEncoder.encode(
                        senhaTemporaria
                );

        Usuario usuario = new Usuario(
                bombeiro.getNomeCompleto(),
                bombeiro.getEmail(),
                senhaCriptografada,
                bombeiro
        );

        Usuario salvo =
                usuarioRepository.save(usuario);

        return response(salvo);
    }

    @Transactional(readOnly = true)
    public UsuarioBombeiroResponse buscarPorBombeiro(
            Long bombeiroId) {

        Usuario usuario = usuarioRepository
                .findByBombeiroId(bombeiroId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Conta do bombeiro não encontrada."
                        )
                );

        return response(usuario);
    }

    private UsuarioBombeiroResponse response(
            Usuario usuario) {

        return new UsuarioBombeiroResponse(
                usuario.getId(),
                usuario.getBombeiro().getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil(),
                usuario.isAtivo()
        );
    }
}