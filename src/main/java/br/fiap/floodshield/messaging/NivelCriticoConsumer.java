package br.fiap.floodshield.messaging;

import br.fiap.floodshield.domain.Alerta;
import br.fiap.floodshield.repository.AlertaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NivelCriticoConsumer {

    private static final Logger log = LoggerFactory.getLogger(NivelCriticoConsumer.class);

    private final AlertaRepository alertaRepository;

    public NivelCriticoConsumer(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    @RabbitListener(queues = "${floodshield.messaging.queue}")
    public void onMessage(NivelCriticoEvent event) {
        persist(event);
    }

    public Alerta persist(NivelCriticoEvent event) {
        Alerta alerta = new Alerta();
        alerta.setPontoId(event.pontoId());
        alerta.setNivelCm(event.nivelCm());
        alerta.setMensagem("Nível crítico em %s (%s): %d cm (limite %d cm)"
                .formatted(event.pontoNome(), event.bairro(), event.nivelCm(), event.limiteCm()));
        Alerta saved = alertaRepository.save(alerta);
        log.info("Alerta persistido id={} ponto={}", saved.getId(), saved.getPontoId());
        return saved;
    }
}
