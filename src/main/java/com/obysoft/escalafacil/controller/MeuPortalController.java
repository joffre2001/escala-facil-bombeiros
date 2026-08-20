package com.obysoft.escalafacil.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.obysoft.escalafacil.dto.EscalaResponse;
import com.obysoft.escalafacil.dto.IndisponibilidadeResponse;
import com.obysoft.escalafacil.dto.MinhaIndisponibilidadeRequest;
import com.obysoft.escalafacil.service.MeuPortalService;
import com.obysoft.escalafacil.service.TrocaTurnoService;
import com.obysoft.escalafacil.repository.BombeiroRepository;
import com.obysoft.escalafacil.enumeration.StatusBombeiro;
import com.obysoft.escalafacil.dto.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/me")
@PreAuthorize("hasRole('BOMBEIRO')")
public class MeuPortalController {
    private final MeuPortalService service;
    private final TrocaTurnoService trocas;
    private final BombeiroRepository bombeiros;
    public MeuPortalController(MeuPortalService service, TrocaTurnoService trocas, BombeiroRepository bombeiros) {
        this.service = service; this.trocas = trocas; this.bombeiros = bombeiros;
    }

    @GetMapping("/schedules")
    public List<EscalaResponse> escalas(Principal principal) { return service.escalas(principal.getName()); }

    @GetMapping("/unavailabilities")
    public List<IndisponibilidadeResponse> indisponibilidades(Principal principal) {
        return service.indisponibilidades(principal.getName());
    }

    @PostMapping("/unavailabilities")
    public ResponseEntity<IndisponibilidadeResponse> criar(Principal principal,
            @Valid @RequestBody MinhaIndisponibilidadeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(principal.getName(), request));
    }

    @PutMapping("/unavailabilities/{id}")
    public IndisponibilidadeResponse atualizar(Principal principal, @PathVariable Long id,
            @Valid @RequestBody MinhaIndisponibilidadeRequest request) {
        return service.atualizar(principal.getName(), id, request);
    }

    @DeleteMapping("/unavailabilities/{id}")
    public ResponseEntity<Void> excluir(Principal principal, @PathVariable Long id) {
        service.excluir(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/firefighters")
    public List<BombeiroOpcaoResponse> bombeiros() {
        return bombeiros.findByStatusOrderByNomeCompletoAsc(StatusBombeiro.ATIVO).stream()
                .map(b -> new BombeiroOpcaoResponse(b.getId(),b.getNomeCompleto(),b.getEquipe())).toList();
    }

    @GetMapping("/shift-exchanges")
    public List<SolicitacaoTrocaResponse> trocas(Principal principal) { return trocas.minhas(principal.getName()); }

    @PostMapping("/shift-exchanges")
    public ResponseEntity<SolicitacaoTrocaResponse> solicitarTroca(Principal principal,
            @Valid @RequestBody SolicitarTrocaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trocas.solicitar(principal.getName(),request));
    }

    @PatchMapping("/shift-exchanges/{id}/response")
    public SolicitacaoTrocaResponse responderTroca(Principal principal,@PathVariable Long id,
            @RequestParam boolean aceitar) { return trocas.responder(principal.getName(),id,aceitar); }
}
