package com.obysoft.escalafacil.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.obysoft.escalafacil.dto.*;
import com.obysoft.escalafacil.entity.*;
import com.obysoft.escalafacil.enumeration.*;
import com.obysoft.escalafacil.exception.*;
import com.obysoft.escalafacil.repository.*;

@Service
public class TrocaTurnoService {
    private final SolicitacaoTrocaRepository trocas; private final ItemEscalaRepository itens;
    private final BombeiroRepository bombeiros; private final UsuarioRepository usuarios;
    private final AuditoriaEscalaService auditoria;
    public TrocaTurnoService(SolicitacaoTrocaRepository trocas, ItemEscalaRepository itens,
            BombeiroRepository bombeiros, UsuarioRepository usuarios, AuditoriaEscalaService auditoria) {
        this.trocas=trocas; this.itens=itens; this.bombeiros=bombeiros; this.usuarios=usuarios; this.auditoria=auditoria;
    }
    @Transactional
    public SolicitacaoTrocaResponse solicitar(String email, SolicitarTrocaRequest req) {
        Bombeiro solicitante=bombeiro(email); ItemEscala item=item(req.itemEscalaId());
        if(!item.getBombeiro().getId().equals(solicitante.getId())) throw new RegraNegocioException("Você só pode trocar seus próprios plantões.");
        if(item.getEscala().getStatus()!=StatusEscala.PUBLICADA) throw new RegraNegocioException("Somente plantões publicados podem ser trocados.");
        if(item.isCancelado()) throw new RegraNegocioException("Um plantão cancelado não pode ser trocado.");
        if(trocas.existsByItemEscalaIdAndStatusIn(item.getId(), List.of(StatusTroca.AGUARDANDO_ACEITE,StatusTroca.AGUARDANDO_APROVACAO)))
            throw new RegraNegocioException("Já existe uma solicitação aberta para este plantão.");
        Bombeiro substituto=bombeiros.findById(req.substitutoId()).orElseThrow(() -> new RecursoNaoEncontradoException("Bombeiro substituto não encontrado."));
        if(substituto.getStatus()!=StatusBombeiro.ATIVO || substituto.getId().equals(solicitante.getId()))
            throw new RegraNegocioException("Selecione outro bombeiro ativo.");
        SolicitacaoTroca troca=trocas.save(new SolicitacaoTroca(item,solicitante,substituto,limpar(req.motivo())));
        auditoria.registrar(item.getEscala(),item,"TROCA_SOLICITADA",solicitante.getNomeCompleto()+" solicitou troca com "+substituto.getNomeCompleto()+".",email);
        return response(troca);
    }
    @Transactional(readOnly=true)
    public List<SolicitacaoTrocaResponse> minhas(String email) {
        Long id=bombeiro(email).getId(); return trocas.findBySolicitanteIdOrSubstitutoIdOrderByCriadoEmDesc(id,id).stream().map(this::response).toList();
    }
    @Transactional(readOnly=true)
    public List<SolicitacaoTrocaResponse> todas() { return trocas.findAllByOrderByCriadoEmDesc().stream().map(this::response).toList(); }
    @Transactional
    public SolicitacaoTrocaResponse responder(String email,Long id,boolean aceitar) {
        Bombeiro ator=bombeiro(email); SolicitacaoTroca troca=encontrar(id);
        if(!troca.getSubstituto().getId().equals(ator.getId())) throw new RegraNegocioException("Somente o substituto indicado pode responder.");
        if(troca.getStatus()!=StatusTroca.AGUARDANDO_ACEITE) throw new RegraNegocioException("Esta solicitação já foi respondida.");
        troca.alterarStatus(aceitar?StatusTroca.AGUARDANDO_APROVACAO:StatusTroca.RECUSADA);
        auditoria.registrar(troca.getItemEscala().getEscala(),troca.getItemEscala(),aceitar?"TROCA_ACEITA":"TROCA_RECUSADA",
                ator.getNomeCompleto()+(aceitar?" aceitou":" recusou")+" a solicitação de troca.",email);
        return response(troca);
    }
    @Transactional
    public SolicitacaoTrocaResponse decidir(String email,Long id,DecisaoRequest req) {
        SolicitacaoTroca troca=encontrar(id);
        if(troca.getStatus()!=StatusTroca.AGUARDANDO_APROVACAO) throw new RegraNegocioException("A troca ainda não está aguardando aprovação.");
        if(req.aprovar()) {
            ItemEscala item=troca.getItemEscala(); Bombeiro substituto=troca.getSubstituto();
            if(itens.existsByBombeiroIdAndInicioPlantao(substituto.getId(),item.getInicioPlantao()))
                throw new RegraNegocioException("O substituto já possui plantão neste horário.");
            item.trocarBombeiro(substituto,false,"Troca aprovada: "+req.motivo().trim());
            troca.alterarStatus(StatusTroca.APROVADA);
        } else troca.alterarStatus(StatusTroca.RECUSADA);
        auditoria.registrar(troca.getItemEscala().getEscala(),troca.getItemEscala(),req.aprovar()?"TROCA_APROVADA":"TROCA_REPROVADA",
                req.motivo().trim(),email);
        return response(troca);
    }
    private Bombeiro bombeiro(String email){Usuario u=usuarios.findByEmailIgnoreCase(email).orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado.")); if(u.getBombeiro()==null) throw new RegraNegocioException("Usuário sem bombeiro vinculado."); return u.getBombeiro();}
    private ItemEscala item(Long id){return itens.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Plantão não encontrado."));}
    private SolicitacaoTroca encontrar(Long id){return trocas.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação não encontrada."));}
    private String limpar(String s){return s==null||s.isBlank()?null:s.trim();}
    private SolicitacaoTrocaResponse response(SolicitacaoTroca t){ItemEscala i=t.getItemEscala(); return new SolicitacaoTrocaResponse(t.getId(),i.getEscala().getId(),i.getEscala().getNome(),i.getId(),i.getInicioPlantao(),t.getSolicitante().getId(),t.getSolicitante().getNomeCompleto(),t.getSubstituto().getId(),t.getSubstituto().getNomeCompleto(),t.getStatus(),t.getMotivo(),t.getCriadoEm());}
}
