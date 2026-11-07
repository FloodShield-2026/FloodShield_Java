package br.fiap.floodshield.web;

import br.fiap.floodshield.domain.PontoRegua;
import br.fiap.floodshield.repository.PontoReguaRepository;
import br.fiap.floodshield.service.LeituraService;
import br.fiap.floodshield.web.dto.DomainDtos.LeituraRequest;
import br.fiap.floodshield.web.dto.DomainDtos.LeituraResponse;
import br.fiap.floodshield.web.dto.DomainDtos.PontoResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class LeituraController {

    private final LeituraService leituraService;
    private final PontoReguaRepository pontoReguaRepository;

    public LeituraController(LeituraService leituraService, PontoReguaRepository pontoReguaRepository) {
        this.leituraService = leituraService;
        this.pontoReguaRepository = pontoReguaRepository;
    }

    @GetMapping("/pontos")
    public List<PontoResponse> pontos() {
        return pontoReguaRepository.findAll().stream()
                .map(p -> new PontoResponse(
                        p.getId(),
                        p.getNome(),
                        p.getBairro(),
                        p.getLatitude(),
                        p.getLongitude(),
                        p.getLimiteAlertaCm()))
                .toList();
    }

    @GetMapping("/leituras")
    public Page<LeituraResponse> leituras(
            @RequestParam(required = false) Long pontoId,
            @PageableDefault(size = 20) Pageable pageable) {
        return leituraService.list(pontoId, pageable);
    }

    @PostMapping("/leituras")
    public LeituraResponse registrar(@Valid @RequestBody LeituraRequest request) {
        return leituraService.registrar(request);
    }
}
