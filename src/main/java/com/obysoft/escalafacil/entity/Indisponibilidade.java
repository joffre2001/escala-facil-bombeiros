package com.obysoft.escalafacil.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.obysoft.escalafacil.enumeration.TipoIndisponibilidade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "indisponibilidades")
public class Indisponibilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bombeiro_id", nullable = false)
    private Bombeiro bombeiro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoIndisponibilidade tipo;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(nullable = false)
    private boolean negociavel;

    @Column(length = 255)
    private String motivo;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    protected Indisponibilidade() {
    }

    public Indisponibilidade(
            Bombeiro bombeiro,
            TipoIndisponibilidade tipo,
            LocalDate dataInicio,
            LocalDate dataFim,
            boolean negociavel,
            String motivo) {

        this.bombeiro = bombeiro;
        this.tipo = tipo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.negociavel = negociavel;
        this.motivo = motivo;
        this.criadoEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Bombeiro getBombeiro() {
        return bombeiro;
    }

    public TipoIndisponibilidade getTipo() {
        return tipo;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public boolean isNegociavel() {
        return negociavel;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void atualizar(
            TipoIndisponibilidade tipo,
            LocalDate dataInicio,
            LocalDate dataFim,
            boolean negociavel,
            String motivo) {

        this.tipo = tipo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.negociavel = negociavel;
        this.motivo = motivo;
    }
}