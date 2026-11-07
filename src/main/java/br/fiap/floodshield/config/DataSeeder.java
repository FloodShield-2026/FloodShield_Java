package br.fiap.floodshield.config;

import br.fiap.floodshield.domain.Abrigo;
import br.fiap.floodshield.domain.Ocorrencia;
import br.fiap.floodshield.domain.OcorrenciaStatus;
import br.fiap.floodshield.domain.PontoRegua;
import br.fiap.floodshield.domain.Role;
import br.fiap.floodshield.domain.Usuario;
import br.fiap.floodshield.repository.AbrigoRepository;
import br.fiap.floodshield.repository.OcorrenciaRepository;
import br.fiap.floodshield.repository.PontoReguaRepository;
import br.fiap.floodshield.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(
            UsuarioRepository usuarioRepository,
            AbrigoRepository abrigoRepository,
            PontoReguaRepository pontoReguaRepository,
            OcorrenciaRepository ocorrenciaRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.count() > 0) {
                return;
            }

            Usuario cidadao = new Usuario();
            cidadao.setNome("Cidadão Demo");
            cidadao.setEmail("cidadao@floodshield.app");
            cidadao.setSenha(passwordEncoder.encode("123456"));
            cidadao.setRole(Role.CIDADAO);
            cidadao = usuarioRepository.save(cidadao);

            Usuario gestor = new Usuario();
            gestor.setNome("Gestor Demo");
            gestor.setEmail("gestor@floodshield.app");
            gestor.setSenha(passwordEncoder.encode("123456"));
            gestor.setRole(Role.GESTOR);
            usuarioRepository.save(gestor);

            Abrigo a1 = new Abrigo();
            a1.setNome("Escola Municipal Penha");
            a1.setEndereco("Rua Padre Antônio Vieira, 120");
            a1.setBairro("Penha");
            a1.setLatitude(-23.5225);
            a1.setLongitude(-46.5421);
            a1.setVagasDisponiveis(84);
            a1.setAberto(true);
            abrigoRepository.save(a1);

            Abrigo a2 = new Abrigo();
            a2.setNome("GINÁSIO Itaquera");
            a2.setEndereco("Av. Itaquera, 5500");
            a2.setBairro("Itaquera");
            a2.setLatitude(-23.5401);
            a2.setLongitude(-46.4712);
            a2.setVagasDisponiveis(210);
            a2.setAberto(true);
            abrigoRepository.save(a2);

            Abrigo a3 = new Abrigo();
            a3.setNome("Centro Comunitário Jardim Pantanal");
            a3.setEndereco("Rua das Hortênsias, 45");
            a3.setBairro("Jardim Pantanal");
            a3.setLatitude(-23.4978);
            a3.setLongitude(-46.4015);
            a3.setVagasDisponiveis(36);
            a3.setAberto(false);
            abrigoRepository.save(a3);

            Abrigo a4 = new Abrigo();
            a4.setNome("CEEJA São Mateus");
            a4.setEndereco("Av. Mateo Bei, 2888");
            a4.setBairro("São Mateus");
            a4.setLatitude(-23.5984);
            a4.setLongitude(-46.4819);
            a4.setVagasDisponiveis(120);
            a4.setAberto(true);
            abrigoRepository.save(a4);

            Abrigo a5 = new Abrigo();
            a5.setNome("Centro Esportivo Tatuapé");
            a5.setEndereco("Rua Serra de Botucatu, 885");
            a5.setBairro("Tatuapé");
            a5.setLatitude(-23.5408);
            a5.setLongitude(-46.5765);
            a5.setVagasDisponiveis(95);
            a5.setAberto(true);
            abrigoRepository.save(a5);

            PontoRegua ponto = new PontoRegua();
            ponto.setNome("Régua Carvalho Pinto");
            ponto.setBairro("Penha");
            ponto.setLatitude(-23.5225);
            ponto.setLongitude(-46.5421);
            ponto.setLimiteAlertaCm(40);
            pontoReguaRepository.save(ponto);

            Ocorrencia ocorrencia = new Ocorrencia();
            ocorrencia.setTitulo("Água na calçada da Carvalho Pinto");
            ocorrencia.setDescricao("Nível subindo rápido após 40 minutos de chuva forte.");
            ocorrencia.setBairro("Penha");
            ocorrencia.setNivelCm(42);
            ocorrencia.setStatus(OcorrenciaStatus.ABERTA);
            ocorrencia.setUsuario(cidadao);
            ocorrenciaRepository.save(ocorrencia);
        };
    }
}
