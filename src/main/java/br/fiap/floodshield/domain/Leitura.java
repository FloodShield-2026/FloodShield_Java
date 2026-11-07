package br.fiap.floodshield.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "leituras")
public class Leitura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ponto_id")
    private PontoRegua ponto;

    @Column(nullable = false)
    private Integer nivelCm;

    @Column(nullable = false)
    private String origem = "SIMULADOR";

    @Column(nullable = false)
    private Instant coletadoEm = Instant.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PontoRegua getPonto() {
        return ponto;
    }

    public void setPonto(PontoRegua ponto) {
        this.ponto = ponto;
    }

    public Integer getNivelCm() {
        return nivelCm;
    }

    public void setNivelCm(Integer nivelCm) {
        this.nivelCm = nivelCm;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public Instant getColetadoEm() {
        return coletadoEm;
    }

    public void setColetadoEm(Instant coletadoEm) {
        this.coletadoEm = coletadoEm;
    }
}
