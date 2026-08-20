package com.obysoft.escalafacil.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.obysoft.escalafacil.entity.Usuario;
import com.obysoft.escalafacil.enumeration.PerfilUsuario;
import com.obysoft.escalafacil.repository.BombeiroRepository;
import com.obysoft.escalafacil.repository.UsuarioRepository;

@Configuration
public class BombeiroUsuarioInitializer {
    @Bean
    CommandLineRunner criarUsuariosDosBombeiros(BombeiroRepository bombeiros,
            UsuarioRepository usuarios, PasswordEncoder encoder) {
        return args -> bombeiros.findAll().forEach(bombeiro -> {
            if (usuarios.findByBombeiroId(bombeiro.getId()).isPresent()
                    || usuarios.existsByEmailIgnoreCase(bombeiro.getEmail())) return;
            Usuario usuario = new Usuario(bombeiro.getNomeCompleto(), bombeiro.getEmail(),
                    encoder.encode(bombeiro.getMatricula()), PerfilUsuario.BOMBEIRO);
            usuario.vincularBombeiro(bombeiro);
            usuarios.save(usuario);
        });
    }
}
