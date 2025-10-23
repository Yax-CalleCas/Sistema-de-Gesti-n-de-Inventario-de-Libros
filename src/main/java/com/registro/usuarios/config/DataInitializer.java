package com.registro.usuarios.config;

import com.registro.usuarios.modelo.Rol;
import com.registro.usuarios.modelo.Usuario;
import com.registro.usuarios.repositorio.RolRepositorio;
import com.registro.usuarios.repositorio.UsuarioRepositorio;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initRoles(RolRepositorio rolRepositorio) {
        return _ -> {
            crearRolSiNoExiste(rolRepositorio, "ROLE_ADMIN");
            crearRolSiNoExiste(rolRepositorio, "ROLE_CLIENTE");
            crearRolSiNoExiste(rolRepositorio, "ROLE_TRABAJADOR");
            System.out.println("Roles iniciales creados o ya existentes.");
        };
    }

    private void crearRolSiNoExiste(RolRepositorio rolRepositorio, String nombreRol) {
        if (rolRepositorio.findByNombre(nombreRol).isEmpty()) {
            rolRepositorio.save(new Rol(nombreRol));
            System.out.println("Rol creado: " + nombreRol);
        } else {
            System.out.println("Rol ya existente: " + nombreRol);
        }
    }

    @Bean
    CommandLineRunner initAdminUser(UsuarioRepositorio usuarioRepositorio, RolRepositorio rolRepositorio, PasswordEncoder passwordEncoder) {
        return _ -> {
            if (usuarioRepositorio.findByEmail("yaxcalles@gmail.com").isEmpty()) {
                Rol rolAdmin = rolRepositorio.findByNombre("ROLE_ADMIN")
                        .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN debe existir antes de crear el usuario administrador"));

                Usuario admin = new Usuario();
                admin.setNombre("Yaxon");
                admin.setApellido("Castillo");
                admin.setEmail("yaxcalles@gmail.com");
                admin.setPassword(passwordEncoder.encode("123123"));
                admin.setEnabled(true);
                admin.setImagenUrl("https://cdn-icons-png.flaticon.com/512/149/149071.png");
                admin.setRol(rolAdmin);

                usuarioRepositorio.save(admin);
                System.out.println("Usuario dueño creado: yaxcalles@gmail.com (123123)");
            } else {
                System.out.println("El usuario dueño ya existe, no se creó nuevamente.");
            }
        };
    }
}