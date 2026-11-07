package br.fiap.floodshield.ai;

import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class AiConfig {

    @Bean
    @ConditionalOnMissingBean(ChatModel.class)
    ChatModel localRagChatModel() {
        return new ChatModel() {
            @Override
            public ChatResponse call(Prompt prompt) {
                String cartilha;
                try {
                    cartilha = new ClassPathResource("cartilha/protocolo-enchente.txt")
                            .getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
                } catch (Exception e) {
                    cartilha = "Evite correnteza, monte o kit e vá ao abrigo.";
                }
                String answer = """
                        Plano de ação FloodShield (RAG local com Spring AI ChatModel):
                        Use o protocolo oficial. Trecho recuperado:
                        %s
                        Passos: 1) segurança imediata 2) kit essencial 3) abrigo 4) check-in no app.
                        """.formatted(cartilha.lines().limit(8).reduce((a, b) -> a + " | " + b).orElse(cartilha));
                return new ChatResponse(List.of(new Generation(new AssistantMessage(answer))));
            }
        };
    }

    @Bean
    ChatClient.Builder chatClientBuilder(ChatModel chatModel) {
        return ChatClient.builder(chatModel);
    }
}
