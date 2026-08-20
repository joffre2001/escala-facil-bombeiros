package com.obysoft.escalafacil.controller;

import java.net.URI;
import java.util.List;
import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.obysoft.escalafacil.dto.EscalaResponse;
import com.obysoft.escalafacil.dto.GerarEscalaRequest;
import com.obysoft.escalafacil.dto.TrocarBombeiroRequest;
import com.obysoft.escalafacil.dto.CancelarTurnoRequest;
import com.obysoft.escalafacil.dto.HistoricoEscalaResponse;
import com.obysoft.escalafacil.service.AuditoriaEscalaService;
import com.obysoft.escalafacil.service.EscalaService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/schedules")
@PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
public class EscalaController {

    private final EscalaService service;
    private final AuditoriaEscalaService auditoria;

    public EscalaController(EscalaService service, AuditoriaEscalaService auditoria) {
        this.service = service;
        this.auditoria = auditoria;
    }

    @GetMapping
    public List<EscalaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public EscalaResponse buscar(
            @PathVariable Long id) {

        return service.buscar(id);
    }

    @PostMapping("/generate")
    public ResponseEntity<EscalaResponse> gerar(
            Principal principal,
            @Valid
            @RequestBody GerarEscalaRequest request) {

        EscalaResponse response =
                service.gerar(request, principal.getName());

        return ResponseEntity
                .created(
                        URI.create(
                                "/schedules/"
                                        + response.id()
                        )
                )
                .body(response);
    }

    @PatchMapping(
            "/{escalaId}/items/{itemId}/firefighter"
    )
    public EscalaResponse trocarBombeiro(
            Principal principal,
            @PathVariable Long escalaId,
            @PathVariable Long itemId,
            @Valid
            @RequestBody
            TrocarBombeiroRequest request) {

        return service.trocarBombeiro(
                escalaId,
                itemId,
                request.bombeiroId(),
                principal.getName()
        );
    }

    @PatchMapping("/{id}/publish")
    public EscalaResponse publicar(
            Principal principal,
            @PathVariable Long id) {

        return service.publicar(id, principal.getName());
    }

    @PatchMapping("/{escalaId}/items/{itemId}/cancel")
    public EscalaResponse cancelarTurno(Principal principal,@PathVariable Long escalaId,
            @PathVariable Long itemId,@Valid @RequestBody CancelarTurnoRequest request) {
        return service.cancelarTurno(escalaId,itemId,request.motivo(),principal.getName());
    }

    @GetMapping("/{id}/history")
    public List<HistoricoEscalaResponse> historico(@PathVariable Long id){return auditoria.listar(id);}

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(
            Principal principal,
            @PathVariable Long id) {

        service.excluir(id, principal.getName());

        return ResponseEntity
                .noContent()
                .build();
    }
}
