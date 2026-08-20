package com.obysoft.escalafacil.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.obysoft.escalafacil.dto.EscalaResponse;
import com.obysoft.escalafacil.dto.IndisponibilidadeRequest;
import com.obysoft.escalafacil.dto.IndisponibilidadeResponse;
import com.obysoft.escalafacil.dto.MinhaIndisponibilidadeRequest;
import com.obysoft.escalafacil.entity.Usuario;
import com.obysoft.escalafacil.exception.RecursoNaoEncontradoException;
import com.obysoft.escalafacil.exception.RegraNegocioException;
import com.obysoft.escalafacil.repository.UsuarioRepository;

@Service
public class MeuPortalService {
    private final UsuarioRepository usuarios;
    private final IndisponibilidadeService indisponibilidades;
    private final EscalaService escalas;

    public MeuPortalService(UsuarioRepository usuarios,
            IndisponibilidadeService indisponibilidades, EscalaService escalas) {
        this.usuarios = usuarios;
        this.indisponibilidades = indisponibilidades;
        this.escalas = escalas;
    }

    @Transactional(readOnly = true)
    public Long bombeiroId(String email) { return usuarioBombeiro(email).getBombeiro().getId(); }

    public List<EscalaResponse> escalas(String email) {
        return escalas.listarPublicadasPorBombeiro(bombeiroId(email));
    }

    public List<IndisponibilidadeResponse> indisponibilidades(String email) {
        return indisponibilidades.listarPorBombeiro(bombeiroId(email));
    }

    public IndisponibilidadeResponse criar(String email, MinhaIndisponibilidadeRequest request) {
        return indisponibilidades.criar(converter(bombeiroId(email), request));
    }

    public IndisponibilidadeResponse atualizar(String email, Long id,
            MinhaIndisponibilidadeRequest request) {
        validarProprietario(email, id);
        return indisponibilidades.atualizar(id, converter(bombeiroId(email), request));
    }

    public void excluir(String email, Long id) {
        validarProprietario(email, id);
        indisponibilidades.excluir(id);
    }

    private void validarProprietario(String email, Long id) {
        if (!indisponibilidades.buscar(id).bombeiroId().equals(bombeiroId(email))) {
            throw new RegraNegocioException("Você só pode alterar suas próprias indisponibilidades.");
        }
    }

    private IndisponibilidadeRequest converter(Long bombeiroId,
            MinhaIndisponibilidadeRequest request) {
        return new IndisponibilidadeRequest(bombeiroId, request.tipo(), request.dataInicio(),
                request.dataFim(), request.negociavel(), request.motivo());
    }

    private Usuario usuarioBombeiro(String email) {
        Usuario usuario = usuarios.findByEmailIgnoreCase(email).orElseThrow(() ->
                new RecursoNaoEncontradoException("Usuário não encontrado."));
        if (usuario.getBombeiro() == null) {
            throw new RegraNegocioException("Este usuário não está vinculado a um bombeiro.");
        }
        return usuario;
    }
}
