package com.registro.usuarios.servicio;

import com.registro.usuarios.controlador.dto.UsuarioRegistroDTO;
import com.registro.usuarios.modelo.Rol;
import com.registro.usuarios.modelo.Usuario;
import com.registro.usuarios.repositorio.RolRepositorio;
import com.registro.usuarios.repositorio.UsuarioRepositorio;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServicioImpl implements UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final RolRepositorio rolRepositorio;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioServicioImpl(UsuarioRepositorio usuarioRepositorio,
                               RolRepositorio rolRepositorio,
                               BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.rolRepositorio = rolRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
        String roleName = usuario.getRol() != null ? usuario.getRol().getNombre() : "ROLE_DEFAULT";
        GrantedAuthority authority = new SimpleGrantedAuthority(roleName);
        return new User(usuario.getEmail(), usuario.getPassword(), usuario.isEnabled(), true, true, true, Collections.singletonList(authority));
    }

    @Override
    public Usuario guardar(UsuarioRegistroDTO registroDTO) {
        Rol rol = rolRepositorio.findByNombre(registroDTO.getRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + registroDTO.getRol()));
        Usuario usuario = new Usuario(
                registroDTO.getNombre(),
                registroDTO.getApellido(),
                registroDTO.getEmail(),
                passwordEncoder.encode(registroDTO.getPassword()),
                rol,
                registroDTO.getImagenUrl()
        );
        return usuarioRepositorio.save(usuario);
    }

    @Override
    public Usuario guardarConRol(UsuarioRegistroDTO registroDTO, String rolNombre) {
        Rol rol = rolRepositorio.findByNombre(rolNombre)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + rolNombre));
        Usuario usuario = new Usuario(
                registroDTO.getNombre(),
                registroDTO.getApellido(),
                registroDTO.getEmail(),
                passwordEncoder.encode(registroDTO.getPassword()),
                rol,
                registroDTO.getImagenUrl()
        );
        return usuarioRepositorio.save(usuario);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        if (usuario.getPassword() != null && !usuario.getPassword().startsWith("$2a$")) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        if (usuario.getRol() == null) {
            throw new RuntimeException("El rol del usuario no puede ser nulo");
        }
        return usuarioRepositorio.save(usuario);
    }

    @Override
    public Usuario actualizar(Usuario usuario) {
        Optional<Usuario> existente = usuarioRepositorio.findById(usuario.getId());
        if (existente.isPresent()) {
            Usuario u = existente.get();
            u.setNombre(usuario.getNombre());
            u.setApellido(usuario.getApellido());
            u.setEmail(usuario.getEmail());

            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty() && !usuario.getPassword().startsWith("$2a$")) {
                u.setPassword(passwordEncoder.encode(usuario.getPassword()));
            }

            if (usuario.getImagenUrl() != null && !usuario.getImagenUrl().isEmpty()) {
                u.setImagenUrl(usuario.getImagenUrl());
            }

            if (usuario.getRol() != null && usuario.getRol().getId() != null) {
                u.setRol(usuario.getRol());
            }

            return usuarioRepositorio.save(u);
        } else {
            throw new RuntimeException("Usuario no encontrado");
        }
    }

    @Override
    public void eliminar(Long usuarioId) {
        usuarioRepositorio.deleteById(usuarioId);
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepositorio.findAll();
    }

    @Override
    public Usuario resetearPassword(Long usuarioId, String nuevaPassword) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        return usuarioRepositorio.save(usuario);
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        return usuarioRepositorio.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return usuarioRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    public void asignarRol(Long usuarioId, String rolNombre) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Rol rol = rolRepositorio.findByNombre(rolNombre)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + rolNombre));
        usuario.setRol(rol);
        usuarioRepositorio.save(usuario);
    }

    @Override
    public long contarUsuarios() {
        return usuarioRepositorio.count();
    }
}