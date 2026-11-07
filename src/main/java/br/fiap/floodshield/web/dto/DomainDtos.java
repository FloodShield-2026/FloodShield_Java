package br.fiap.floodshield.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public final class DomainDtos {

    private DomainDtos() {
    }

    public record AbrigoResponse(Long id, String name, String address, Integer availableSlots, boolean open) {
    }

    public record AbrigoRequest(
            @NotBlank String name,
            @NotBlank String address,
            @NotNull Integer availableSlots,
            Boolean open) {
    }

    public record PontoResponse(Long id, String name, String neighborhood, Double latitude, Double longitude, Integer alertLimitCm) {
    }

    public record LeituraRequest(
            @NotNull Long pontoId,
            @NotNull Integer nivelCm,
            String origem) {
    }

    public record LeituraResponse(Long id, Long pontoId, Integer nivelCm, String origem, Instant coletadoEm) {
    }

    public record PlanoAcaoRequest(Long occurrenceId, Integer waterLevelCm, String neighborhood) {
    }

    public record PlanoAcaoResponse(
            Long occurrenceId,
            String summary,
            List<String> steps,
            String shelterName,
            Instant generatedAt,
            String source) {
    }

    public record AlertaResponse(Long id, Long pontoId, Integer nivelCm, String mensagem, Instant criadoEm) {
    }
}
