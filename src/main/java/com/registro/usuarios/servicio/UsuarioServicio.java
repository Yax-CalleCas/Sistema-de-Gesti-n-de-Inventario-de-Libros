package com.registro.usuarios.servicio;

import com.registro.usuarios.controlador.dto.UsuarioRegistroDTO;
import com.registro.usuarios.modelo.Usuario;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.List;

public interface UsuarioServicio extends UserDetailsService {

    // Crear usuario desde formulario de registro
    Usuario guardar(UsuarioRegistroDTO registroDTO);

    // Crear usuario con rol explícito (ej: Admin crea un nuevo usuario)
    Usuario guardarConRol(UsuarioRegistroDTO registroDTO, String rolNombre);

    // Crear o actualizar usuario directamente (CRUD admin)
    Usuario guardar(Usuario usuario);

    // Actualizar usuario existente
    Usuario actualizar(Usuario usuario);

    // Eliminar usuario
    void eliminar(Long usuarioId);

    // Asignar rol a usuario existente
    void asignarRol(Long usuarioId, String rolNombre);

    // Listar todos los usuarios
    List<Usuario> listarTodos(); 

    // Resetear contraseña
    Usuario resetearPassword(Long usuarioId, String nuevaPassword);

    // Buscar por email
    Usuario buscarPorEmail(String email);

    // Buscar por id
    Usuario buscarPorId(Long id);

    // MÉTODO AÑADIDO: Contar usuarios (útil para dashboards)
    long contarUsuarios(); 
}