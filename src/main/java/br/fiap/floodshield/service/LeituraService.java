package br.fiap.floodshield.service;

import br.fiap.floodshield.domain.Leitura;
import br.fiap.floodshield.domain.PontoRegua;
import br.fiap.floodshield.messaging.NivelCriticoConsumer;
import br.fiap.floodshield.messaging.NivelCriticoEvent;
import br.fiap.floodshield.messaging.NivelCriticoPublisher;
import br.fiap.floodshield.repository.LeituraRepository;
import br.fiap.floodshield.repository.PontoReguaRepository;
import br.fiap.floodshield.web.dto.DomainDtos.LeituraRequest;
import br.fiap.floodshield.web.dto.DomainDtos.LeituraResponse;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeituraService {

    private final LeituraRepository leituraRepository;
    private final PontoReguaRepository pontoReguaRepository;
    private final NivelCriticoPublisher publisher;
    private final NivelCriticoConsumer consumer;
    private final int criticalDefault;

    public LeituraService(
            LeituraRepository leituraRepository,
            PontoReguaRepository pontoReguaRepository,
            NivelCriticoPublisher publisher,
            NivelCriticoConsumer consumer,
            @Value("${floodshield.water-level.critical-cm}") int criticalDefault) {
        this.leituraRepository = leituraRepository;
        this.pontoReguaRepository = pontoReguaRepository;
        this.publisher = publisher;
        this.consumer = consumer;
        this.criticalDefault = criticalDefault;
    }

    public Page<LeituraResponse> list(Long pontoId, Pageable pageable) {
        Page<Leitura> page = pontoId == null
                ? leituraRepository.findAll(pageable)
                : leituraRepository.findByPontoId(pontoId, pageable);
        return page.map(this::toResponse);
    }

    @Transactional
    public LeituraResponse registrar(LeituraRequest request) {
        PontoRegua ponto = pontoReguaRepository.findById(request.pontoId())
                .orElseThrow(() -> new IllegalArgumentException("Ponto de régua não encontrado"));

        Leitura leitura = new Leitura();
        leitura.setPonto(ponto);
        leitura.setNivelCm(request.nivelCm());
        leitura.setOrigem(request.origem() == null || request.origem().isBlank() ? "SIMULADOR" : request.origem());
        leitura.setColetadoEm(Instant.now());
        Leitura saved = leituraRepository.save(leitura);

        int limite = ponto.getLimiteAlertaCm() != null ? ponto.getLimiteAlertaCm() : criticalDefault;
        if (saved.getNivelCm() >= limite) {
            NivelCriticoEvent event = new NivelCriticoEvent(
                    ponto.getId(),
                    ponto.getNome(),
                    ponto.getBairro(),
                    saved.getNivelCm(),
                    limite,
                    Instant.now());
            publisher.publish(event);
            // Garante alerta mesmo se o broker estiver offline.
            consumer.persist(event);
        }

        return toResponse(saved);
    }

    private LeituraResponse toResponse(Leitura leitura) {
        return new LeituraResponse(
                leitura.getId(),
                leitura.getPonto().getId(),
                leitura.getNivelCm(),
                leitura.getOrigem(),
                leitura.getColetadoEm());
    }
}
