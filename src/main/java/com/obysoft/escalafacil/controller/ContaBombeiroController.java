package com.obysoft.escalafacil.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.obysoft.escalafacil.dto.CriarContaBombeiroRequest;
import com.obysoft.escalafacil.dto.UsuarioBombeiroResponse;
import com.obysoft.escalafacil.service.ContaBombeiroService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users/firefighters")
@PreAuthorize("hasRole('ADMIN')")
public class ContaBombeiroController {

    private final ContaBombeiroService service;

    public ContaBombeiroController(
            ContaBombeiroService service) {

        this.service = service;
    }

    @PostMapping("/{bombeiroId}")
    public ResponseEntity<UsuarioBombeiroResponse>
            criarConta(
                    @PathVariable Long bombeiroId,
                    @Valid
                    @RequestBody
                    CriarContaBombeiroRequest request) {

        UsuarioBombeiroResponse response =
                service.criarConta(
                        bombeiroId,
                        request.senhaTemporaria()
                );

        URI localizacao = URI.create(
                "/users/firefighters/"
                        + bombeiroId
        );

        return ResponseEntity
                .created(localizacao)
                .body(response);
    }

    @GetMapping("/{bombeiroId}")
    public UsuarioBombeiroResponse buscarConta(
            @PathVariable Long bombeiroId) {

        return service.buscarPorBombeiro(
                bombeiroId
        );
    }
}