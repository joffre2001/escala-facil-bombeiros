package com.obysoft.escalafacil.controller;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.obysoft.escalafacil.dto.HistoricoEscalaResponse;
import com.obysoft.escalafacil.service.AuditoriaEscalaService;
@RestController @RequestMapping("/audit") @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
public class AuditoriaController {
 private final AuditoriaEscalaService service; public AuditoriaController(AuditoriaEscalaService service){this.service=service;}
 @GetMapping public List<HistoricoEscalaResponse> listar(){return service.listarTudo();}
}
