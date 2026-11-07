package br.fiap.floodshield.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    TopicExchange floodshieldExchange(@Value("${floodshield.messaging.exchange}") String exchange) {
        return new TopicExchange(exchange, true, false);
    }

    @Bean
    Queue nivelCriticoQueue(@Value("${floodshield.messaging.queue}") String queue) {
        return new Queue(queue, true);
    }

    @Bean
    Binding nivelCriticoBinding(
            Queue nivelCriticoQueue,
            TopicExchange floodshieldExchange,
            @Value("${floodshield.messaging.routing-key}") String routingKey) {
        return BindingBuilder.bind(nivelCriticoQueue).to(floodshieldExchange).with(routingKey);
    }

    @Bean
    MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
