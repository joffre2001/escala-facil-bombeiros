package com.obysoft.escalafacil.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.obysoft.escalafacil.dto.HistoricoEscalaResponse;
import com.obysoft.escalafacil.entity.*;
import com.obysoft.escalafacil.exception.RecursoNaoEncontradoException;
import com.obysoft.escalafacil.repository.*;

@Service
public class AuditoriaEscalaService {
    private final HistoricoEscalaRepository historico;
    private final UsuarioRepository usuarios;
    public AuditoriaEscalaService(HistoricoEscalaRepository historico, UsuarioRepository usuarios) {
        this.historico=historico; this.usuarios=usuarios;
    }
    @Transactional
    public void registrar(Escala escala, ItemEscala item, String acao, String descricao, String email) {
        Usuario ator=usuarios.findByEmailIgnoreCase(email).orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        historico.save(new HistoricoEscala(escala,item,acao,descricao,ator.getEmail(),ator.getPerfil().name()));
    }
    @Transactional(readOnly=true)
    public List<HistoricoEscalaResponse> listar(Long escalaId) {
        return historico.findByEscalaIdOrderByCriadoEmDesc(escalaId).stream().map(h ->
            new HistoricoEscalaResponse(h.getId(),h.getEscalaId(),h.getItemEscalaId(),h.getAcao(),
                h.getDescricao(),h.getAtorEmail(),h.getAtorPerfil(),h.getCriadoEm())).toList();
    }
    @Transactional(readOnly=true)
    public List<HistoricoEscalaResponse> listarTudo() {
        return historico.findAllByOrderByCriadoEmDesc().stream().map(h ->
            new HistoricoEscalaResponse(h.getId(),h.getEscalaId(),h.getItemEscalaId(),h.getAcao(),
                h.getDescricao(),h.getAtorEmail(),h.getAtorPerfil(),h.getCriadoEm())).toList();
    }
}
