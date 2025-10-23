package com.registro.usuarios.controlador;

import com.registro.usuarios.modelo.Rol;
import com.registro.usuarios.modelo.Usuario;
import com.registro.usuarios.servicio.RolServicio;
import com.registro.usuarios.servicio.UsuarioServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/admin/usuarios") // Mapeo base para la gestión de usuarios
public class AdminUsuarioControlador {

    private final UsuarioServicio usuarioServicio;
    private final RolServicio rolServicio;

    public AdminUsuarioControlador(UsuarioServicio usuarioServicio,
                                   RolServicio rolServicio) {
        this.usuarioServicio = usuarioServicio;
        this.rolServicio = rolServicio;
    }
    // 1. LISTAR/VISTA (READ) - GET /admin/usuarios
    
    @GetMapping 
    public String gestionUsuarios(Model model, Principal principal) {
        // Obtenemos el usuario logueado para mostrar en la cabecera/fragmento
        Usuario usuarioLogueado = usuarioServicio.buscarPorEmail(principal.getName());
        model.addAttribute("usuario", usuarioLogueado); 
        
        // Listado de todos los usuarios
        model.addAttribute("usuarios", usuarioServicio.listarTodos());
        // Lista de roles (para formularios/modals de creación/edición)
        model.addAttribute("roles", rolServicio.listarTodos());
        
        return "admin_usuarios"; // Plantilla que muestra la tabla de usuarios
    }
    
    // 2. FORMULARIO NUEVO (CREATE) - GET /admin/usuarios/nuevo
    
    @GetMapping("/nuevo")
    public String nuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolServicio.listarTodos());
        return "admin_usuario_form"; // Plantilla del formulario
    }
    // 3. GUARDAR NUEVO (CREATE) - POST /admin/usuarios
    
    @PostMapping
    public String guardarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes ra) {
        if (usuario.getRol() != null && usuario.getRol().getId() != null) {
            Rol rol = rolServicio.buscarPorId(usuario.getRol().getId());
            usuario.setRol(rol);
        }
        usuarioServicio.guardar(usuario); // Asume que este método encripta la contraseña
        ra.addFlashAttribute("msgExito", "Usuario '" + usuario.getNombre() + "' creado exitosamente. ✅");
        return "redirect:/admin/usuarios";
    }
    // 4. FORMULARIO EDITAR (UPDATE) - GET /admin/usuarios/editar/{id}
    
    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioServicio.buscarPorId(id));
        model.addAttribute("roles", rolServicio.listarTodos());
        return "admin_usuario_form";
    }

  
    // 5. ACTUALIZAR (UPDATE) - POST /admin/usuarios/actualizar/{id}
    @PostMapping("/actualizar/{id}")
    public String actualizarUsuario(@PathVariable Long id, @ModelAttribute Usuario usuario, RedirectAttributes ra) {
        usuario.setId(id);
        if (usuario.getRol() != null && usuario.getRol().getId() != null) {
            Rol rol = rolServicio.buscarPorId(usuario.getRol().getId());
            usuario.setRol(rol);
        }
        usuarioServicio.actualizar(usuario);
        ra.addFlashAttribute("msgExito", "Usuario actualizado exitosamente. ✅");
        return "redirect:/admin/usuarios";
    }

    // 6. ELIMINAR (DELETE) - GET /admin/usuarios/eliminar/{id}
    
    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes ra) {
        usuarioServicio.eliminar(id);
        ra.addFlashAttribute("msgExito", "Usuario eliminado exitosamente. 🗑️");
        return "redirect:/admin/usuarios";
    }
}