package com.obysoft.escalafacil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.obysoft.escalafacil.entity.Usuario;


public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    Optional<Usuario> findByBombeiroId(Long bombeiroId);
}
