package br.fiap.floodshield.messaging;

import java.time.Instant;

public record NivelCriticoEvent(
        Long pontoId,
        String pontoNome,
        String bairro,
        Integer nivelCm,
        Integer limiteCm,
        Instant ocorridoEm) {
}
