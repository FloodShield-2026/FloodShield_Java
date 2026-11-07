package br.fiap.floodshield.repository;

import br.fiap.floodshield.domain.Ocorrencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {
    Page<Ocorrencia> findByUsuarioId(Long usuarioId, Pageable pageable);
}
