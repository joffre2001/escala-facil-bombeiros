package com.obysoft.escalafacil.entity;

import java.time.LocalDateTime;

import com.obysoft.escalafacil.enumeration.PerfilUsuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
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
            ),
            @UniqueConstraint(
                    name = "uk_usuarios_bombeiro",
                    columnNames = "bombeiro_id"
            )
        }
)
public class Usuario {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(
            nullable = false,
            length = 120
    )
    private String nome;

    @Column(
            nullable = false,
            length = 150
    )
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private PerfilUsuario perfil;

    @Column(nullable = false)
    private boolean ativo;

    @Column(
            name = "criado_em",
            nullable = false,
            updatable = false
    )
    private LocalDateTime criadoEm;

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = true
    )
    @JoinColumn(
            name = "bombeiro_id",
            unique = true,
            foreignKey = @ForeignKey(
                    name = "fk_usuarios_bombeiro"
            )
    )
    private Bombeiro bombeiro;

    protected Usuario() {
        // Construtor exigido pelo JPA
    }

    public Usuario(
            String nome,
            String email,
            String senha,
            PerfilUsuario perfil) {

        if (perfil == PerfilUsuario.BOMBEIRO) {
            throw new IllegalArgumentException(
                    "Um usuário BOMBEIRO precisa "
                            + "estar associado a um bombeiro."
            );
        }

        inicializar(
                nome,
                email,
                senha,
                perfil
        );
    }

    public Usuario(
            String nome,
            String email,
            String senha,
            Bombeiro bombeiro) {

        if (bombeiro == null) {
            throw new IllegalArgumentException(
                    "O bombeiro é obrigatório."
            );
        }

        inicializar(
                nome,
                email,
                senha,
                PerfilUsuario.BOMBEIRO
        );

        this.bombeiro = bombeiro;
    }

    private void inicializar(
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

    public void vincularBombeiro(
            Bombeiro bombeiro) {

        if (perfil != PerfilUsuario.BOMBEIRO) {
            throw new IllegalStateException(
                    "Somente usuários com perfil BOMBEIRO "
                            + "podem ser vinculados."
            );
        }

        if (bombeiro == null) {
            throw new IllegalArgumentException(
                    "O bombeiro é obrigatório."
            );
        }

        this.bombeiro = bombeiro;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void alterarStatus(boolean ativo) {
        this.ativo = ativo;
    }

    public void alterarSenha(String senha) {
        this.senha = senha;
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

    public boolean isAtivo() {
        return ativo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public Bombeiro getBombeiro() {
        return bombeiro;
    }
}