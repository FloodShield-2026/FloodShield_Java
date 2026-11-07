package br.fiap.floodshield.web.dto;

import br.fiap.floodshield.domain.OcorrenciaStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public final class OcorrenciaDtos {

    private OcorrenciaDtos() {
    }

    public record CreateRequest(
            @NotBlank String title,
            @NotBlank String description,
            @NotBlank String neighborhood,
            @NotNull Integer waterLevelCm,
            Double latitude,
            Double longitude) {
    }

    public record UpdateRequest(
            String title,
            String description,
            String neighborhood,
            Integer waterLevelCm,
            Double latitude,
            Double longitude,
            OcorrenciaStatus status) {
    }

    public record Response(
            Long id,
            String title,
            String description,
            String neighborhood,
            Integer waterLevelCm,
            Double latitude,
            Double longitude,
            OcorrenciaStatus status,
            Long userId,
            Instant createdAt) {
    }
}
