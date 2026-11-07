package br.fiap.floodshield.web;

import br.fiap.floodshield.service.OcorrenciaService;
import br.fiap.floodshield.web.dto.OcorrenciaDtos.CreateRequest;
import br.fiap.floodshield.web.dto.OcorrenciaDtos.Response;
import br.fiap.floodshield.web.dto.OcorrenciaDtos.UpdateRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ocorrencias")
public class OcorrenciaController {

    private final OcorrenciaService ocorrenciaService;

    public OcorrenciaController(OcorrenciaService ocorrenciaService) {
        this.ocorrenciaService = ocorrenciaService;
    }

    @GetMapping
    public Page<Response> list(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable) {
        return ocorrenciaService.list(authentication, pageable);
    }

    @GetMapping("/{id}")
    public Response get(@PathVariable Long id, Authentication authentication) {
        return ocorrenciaService.get(id, authentication);
    }

    @PostMapping
    public Response create(@Valid @RequestBody CreateRequest request, Authentication authentication) {
        return ocorrenciaService.create(request, authentication);
    }

    @PutMapping("/{id}")
    public Response update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRequest request,
            Authentication authentication) {
        return ocorrenciaService.update(id, request, authentication);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication authentication) {
        ocorrenciaService.delete(id, authentication);
    }
}
