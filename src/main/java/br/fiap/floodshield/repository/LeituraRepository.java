package br.fiap.floodshield.repository;

import br.fiap.floodshield.domain.Leitura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeituraRepository extends JpaRepository<Leitura, Long> {
    Page<Leitura> findByPontoId(Long pontoId, Pageable pageable);
}
