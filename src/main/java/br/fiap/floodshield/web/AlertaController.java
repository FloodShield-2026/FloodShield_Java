package br.fiap.floodshield.web;

import br.fiap.floodshield.repository.AlertaRepository;
import br.fiap.floodshield.web.dto.DomainDtos.AlertaResponse;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alertas")
public class AlertaController {

    private final AlertaRepository alertaRepository;

    public AlertaController(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CIDADAO','GESTOR')")
    public List<AlertaResponse> list() {
        return alertaRepository.findAll().stream()
                .sorted((a, b) -> b.getCriadoEm().compareTo(a.getCriadoEm()))
                .map(a -> new AlertaResponse(
                        a.getId(), a.getPontoId(), a.getNivelCm(), a.getMensagem(), a.getCriadoEm()))
                .toList();
    }
}
