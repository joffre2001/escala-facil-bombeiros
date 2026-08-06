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
    public BombeiroService(BombeiroRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public List<BombeiroResponse> listar() { return repository.findAll().stream().map(this::response).toList(); }
    @Transactional(readOnly = true)
    public BombeiroResponse buscar(Long id) { return response(encontrar(id)); }
    @Transactional
    public BombeiroResponse criar(BombeiroRequest req) {
        if (repository.existsByMatriculaIgnoreCase(req.matricula())) throw new RegraNegocioException("Matrícula já cadastrada.");
        if (repository.existsByEmailIgnoreCase(req.email())) throw new RegraNegocioException("E-mail já cadastrado.");
        Bombeiro b = new Bombeiro(); preencher(b, req); return response(repository.save(b));
    }
    @Transactional
    public BombeiroResponse alterarStatus(Long id, StatusBombeiro status) {
        Bombeiro b = encontrar(id); b.setStatus(status); return response(b);
    }
    private Bombeiro encontrar(Long id) { return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Bombeiro não encontrado.")); }
    private void preencher(Bombeiro b, BombeiroRequest r) {
        b.setNomeCompleto(r.nomeCompleto().trim()); b.setMatricula(r.matricula().trim());
        b.setEmail(r.email().trim().toLowerCase()); b.setTelefone(r.telefone());
        b.setCargo(r.cargo().trim()); b.setEquipe(r.equipe()); b.setDataAdmissao(r.dataAdmissao());
    }
    private BombeiroResponse response(Bombeiro b) {
        return new BombeiroResponse(b.getId(), b.getNomeCompleto(), b.getMatricula(), b.getEmail(), b.getTelefone(), b.getCargo(), b.getEquipe(), b.getDataAdmissao(), b.getStatus(), b.getCriadoEm());
    }
}
