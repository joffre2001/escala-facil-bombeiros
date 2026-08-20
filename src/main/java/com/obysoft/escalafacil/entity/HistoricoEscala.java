package com.obysoft.escalafacil.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "historico_escalas")
public class HistoricoEscala {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "escala_id", nullable = false) private Long escalaId;
    @Column(name = "item_escala_id") private Long itemEscalaId;
    @Column(nullable = false, length = 40) private String acao;
    @Column(nullable = false, length = 500) private String descricao;
    @Column(name = "ator_email", nullable = false, length = 150) private String atorEmail;
    @Column(name = "ator_perfil", nullable = false, length = 20) private String atorPerfil;
    @Column(name = "criado_em", nullable = false) private LocalDateTime criadoEm;
    protected HistoricoEscala() {}
    public HistoricoEscala(Escala escala, ItemEscala item, String acao, String descricao, String email, String perfil) {
        this.escalaId=escala.getId(); this.itemEscalaId=item==null?null:item.getId(); this.acao=acao; this.descricao=descricao;
        this.atorEmail=email; this.atorPerfil=perfil; this.criadoEm=LocalDateTime.now();
    }
    public Long getId(){return id;} public Long getEscalaId(){return escalaId;} public Long getItemEscalaId(){return itemEscalaId;}
    public String getAcao(){return acao;} public String getDescricao(){return descricao;} public String getAtorEmail(){return atorEmail;}
    public String getAtorPerfil(){return atorPerfil;} public LocalDateTime getCriadoEm(){return criadoEm;}
}
