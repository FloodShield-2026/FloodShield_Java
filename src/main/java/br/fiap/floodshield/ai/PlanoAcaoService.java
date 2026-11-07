package br.fiap.floodshield.ai;

import br.fiap.floodshield.domain.Abrigo;
import br.fiap.floodshield.domain.Ocorrencia;
import br.fiap.floodshield.repository.AbrigoRepository;
import br.fiap.floodshield.repository.OcorrenciaRepository;
import br.fiap.floodshield.web.dto.DomainDtos.PlanoAcaoRequest;
import br.fiap.floodshield.web.dto.DomainDtos.PlanoAcaoResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class PlanoAcaoService {

    private static final Logger log = LoggerFactory.getLogger(PlanoAcaoService.class);

    private final OcorrenciaRepository ocorrenciaRepository;
    private final AbrigoRepository abrigoRepository;
    private final ObjectProvider<ChatClient.Builder> chatClientBuilderProvider;
    private final String cartilha;

    public PlanoAcaoService(
            OcorrenciaRepository ocorrenciaRepository,
            AbrigoRepository abrigoRepository,
            ObjectProvider<ChatClient.Builder> chatClientBuilderProvider) {
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.abrigoRepository = abrigoRepository;
        this.chatClientBuilderProvider = chatClientBuilderProvider;
        this.cartilha = loadCartilha();
    }

    public PlanoAcaoResponse gerar(PlanoAcaoRequest request) {
        Ocorrencia ocorrencia = null;
        if (request.occurrenceId() != null) {
            ocorrencia = ocorrenciaRepository.findById(request.occurrenceId())
                    .orElseThrow(() -> new IllegalArgumentException("Ocorrência não encontrada"));
        }

        int nivel = request.waterLevelCm() != null
                ? request.waterLevelCm()
                : ocorrencia != null ? ocorrencia.getNivelCm() : 25;
        String bairro = request.neighborhood() != null
                ? request.neighborhood()
                : ocorrencia != null ? ocorrencia.getBairro() : "sua região";

        Abrigo abrigo = abrigoRepository.findByAbertoTrueOrderByNomeAsc().stream().findFirst().orElse(null);
        String abrigoNome = abrigo != null ? abrigo.getNome() + " — " + abrigo.getEndereco() : "ponto de apoio da Defesa Civil";

        ChatClient.Builder builder = chatClientBuilderProvider.getIfAvailable();
        if (builder != null && isOpenAiConfigured()) {
            try {
                String prompt = """
                        Você é o assistente do FloodShield. Use APENAS o protocolo abaixo (RAG básico) para montar
                        um plano de ação objetivo em português para o morador.

                        PROTOCOLO:
                        %s

                        Contexto:
                        - Bairro: %s
                        - Nível da água: %d cm
                        - Abrigo sugerido: %s

                        Responda em JSON com campos summary (string) e steps (array de 3 a 5 strings curtas).
                        """.formatted(cartilha, bairro, nivel, abrigoNome);

                String content = builder.build()
                        .prompt()
                        .user(prompt)
                        .call()
                        .content();

                return parseOrFallback(content, ocorrencia, nivel, bairro, abrigoNome, "spring-ai-openai");
            } catch (Exception ex) {
                log.warn("Spring AI indisponível, usando RAG local: {}", ex.getMessage());
            }
        }

        return localRag(ocorrencia, nivel, bairro, abrigoNome);
    }

    private PlanoAcaoResponse localRag(
            Ocorrencia ocorrencia, int nivel, String bairro, String abrigoNome) {
        boolean critico = nivel >= 40;
        String summary = critico
                ? "Nível crítico em %s (%d cm). Evacue com segurança e vá ao abrigo indicado."
                        .formatted(bairro, nivel)
                : "Atenção em %s (%d cm). Prepare o kit e evite vias alagadas."
                        .formatted(bairro, nivel);

        List<String> steps = new ArrayList<>();
        if (critico) {
            steps.add("Desligue a energia se a água estiver entrando em casa.");
            steps.add("Leve documentos, remédios e um carregador.");
            steps.add("Dirija-se ao abrigo: " + abrigoNome + ".");
            steps.add("Registre o check-in no app e avise familiares.");
        } else {
            steps.add("Suba itens essenciais para lugares altos.");
            steps.add("Evite atravessar a rua com correnteza.");
            steps.add("Monte o kit com documentos e remédios.");
            steps.add("Acompanhe novos alertas no FloodShield.");
        }

        // Trecho recuperado da cartilha (RAG básico local).
        String recovered = cartilha.lines()
                .filter(line -> line.toLowerCase().contains(critico ? "40 cm" : "kit"))
                .findFirst()
                .orElse("Siga o protocolo oficial FloodShield.");
        steps.add("Referência da cartilha: " + recovered.replace("- ", ""));

        return new PlanoAcaoResponse(
                ocorrencia != null ? ocorrencia.getId() : null,
                summary,
                steps,
                abrigoNome,
                Instant.now(),
                "rag-local-cartilha");
    }

    private PlanoAcaoResponse parseOrFallback(
            String content,
            Ocorrencia ocorrencia,
            int nivel,
            String bairro,
            String abrigoNome,
            String source) {
        try {
            String summary = content;
            List<String> steps = List.of(
                    "Siga o plano gerado pela IA com base na cartilha oficial.",
                    content.length() > 280 ? content.substring(0, 280) + "..." : content,
                    "Abrigo sugerido: " + abrigoNome);
            return new PlanoAcaoResponse(
                    ocorrencia != null ? ocorrencia.getId() : null,
                    "Plano gerado para %s (%d cm)".formatted(bairro, nivel),
                    steps,
                    abrigoNome,
                    Instant.now(),
                    source);
        } catch (Exception ex) {
            return localRag(ocorrencia, nivel, bairro, abrigoNome);
        }
    }

    private boolean isOpenAiConfigured() {
        String key = System.getenv("OPENAI_API_KEY");
        return key != null && !key.isBlank() && !key.startsWith("sk-local");
    }

    private String loadCartilha() {
        try {
            return new ClassPathResource("cartilha/protocolo-enchente.txt")
                    .getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "Protocolo padrão: buscar abrigo, evitar correnteza e registrar check-in.";
        }
    }
}
