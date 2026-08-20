package com.obysoft.escalafacil.entity;

import java.time.LocalDateTime;

import com.obysoft.escalafacil.enumeration.PerfilUsuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "usuarios",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_usuarios_email",
                    columnNames = "email"
            )
        }
)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PerfilUsuario perfil;

    @Column(nullable = false)
    private boolean ativo;

    @Column(
            name = "criado_em",
            nullable = false,
            updatable = false
    )
    private LocalDateTime criadoEm;

    @OneToOne
    @JoinColumn(name = "bombeiro_id", unique = true)
    private Bombeiro bombeiro;

    protected Usuario() {
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Usuario(
            String nome,
            String email,
            String senha,
            PerfilUsuario perfil) {

        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfil = perfil;
        this.ativo = true;
        this.criadoEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public Bombeiro getBombeiro() { return bombeiro; }

    public void vincularBombeiro(Bombeiro bombeiro) { this.bombeiro = bombeiro; }

    public void atualizarIdentificacao(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void alterarStatus(boolean ativo) {
        this.ativo = ativo;
    }

    public void alterarSenha(String senha) {
        this.senha = senha;
    }
}
