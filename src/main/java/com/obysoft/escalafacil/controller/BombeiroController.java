package com.obysoft.escalafacil.controller;

import com.obysoft.escalafacil.dto.BombeiroRequest;
import com.obysoft.escalafacil.dto.BombeiroResponse;
import com.obysoft.escalafacil.enumeration.StatusBombeiro;
import com.obysoft.escalafacil.service.BombeiroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/firefighters")
public class BombeiroController {
    private final BombeiroService service;
    public BombeiroController(BombeiroService service) { this.service = service; }
    @GetMapping public List<BombeiroResponse> listar() { return service.listar(); }
    @GetMapping("/{id}") public BombeiroResponse buscar(@PathVariable Long id) { return service.buscar(id); }
    @PostMapping public ResponseEntity<BombeiroResponse> criar(@Valid @RequestBody BombeiroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }
    @PatchMapping("/{id}/status") public BombeiroResponse alterarStatus(@PathVariable Long id, @RequestParam StatusBombeiro status) {
        return service.alterarStatus(id, status);
    }
}
