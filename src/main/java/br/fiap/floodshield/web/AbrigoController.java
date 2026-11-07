package br.fiap.floodshield.web;

import br.fiap.floodshield.domain.Abrigo;
import br.fiap.floodshield.repository.AbrigoRepository;
import br.fiap.floodshield.service.AbrigoService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/abrigos")
public class AbrigoController {

    private final AbrigoService abrigoService;
    private final AbrigoRepository abrigoRepository;

    public AbrigoController(AbrigoService abrigoService, AbrigoRepository abrigoRepository) {
        this.abrigoService = abrigoService;
        this.abrigoRepository = abrigoRepository;
    }

    @GetMapping
    public List<AbrigoResponse> list(
            @RequestParam(required = false) String bairro,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng) {
        return abrigoService.list(bairro, lat, lng);
    }

    @GetMapping("/{id}")
    public AbrigoResponse get(@PathVariable Long id) {
        return abrigoService.list(null, null, null).stream()
                .filter(item -> item.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Abrigo não encontrado"));
    }

    @PostMapping
    @PreAuthorize("hasRole('GESTOR')")
    public AbrigoResponse create(@Valid @RequestBody AbrigoRequest request) {
        Abrigo abrigo = new Abrigo();
        apply(abrigo, request);
        abrigoRepository.save(abrigo);
        return abrigoService.list(abrigo.getBairro(), abrigo.getLatitude(), abrigo.getLongitude()).stream()
                .filter(item -> item.id().equals(abrigo.getId()))
                .findFirst()
                .orElseThrow();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public AbrigoResponse update(@PathVariable Long id, @Valid @RequestBody AbrigoRequest request) {
        Abrigo abrigo = abrigoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Abrigo não encontrado"));
        apply(abrigo, request);
        abrigoRepository.save(abrigo);
        return abrigoService.list(abrigo.getBairro(), abrigo.getLatitude(), abrigo.getLongitude()).stream()
                .filter(item -> item.id().equals(abrigo.getId()))
                .findFirst()
                .orElseThrow();
    }

    private void apply(Abrigo abrigo, AbrigoRequest request) {
        abrigo.setNome(request.name());
        abrigo.setEndereco(request.address());
        abrigo.setBairro(request.neighborhood().trim());
        abrigo.setLatitude(request.latitude());
        abrigo.setLongitude(request.longitude());
        abrigo.setVagasDisponiveis(request.availableSlots());
        if (request.open() != null) {
            abrigo.setAberto(request.open());
        } else if (abrigo.getId() == null) {
            abrigo.setAberto(true);
        }
    }
}
