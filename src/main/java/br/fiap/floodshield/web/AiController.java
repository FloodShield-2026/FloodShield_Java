package br.fiap.floodshield.web;

import br.fiap.floodshield.ai.PlanoAcaoService;
import br.fiap.floodshield.web.dto.DomainDtos.PlanoAcaoRequest;
import br.fiap.floodshield.web.dto.DomainDtos.PlanoAcaoResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final PlanoAcaoService planoAcaoService;

    public AiController(PlanoAcaoService planoAcaoService) {
        this.planoAcaoService = planoAcaoService;
    }

    @PostMapping("/plano-acao")
    public PlanoAcaoResponse plano(@Valid @RequestBody PlanoAcaoRequest request) {
        return planoAcaoService.gerar(request);
    }
}
