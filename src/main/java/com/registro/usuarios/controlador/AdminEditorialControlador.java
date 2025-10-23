package com.registro.usuarios.controlador;

import com.registro.usuarios.modelo.Editorial;
import com.registro.usuarios.servicio.EditorialServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/editoriales")
public class AdminEditorialControlador {

    private final EditorialServicio editorialServicio;

    public AdminEditorialControlador(EditorialServicio editorialServicio) {
        this.editorialServicio = editorialServicio;
    }

    @GetMapping("")
    public String listar(Model model) {
        // Siempre inicializa el objeto Editorial para el formulario de 'Nuevo'
        model.addAttribute("editorial", new Editorial());
        model.addAttribute("editoriales", editorialServicio.listarTodos());
        return "admin_editoriales";
    }

    @PostMapping("")
    public String guardar(@ModelAttribute Editorial editorial) {
        editorialServicio.guardar(editorial);
        return "redirect:/admin/editoriales";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        // Carga la editorial para editar y la pasa al mismo formulario
        model.addAttribute("editorial", editorialServicio.buscarPorId(id));
        model.addAttribute("editoriales", editorialServicio.listarTodos());
        // Se mantiene la misma vista
        return "admin_editoriales"; 
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        editorialServicio.eliminar(id);
        return "redirect:/admin/editoriales";
    }
}