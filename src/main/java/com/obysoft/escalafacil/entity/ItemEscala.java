package com.obysoft.escalafacil.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "itens_escala")
public class ItemEscala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "escala_id",
            nullable = false
    )
    private Escala escala;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "bombeiro_id",
            nullable = false
    )
    private Bombeiro bombeiro;

    @Column(
            name = "inicio_plantao",
            nullable = false
    )
    private LocalDateTime inicioPlantao;

    @Column(
            name = "fim_plantao",
            nullable = false
    )
    private LocalDateTime fimPlantao;

    @Column(nullable = false)
    private boolean conflito;

    @Column(length = 255)
    private String observacao;

    @Column(nullable = false)
    private boolean cancelado;

    @Column(name = "motivo_cancelamento", length = 255)
    private String motivoCancelamento;

    protected ItemEscala() {
        // Construtor exigido pelo JPA
    }

    public ItemEscala(
            Escala escala,
            Bombeiro bombeiro,
            LocalDateTime inicioPlantao,
            LocalDateTime fimPlantao,
            boolean conflito,
            String observacao) {

        this.escala = escala;
        this.bombeiro = bombeiro;
        this.inicioPlantao = inicioPlantao;
        this.fimPlantao = fimPlantao;
        this.conflito = conflito;
        this.observacao = observacao;
        this.cancelado = false;
    }

    public void trocarBombeiro(
            Bombeiro novoBombeiro,
            boolean conflito,
            String observacao) {

        this.bombeiro = novoBombeiro;
        this.conflito = conflito;
        this.observacao = observacao;
    }

    public Long getId() {
        return id;
    }

    public Escala getEscala() {
        return escala;
    }

    public Bombeiro getBombeiro() {
        return bombeiro;
    }

    public LocalDateTime getInicioPlantao() {
        return inicioPlantao;
    }

    public LocalDateTime getFimPlantao() {
        return fimPlantao;
    }

    public boolean isConflito() {
        return conflito;
    }

    public String getObservacao() {
        return observacao;
    }

    public boolean isCancelado() { return cancelado; }
    public String getMotivoCancelamento() { return motivoCancelamento; }
    public void cancelar(String motivo) { this.cancelado = true; this.motivoCancelamento = motivo; }
}
