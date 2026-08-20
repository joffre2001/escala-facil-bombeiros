package com.obysoft.escalafacil.entity;

import java.time.LocalDateTime;
import com.obysoft.escalafacil.enumeration.StatusTroca;
import jakarta.persistence.*;

@Entity
@Table(name = "solicitacoes_troca")
public class SolicitacaoTroca {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "item_escala_id")
    private ItemEscala itemEscala;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "solicitante_id")
    private Bombeiro solicitante;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "substituto_id")
    private Bombeiro substituto;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30)
    private StatusTroca status;
    @Column(length = 255) private String motivo;
    @Column(name = "criado_em", nullable = false) private LocalDateTime criadoEm;
    @Column(name = "atualizado_em", nullable = false) private LocalDateTime atualizadoEm;

    protected SolicitacaoTroca() {}
    public SolicitacaoTroca(ItemEscala item, Bombeiro solicitante, Bombeiro substituto, String motivo) {
        this.itemEscala = item; this.solicitante = solicitante; this.substituto = substituto;
        this.motivo = motivo; this.status = StatusTroca.AGUARDANDO_ACEITE;
        this.criadoEm = LocalDateTime.now(); this.atualizadoEm = this.criadoEm;
    }
    public Long getId() { return id; }
    public ItemEscala getItemEscala() { return itemEscala; }
    public Bombeiro getSolicitante() { return solicitante; }
    public Bombeiro getSubstituto() { return substituto; }
    public StatusTroca getStatus() { return status; }
    public String getMotivo() { return motivo; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void alterarStatus(StatusTroca status) { this.status = status; this.atualizadoEm = LocalDateTime.now(); }
}
