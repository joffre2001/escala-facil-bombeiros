package com.obysoft.escalafacil.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.obysoft.escalafacil.entity.Usuario;
import com.obysoft.escalafacil.enumeration.PerfilUsuario;
import com.obysoft.escalafacil.repository.UsuarioRepository;


@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner criarOuAtualizarAdministradorInicial(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.nome:Administrador}") String nome,
            @Value("${app.admin.email:}") String email,
            @Value("${app.admin.senha:}") String senha) {

        return args -> {
            String emailNormalizado = email.trim().toLowerCase();

            if (emailNormalizado.isBlank() || senha.isBlank()) {
                System.out.println(
                        "Administrador não configurado: verifique o arquivo .env."
                );
                return;
            }

            Usuario admin = usuarioRepository
                    .findByEmailIgnoreCase(emailNormalizado)
                    .orElseGet(() -> new Usuario(
                            nome.isBlank()
                                    ? "Administrador"
                                    : nome.trim(),
                            emailNormalizado,
                            passwordEncoder.encode(senha),
                            PerfilUsuario.ADMIN
                    ));

            if (admin.getId() != null
                    && !passwordEncoder.matches(
                            senha,
                            admin.getSenha()
                    )) {

                admin.setSenha(passwordEncoder.encode(senha));

                System.out.println(
                        "Senha do administrador atualizada a partir do .env."
                );
            }

            usuarioRepository.save(admin);
        };
    }
}