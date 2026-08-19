package com.obysoft.escalafacil.controller;

import java.net.URI;
import java.util.List;

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

import com.obysoft.escalafacil.dto.EscalaResponse;
import com.obysoft.escalafacil.dto.GerarEscalaRequest;
import com.obysoft.escalafacil.dto.TrocarBombeiroRequest;
import com.obysoft.escalafacil.service.EscalaService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/schedules")
public class EscalaController {

    private final EscalaService service;

    public EscalaController(EscalaService service) {
        this.service = service;
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
            @Valid
            @RequestBody GerarEscalaRequest request) {

        EscalaResponse response =
                service.gerar(request);

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
            @PathVariable Long escalaId,
            @PathVariable Long itemId,
            @Valid
            @RequestBody
            TrocarBombeiroRequest request) {

        return service.trocarBombeiro(
                escalaId,
                itemId,
                request.bombeiroId()
        );
    }

    @PatchMapping("/{id}/publish")
    public EscalaResponse publicar(
            @PathVariable Long id) {

        return service.publicar(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        service.excluir(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}