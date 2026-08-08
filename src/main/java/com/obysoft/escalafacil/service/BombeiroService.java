package com.obysoft.escalafacil.service;

import com.obysoft.escalafacil.dto.BombeiroRequest;
import com.obysoft.escalafacil.dto.BombeiroResponse;
import com.obysoft.escalafacil.entity.Bombeiro;
import com.obysoft.escalafacil.enumeration.StatusBombeiro;
import com.obysoft.escalafacil.exception.RecursoNaoEncontradoException;
import com.obysoft.escalafacil.exception.RegraNegocioException;
import com.obysoft.escalafacil.repository.BombeiroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BombeiroService {

    private final BombeiroRepository repository;

    public BombeiroService(BombeiroRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<BombeiroResponse> listar() {
        return repository.findAll()
                .stream()
                .map(this::response)
                .toList();
    }

    @Transactional(readOnly = true)
    public BombeiroResponse buscar(Long id) {
        return response(encontrar(id));
    }

    @Transactional
    public BombeiroResponse criar(BombeiroRequest request) {
        validarDadosUnicosNaCriacao(request);

        Bombeiro bombeiro = new Bombeiro();
        preencher(bombeiro, request);

        return response(repository.save(bombeiro));
    }

    @Transactional
    public BombeiroResponse atualizar(
            Long id,
            BombeiroRequest request) {

        Bombeiro bombeiro = encontrar(id);

        validarDadosUnicosNaAtualizacao(id, request);
        preencher(bombeiro, request);

        return response(bombeiro);
    }

    @Transactional
    public BombeiroResponse alterarStatus(
            Long id,
            StatusBombeiro status) {

        if (status == null) {
            throw new RegraNegocioException(
                    "O status do bombeiro é obrigatório."
            );
        }

        Bombeiro bombeiro = encontrar(id);
        bombeiro.setStatus(status);

        return response(bombeiro);
    }

    private void validarDadosUnicosNaCriacao(BombeiroRequest request) {
        String matricula = request.matricula().trim();
        String email = request.email().trim();

        if (repository.existsByMatriculaIgnoreCase(matricula)) {
            throw new RegraNegocioException(
                    "Matrícula já cadastrada."
            );
        }

        if (repository.existsByEmailIgnoreCase(email)) {
            throw new RegraNegocioException(
                    "E-mail já cadastrado."
            );
        }
    }

    private void validarDadosUnicosNaAtualizacao(
            Long id,
            BombeiroRequest request) {

        String matricula = request.matricula().trim();
        String email = request.email().trim();

        if (repository.existsByMatriculaIgnoreCaseAndIdNot(
                matricula, id)) {

            throw new RegraNegocioException(
                    "Matrícula já cadastrada para outro bombeiro."
            );
        }

        if (repository.existsByEmailIgnoreCaseAndIdNot(
                email, id)) {

            throw new RegraNegocioException(
                    "E-mail já cadastrado para outro bombeiro."
            );
        }
    }

    private Bombeiro encontrar(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Bombeiro não encontrado."
                        )
                );
    }

    private void preencher(
            Bombeiro bombeiro,
            BombeiroRequest request) {

        bombeiro.setNomeCompleto(
                request.nomeCompleto().trim()
        );

        bombeiro.setMatricula(
                request.matricula().trim()
        );

        bombeiro.setEmail(
                request.email().trim().toLowerCase()
        );

        bombeiro.setTelefone(
                limparTextoOpcional(request.telefone())
        );

        bombeiro.setCargo(
                request.cargo().trim()
        );

        bombeiro.setEquipe(
                limparTextoOpcional(request.equipe())
        );

        bombeiro.setDataAdmissao(
                request.dataAdmissao()
        );
    }

    private String limparTextoOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }

    private BombeiroResponse response(Bombeiro bombeiro) {
        return new BombeiroResponse(
                bombeiro.getId(),
                bombeiro.getNomeCompleto(),
                bombeiro.getMatricula(),
                bombeiro.getEmail(),
                bombeiro.getTelefone(),
                bombeiro.getCargo(),
                bombeiro.getEquipe(),
                bombeiro.getDataAdmissao(),
                bombeiro.getStatus(),
                bombeiro.getCriadoEm()
        );
    }
}