package br.fiap.floodshield.repository;

import br.fiap.floodshield.domain.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {
}
