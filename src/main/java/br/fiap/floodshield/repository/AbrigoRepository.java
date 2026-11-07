package br.fiap.floodshield.repository;

import br.fiap.floodshield.domain.Abrigo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbrigoRepository extends JpaRepository<Abrigo, Long> {
    List<Abrigo> findByAbertoTrueOrderByNomeAsc();
}
