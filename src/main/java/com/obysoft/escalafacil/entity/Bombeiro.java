package com.obysoft.escalafacil.entity;

import com.obysoft.escalafacil.enumeration.StatusBombeiro;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "bombeiros")
public class Bombeiro {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 150)
    private String nomeCompleto;
    @Column(nullable = false, unique = true, length = 30)
    private String matricula;
    @Column(nullable = false, unique = true, length = 150)
    private String email;
    @Column(length = 30)
    private String telefone;
    @Column(nullable = false, length = 80)
    private String cargo;
    @Column(length = 80)
    private String equipe;
    private LocalDate dataAdmissao;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private StatusBombeiro status = StatusBombeiro.ATIVO;
    @Column(nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @PrePersist void prePersist() { criadoEm = OffsetDateTime.now(); }
    public Long getId() { return id; }
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public String getEquipe() { return equipe; }
    public void setEquipe(String equipe) { this.equipe = equipe; }
    public LocalDate getDataAdmissao() { return dataAdmissao; }
    public void setDataAdmissao(LocalDate dataAdmissao) { this.dataAdmissao = dataAdmissao; }
    public StatusBombeiro getStatus() { return status; }
    public void setStatus(StatusBombeiro status) { this.status = status; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
}
