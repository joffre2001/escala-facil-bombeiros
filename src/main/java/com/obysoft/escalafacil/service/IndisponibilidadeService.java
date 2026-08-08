package com.obysoft.escalafacil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.obysoft.escalafacil.dto.IndisponibilidadeRequest;
import com.obysoft.escalafacil.dto.IndisponibilidadeResponse;
import com.obysoft.escalafacil.entity.Bombeiro;
import com.obysoft.escalafacil.entity.Indisponibilidade;
import com.obysoft.escalafacil.enumeration.TipoIndisponibilidade;
import com.obysoft.escalafacil.exception.RecursoNaoEncontradoException;
import com.obysoft.escalafacil.exception.RegraNegocioException;
import com.obysoft.escalafacil.repository.BombeiroRepository;
import com.obysoft.escalafacil.repository.IndisponibilidadeRepository;

@Service
public class IndisponibilidadeService {

    private final IndisponibilidadeRepository repository;
    private final BombeiroRepository bombeiroRepository;

    public IndisponibilidadeService(
            IndisponibilidadeRepository repository,
            BombeiroRepository bombeiroRepository) {

        this.repository = repository;
        this.bombeiroRepository = bombeiroRepository;
    }

    @Transactional(readOnly = true)
    public List<IndisponibilidadeResponse> listar() {
        return repository.findAll()
                .stream()
                .map(this::response)
                .toList();
    }

    @Transactional(readOnly = true)
    public IndisponibilidadeResponse buscar(Long id) {
        return response(encontrar(id));
    }

    @Transactional(readOnly = true)
    public List<IndisponibilidadeResponse> listarPorBombeiro(
            Long bombeiroId) {

        verificarBombeiro(bombeiroId);

        return repository
                .findByBombeiroIdOrderByDataInicioAsc(bombeiroId)
                .stream()
                .map(this::response)
                .toList();
    }

    @Transactional
    public IndisponibilidadeResponse criar(
            IndisponibilidadeRequest request) {

        validarDatas(request);
        validarSobreposicaoNaCriacao(request);

        Bombeiro bombeiro = verificarBombeiro(request.bombeiroId());
        boolean negociavel = definirNegociavel(request);

        Indisponibilidade indisponibilidade =
                new Indisponibilidade(
                        bombeiro,
                        request.tipo(),
                        request.dataInicio(),
                        request.dataFim(),
                        negociavel,
                        limparTextoOpcional(request.motivo())
                );

        return response(repository.save(indisponibilidade));
    }

    @Transactional
    public IndisponibilidadeResponse atualizar(
            Long id,
            IndisponibilidadeRequest request) {

        Indisponibilidade indisponibilidade = encontrar(id);

        if (!indisponibilidade.getBombeiro()
                .getId()
                .equals(request.bombeiroId())) {

            throw new RegraNegocioException(
                    "Não é permitido alterar o bombeiro da indisponibilidade."
            );
        }

        validarDatas(request);
        validarSobreposicaoNaAtualizacao(id, request);

        indisponibilidade.atualizar(
                request.tipo(),
                request.dataInicio(),
                request.dataFim(),
                definirNegociavel(request),
                limparTextoOpcional(request.motivo())
        );

        return response(indisponibilidade);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(encontrar(id));
    }

    private void validarDatas(IndisponibilidadeRequest request) {
        if (request.dataFim().isBefore(request.dataInicio())) {
            throw new RegraNegocioException(
                    "A data final não pode ser anterior à data inicial."
            );
        }
    }

    private void validarSobreposicaoNaCriacao(
            IndisponibilidadeRequest request) {

        boolean existeSobreposicao =
                repository
                        .existsByBombeiroIdAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
                                request.bombeiroId(),
                                request.dataFim(),
                                request.dataInicio()
                        );

        if (existeSobreposicao) {
            throw new RegraNegocioException(
                    "O bombeiro já possui uma indisponibilidade nesse período."
            );
        }
    }

    private void validarSobreposicaoNaAtualizacao(
            Long id,
            IndisponibilidadeRequest request) {

        boolean existeSobreposicao =
                repository
                        .existsByBombeiroIdAndDataInicioLessThanEqualAndDataFimGreaterThanEqualAndIdNot(
                                request.bombeiroId(),
                                request.dataFim(),
                                request.dataInicio(),
                                id
                        );

        if (existeSobreposicao) {
            throw new RegraNegocioException(
                    "O bombeiro já possui outra indisponibilidade nesse período."
            );
        }
    }

    private boolean definirNegociavel(
            IndisponibilidadeRequest request) {

        if (request.tipo() == TipoIndisponibilidade.FERIAS) {
            return false;
        }

        return request.negociavel() == null
                || request.negociavel();
    }

    private Bombeiro verificarBombeiro(Long id) {
        return bombeiroRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Bombeiro não encontrado."
                        )
                );
    }

    private Indisponibilidade encontrar(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Indisponibilidade não encontrada."
                        )
                );
    }

    private String limparTextoOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }

    private IndisponibilidadeResponse response(
            Indisponibilidade indisponibilidade) {

        return new IndisponibilidadeResponse(
                indisponibilidade.getId(),
                indisponibilidade.getBombeiro().getId(),
                indisponibilidade.getBombeiro().getNomeCompleto(),
                indisponibilidade.getTipo(),
                indisponibilidade.getDataInicio(),
                indisponibilidade.getDataFim(),
                indisponibilidade.isNegociavel(),
                indisponibilidade.getMotivo(),
                indisponibilidade.getCriadoEm()
        );
    }
}