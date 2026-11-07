package br.fiap.floodshield.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NivelCriticoPublisher {

    private static final Logger log = LoggerFactory.getLogger(NivelCriticoPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public NivelCriticoPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${floodshield.messaging.exchange}") String exchange,
            @Value("${floodshield.messaging.routing-key}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void publish(NivelCriticoEvent event) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            log.info("Evento NivelCritico publicado ponto={} nivel={}cm", event.pontoId(), event.nivelCm());
        } catch (Exception ex) {
            log.warn("RabbitMQ indisponível — evento local registrado: {}", ex.getMessage());
            // Mantém a API utilizável sem broker; o consumer direto é acionado pelo serviço.
        }
    }
}
