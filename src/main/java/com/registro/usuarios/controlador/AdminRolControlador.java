package com.registro.usuarios.controlador;

import com.registro.usuarios.modelo.Rol;
import com.registro.usuarios.servicio.RolServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/roles") // Mapeo base: /admin/roles
public class AdminRolControlador {

    private final RolServicio rolServicio;

    public AdminRolControlador(RolServicio rolServicio) {
        this.rolServicio = rolServicio;
    }
    // LISTAR/VISTA (GET /admin/roles)
    @GetMapping
    public String gestionRoles(Model model) {
        model.addAttribute("roles", rolServicio.listarTodos());
        model.addAttribute("rol", new Rol()); // Para el formulario de nuevo rol
        return "admin_roles"; // Nombre de la plantilla de listado de roles
    }
    // GUARDAR (POST /admin/roles)
    @PostMapping
    public String guardarRol(@ModelAttribute Rol rol, RedirectAttributes ra) {
        rolServicio.guardar(rol);
        ra.addFlashAttribute("msgExito", "Rol '" + rol.getNombre() + "' creado exitosamente. ✅");
        return "redirect:/admin/roles";
    }
    // FORMULARIO EDITAR (GET /admin/roles/editar/{id})

    @GetMapping("/editar/{id}")
    public String editarRol(@PathVariable Long id, Model model) {
        model.addAttribute("rol", rolServicio.buscarPorId(id));
        return "admin_rol_form"; // Plantilla para editar un rol
    }
    // ACTUALIZAR (POST /admin/roles/actualizar/{id})
    
    @PostMapping("/actualizar/{id}")
    public String actualizarRol(@PathVariable Long id, @ModelAttribute Rol rol, RedirectAttributes ra) {
        rol.setId(id);
        rolServicio.actualizar(rol);
        ra.addFlashAttribute("msgExito", "Rol actualizado exitosamente. ✅");
        return "redirect:/admin/roles";
    }
    // ELIMINAR (GET /admin/roles/eliminar/{id})
    
    @GetMapping("/eliminar/{id}")
    public String eliminarRol(@PathVariable Long id, RedirectAttributes ra) {
        // En una app real, aquí se debe verificar que el rol no esté siendo usado
        rolServicio.eliminar(id);
        ra.addFlashAttribute("msgExito", "Rol eliminado exitosamente. 🗑️");
        return "redirect:/admin/roles";
    }
}