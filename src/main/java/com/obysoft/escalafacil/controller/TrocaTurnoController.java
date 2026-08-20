package com.obysoft.escalafacil.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.obysoft.escalafacil.dto.*;
import com.obysoft.escalafacil.service.TrocaTurnoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/shift-exchanges")
@PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
public class TrocaTurnoController {
    private final TrocaTurnoService service;
    public TrocaTurnoController(TrocaTurnoService service){this.service=service;}
    @GetMapping public List<SolicitacaoTrocaResponse> listar(){return service.todas();}
    @PatchMapping("/{id}/decision")
    public SolicitacaoTrocaResponse decidir(Principal principal,@PathVariable Long id,
            @Valid @RequestBody DecisaoRequest request){return service.decidir(principal.getName(),id,request);}
}
