package br.fiap.floodshield.web;

import br.fiap.floodshield.domain.Abrigo;
import br.fiap.floodshield.repository.AbrigoRepository;
import br.fiap.floodshield.web.dto.DomainDtos.AbrigoRequest;
import br.fiap.floodshield.web.dto.DomainDtos.AbrigoResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/abrigos")
public class AbrigoController {

    private final AbrigoRepository abrigoRepository;

    public AbrigoController(AbrigoRepository abrigoRepository) {
        this.abrigoRepository = abrigoRepository;
    }

    @GetMapping
    public List<AbrigoResponse> list() {
        return abrigoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public AbrigoResponse get(@PathVariable Long id) {
        return toResponse(abrigoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Abrigo não encontrado")));
    }

    @PostMapping
    @PreAuthorize("hasRole('GESTOR')")
    public AbrigoResponse create(@Valid @RequestBody AbrigoRequest request) {
        Abrigo abrigo = new Abrigo();
        abrigo.setNome(request.name());
        abrigo.setEndereco(request.address());
        abrigo.setVagasDisponiveis(request.availableSlots());
        abrigo.setAberto(request.open() == null || request.open());
        return toResponse(abrigoRepository.save(abrigo));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public AbrigoResponse update(@PathVariable Long id, @Valid @RequestBody AbrigoRequest request) {
        Abrigo abrigo = abrigoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Abrigo não encontrado"));
        abrigo.setNome(request.name());
        abrigo.setEndereco(request.address());
        abrigo.setVagasDisponiveis(request.availableSlots());
        if (request.open() != null) {
            abrigo.setAberto(request.open());
        }
        return toResponse(abrigoRepository.save(abrigo));
    }

    private AbrigoResponse toResponse(Abrigo abrigo) {
        return new AbrigoResponse(
                abrigo.getId(),
                abrigo.getNome(),
                abrigo.getEndereco(),
                abrigo.getVagasDisponiveis(),
                abrigo.isAberto());
    }
}
