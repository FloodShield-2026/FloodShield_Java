package br.fiap.floodshield.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "alertas")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pontoId;

    @Column(nullable = false)
    private Integer nivelCm;

    @Column(nullable = false)
    private String mensagem;

    @Column(nullable = false)
    private Instant criadoEm = Instant.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPontoId() {
        return pontoId;
    }

    public void setPontoId(Long pontoId) {
        this.pontoId = pontoId;
    }

    public Integer getNivelCm() {
        return nivelCm;
    }

    public void setNivelCm(Integer nivelCm) {
        this.nivelCm = nivelCm;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Instant criadoEm) {
        this.criadoEm = criadoEm;
    }
}
