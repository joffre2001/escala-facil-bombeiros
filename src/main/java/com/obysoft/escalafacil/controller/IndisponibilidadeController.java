package com.obysoft.escalafacil.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.obysoft.escalafacil.dto.IndisponibilidadeRequest;
import com.obysoft.escalafacil.dto.IndisponibilidadeResponse;
import com.obysoft.escalafacil.service.IndisponibilidadeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/unavailabilities")
public class IndisponibilidadeController {

    private final IndisponibilidadeService service;

    public IndisponibilidadeController(
            IndisponibilidadeService service) {
        this.service = service;
    }

    @GetMapping
    public List<IndisponibilidadeResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public IndisponibilidadeResponse buscar(
            @PathVariable Long id) {
        return service.buscar(id);
    }

    @GetMapping("/firefighter/{bombeiroId}")
    public List<IndisponibilidadeResponse> listarPorBombeiro(
            @PathVariable Long bombeiroId) {
        return service.listarPorBombeiro(bombeiroId);
    }

    @PostMapping
    public ResponseEntity<IndisponibilidadeResponse> criar(
            @Valid @RequestBody IndisponibilidadeRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.criar(request));
    }

    @PutMapping("/{id}")
    public IndisponibilidadeResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody IndisponibilidadeRequest request) {

        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}