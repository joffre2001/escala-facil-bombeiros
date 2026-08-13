package com.obysoft.escalafacil.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.obysoft.escalafacil.dto.EscalaResponse;
import com.obysoft.escalafacil.dto.GerarEscalaRequest;
import com.obysoft.escalafacil.dto.ItemEscalaResponse;
import com.obysoft.escalafacil.entity.Bombeiro;
import com.obysoft.escalafacil.entity.Escala;
import com.obysoft.escalafacil.entity.Indisponibilidade;
import com.obysoft.escalafacil.entity.ItemEscala;
import com.obysoft.escalafacil.enumeration.StatusBombeiro;
import com.obysoft.escalafacil.enumeration.StatusEscala;
import com.obysoft.escalafacil.exception.RecursoNaoEncontradoException;
import com.obysoft.escalafacil.exception.RegraNegocioException;
import com.obysoft.escalafacil.repository.BombeiroRepository;
import com.obysoft.escalafacil.repository.EscalaRepository;
import com.obysoft.escalafacil.repository.IndisponibilidadeRepository;
import com.obysoft.escalafacil.repository.ItemEscalaRepository;

@Service
public class EscalaService {

    private final EscalaRepository escalaRepository;
    private final BombeiroRepository bombeiroRepository;
    private final IndisponibilidadeRepository indisponibilidadeRepository;
    private final ItemEscalaRepository itemEscalaRepository;

    public EscalaService(EscalaRepository escalaRepository,
            BombeiroRepository bombeiroRepository,
            IndisponibilidadeRepository indisponibilidadeRepository,
            ItemEscalaRepository itemEscalaRepository) {
        this.escalaRepository = escalaRepository;
        this.bombeiroRepository = bombeiroRepository;
        this.indisponibilidadeRepository = indisponibilidadeRepository;
        this.itemEscalaRepository = itemEscalaRepository;
    }

    @Transactional(readOnly = true)
    public List<EscalaResponse> listar() {
        return escalaRepository.findAllByOrderByDataInicioDesc().stream()
                .map(this::response).toList();
    }

    @Transactional(readOnly = true)
    public EscalaResponse buscar(Long id) {
        return response(encontrar(id));
    }

    @Transactional
    public EscalaResponse gerar(GerarEscalaRequest request) {
        validar(request);

        List<Bombeiro> bombeiros = bombeiroRepository
                .findByStatusOrderByNomeCompletoAsc(StatusBombeiro.ATIVO);
        if (bombeiros.isEmpty()) {
            throw new RegraNegocioException("Não existem bombeiros ativos para gerar a escala.");
        }

        List<Indisponibilidade> indisponibilidades = indisponibilidadeRepository
                .findByDataInicioLessThanEqualAndDataFimGreaterThanEqual(
                        request.dataFim(), request.dataInicio());

        Map<Long, List<Indisponibilidade>> porBombeiro = new HashMap<>();
        indisponibilidades.forEach(item -> porBombeiro
                .computeIfAbsent(item.getBombeiro().getId(), chave -> new ArrayList<>())
                .add(item));

        Escala escala = new Escala(request.nome().trim(),
                request.dataInicio(), request.dataFim());
        Map<Long, Integer> totalPlantoes = new HashMap<>();
        Map<Long, LocalDate> ultimoPlantao = new HashMap<>();

        for (LocalDate data = request.dataInicio(); !data.isAfter(request.dataFim());
                data = data.plusDays(1)) {
            Set<Long> escaladosNoDia = new HashSet<>();

            for (int vaga = 0; vaga < request.quantidadePorPlantao(); vaga++) {
                Candidato candidato = selecionar(bombeiros, porBombeiro,
                        totalPlantoes, ultimoPlantao, escaladosNoDia, data);

                if (candidato == null) {
                    escala.registrarVagaNaoPreenchida();
                    continue;
                }

                String observacao = candidato.conflito()
                        ? "Indisponibilidade negociável — confirmar com o bombeiro."
                        : null;
                LocalDateTime inicio = RegraPlantao24h.inicio(data);
                LocalDateTime fim = RegraPlantao24h.fim(data);
                escala.adicionarItem(new ItemEscala(escala, candidato.bombeiro(),
                        inicio, fim, candidato.conflito(), observacao));
                escaladosNoDia.add(candidato.bombeiro().getId());
                ultimoPlantao.put(candidato.bombeiro().getId(), data);
                totalPlantoes.merge(candidato.bombeiro().getId(), 1, Integer::sum);
            }
        }

        return response(escalaRepository.save(escala));
    }

    @Transactional
    public EscalaResponse publicar(Long id) {
        Escala escala = encontrar(id);
        if (escala.getStatus() == StatusEscala.PUBLICADA) {
            throw new RegraNegocioException("A escala já está publicada.");
        }
        if (totalAlertas(escala) > 0) {
            throw new RegraNegocioException(
                    "Resolva as vagas não preenchidas e os conflitos antes de publicar.");
        }
        escala.publicar();
        return response(escala);
    }

    @Transactional
    public void excluir(Long id) {
        Escala escala = encontrar(id);
        if (escala.getStatus() == StatusEscala.PUBLICADA) {
            throw new RegraNegocioException("Uma escala publicada não pode ser excluída.");
        }
        escalaRepository.delete(escala);
    }

    private Candidato selecionar(List<Bombeiro> bombeiros,
            Map<Long, List<Indisponibilidade>> porBombeiro,
            Map<Long, Integer> totalPlantoes, Map<Long, LocalDate> ultimoPlantao,
            Set<Long> escaladosNoDia, LocalDate data) {
        Comparator<Bombeiro> equilibrio = Comparator
                .comparingInt((Bombeiro b) -> totalPlantoes.getOrDefault(b.getId(), 0))
                .thenComparing(Bombeiro::getNomeCompleto);

        List<Bombeiro> livres = bombeiros.stream()
                .filter(b -> !escaladosNoDia.contains(b.getId()))
                .filter(b -> podeTrabalharSemDobrar(
                        b.getId(), ultimoPlantao.get(b.getId()), data))
                .filter(b -> indisponibilidadeNaData(porBombeiro.get(b.getId()), data) == null)
                .sorted(equilibrio).toList();
        if (!livres.isEmpty()) return new Candidato(livres.get(0), false);

        List<Bombeiro> negociaveis = bombeiros.stream()
                .filter(b -> !escaladosNoDia.contains(b.getId()))
                .filter(b -> podeTrabalharSemDobrar(
                        b.getId(), ultimoPlantao.get(b.getId()), data))
                .filter(b -> {
                    Indisponibilidade i = indisponibilidadeNaData(porBombeiro.get(b.getId()), data);
                    return i != null && i.isNegociavel();
                })
                .sorted(equilibrio).toList();
        return negociaveis.isEmpty() ? null : new Candidato(negociaveis.get(0), true);
    }

    private boolean podeTrabalharSemDobrar(
            Long bombeiroId, LocalDate ultimoPlantao, LocalDate dataAtual) {
        if (!RegraPlantao24h.podeEscalar(ultimoPlantao, dataAtual)) {
            return false;
        }

        LocalDateTime inicioPlantaoAnterior = RegraPlantao24h
                .inicio(dataAtual.minusDays(1));
        return !itemEscalaRepository.existsByBombeiroIdAndInicioPlantao(
                bombeiroId, inicioPlantaoAnterior);
    }

    private Indisponibilidade indisponibilidadeNaData(
            List<Indisponibilidade> itens, LocalDate data) {
        if (itens == null) return null;
        return itens.stream()
                .filter(i -> !data.isBefore(i.getDataInicio()) && !data.isAfter(i.getDataFim()))
                .findFirst().orElse(null);
    }

    private void validar(GerarEscalaRequest request) {
        if (request.dataFim().isBefore(request.dataInicio())) {
            throw new RegraNegocioException("A data final não pode ser anterior à inicial.");
        }
        if (escalaRepository.existsByDataInicioLessThanEqualAndDataFimGreaterThanEqual(
                request.dataFim(), request.dataInicio())) {
            throw new RegraNegocioException("Já existe uma escala nesse período.");
        }
    }

    private Escala encontrar(Long id) {
        return escalaRepository.findById(id).orElseThrow(() ->
                new RecursoNaoEncontradoException("Escala não encontrada."));
    }

    private int totalAlertas(Escala escala) {
        return escala.getVagasNaoPreenchidas()
                + (int) escala.getItens().stream().filter(ItemEscala::isConflito).count();
    }

    private EscalaResponse response(Escala escala) {
        List<ItemEscalaResponse> itens = escala.getItens().stream()
                .sorted(Comparator.comparing(ItemEscala::getInicioPlantao)
                        .thenComparing(i -> i.getBombeiro().getNomeCompleto()))
                .map(i -> new ItemEscalaResponse(i.getId(), i.getBombeiro().getId(),
                        i.getBombeiro().getNomeCompleto(), i.getInicioPlantao(),
                        i.getFimPlantao(),
                        i.isConflito(), i.getObservacao()))
                .toList();
        return new EscalaResponse(escala.getId(), escala.getNome(), escala.getDataInicio(),
                escala.getDataFim(), escala.getStatus(), escala.getCriadaEm(), itens.size(),
                totalAlertas(escala), itens);
    }

    private record Candidato(Bombeiro bombeiro, boolean conflito) {}
}