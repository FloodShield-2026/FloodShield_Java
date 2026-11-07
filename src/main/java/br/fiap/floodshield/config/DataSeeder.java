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
            a1.setEndereco("Rua Padre Antônio Vieira, 120 — Penha");
            a1.setVagasDisponiveis(84);
            a1.setAberto(true);
            abrigoRepository.save(a1);

            Abrigo a2 = new Abrigo();
            a2.setNome("GINÁSIO Itaquera");
            a2.setEndereco("Av. Itaquera, 5500 — Itaquera");
            a2.setVagasDisponiveis(210);
            a2.setAberto(true);
            abrigoRepository.save(a2);

            Abrigo a3 = new Abrigo();
            a3.setNome("Centro Comunitário Jardim Pantanal");
            a3.setEndereco("Rua das Hortênsias, 45 — Jardim Pantanal");
            a3.setVagasDisponiveis(36);
            a3.setAberto(false);
            abrigoRepository.save(a3);

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
