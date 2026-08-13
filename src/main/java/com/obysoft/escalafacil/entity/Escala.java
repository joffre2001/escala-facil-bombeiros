package com.obysoft.escalafacil.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.obysoft.escalafacil.enumeration.StatusEscala;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "escalas")
public class Escala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusEscala status;

    @Column(name = "criada_em", nullable = false, updatable = false)
    private LocalDateTime criadaEm;

    @Column(name = "vagas_nao_preenchidas", nullable = false)
    private int vagasNaoPreenchidas;

    @OneToMany(mappedBy = "escala", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemEscala> itens = new ArrayList<>();

    protected Escala() {}

    public Escala(String nome, LocalDate dataInicio, LocalDate dataFim) {
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = StatusEscala.EM_REVISAO;
        this.criadaEm = LocalDateTime.now();
    }

    public void adicionarItem(ItemEscala item) { itens.add(item); }
    public void registrarVagaNaoPreenchida() { vagasNaoPreenchidas++; }
    public void publicar() { status = StatusEscala.PUBLICADA; }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public StatusEscala getStatus() { return status; }
    public LocalDateTime getCriadaEm() { return criadaEm; }
    public int getVagasNaoPreenchidas() { return vagasNaoPreenchidas; }
    public List<ItemEscala> getItens() { return itens; }
}