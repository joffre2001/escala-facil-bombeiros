package com.obysoft.escalafacil.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.obysoft.escalafacil.dto.BombeiroRequest;
import com.obysoft.escalafacil.dto.BombeiroResponse;
import com.obysoft.escalafacil.enumeration.StatusBombeiro;
import com.obysoft.escalafacil.service.BombeiroService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/firefighters")
@PreAuthorize("hasAnyRole('ADMIN', 'GESTOR')")
public class BombeiroController {

    private final BombeiroService service;

    public BombeiroController(BombeiroService service) {
        this.service = service;
    }

    @GetMapping
    public List<BombeiroResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public BombeiroResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<BombeiroResponse> criar(
            @Valid @RequestBody BombeiroRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.criar(request));
    }

    @PutMapping("/{id}")
    public BombeiroResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody BombeiroRequest request) {

        return service.atualizar(id, request);
    }

    @PatchMapping("/{id}/status")
    public BombeiroResponse alterarStatus(
            @PathVariable Long id,
            @RequestParam StatusBombeiro status) {

        return service.alterarStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
    
}
