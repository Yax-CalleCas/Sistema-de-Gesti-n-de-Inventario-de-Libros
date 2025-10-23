package com.registro.usuarios.controlador;

import com.registro.usuarios.modelo.Autor;
import com.registro.usuarios.servicio.AutorServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/autores")
public class AdminAutorControlador {

    private final AutorServicio autorServicio;

    public AdminAutorControlador(AutorServicio autorServicio) {
        this.autorServicio = autorServicio;
    }

    @GetMapping("")
    public String listar(Model model) {
        model.addAttribute("autores", autorServicio.listarTodos());
        return "admin_autores";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("autor", new Autor());
        return "admin_autor_form";
    }

    @PostMapping("")
    public String guardar(@ModelAttribute Autor autor) {
        autorServicio.guardar(autor);
        return "redirect:/admin/autores";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("autor", autorServicio.buscarPorId(id));
        return "admin_autor_form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        autorServicio.eliminar(id);
        return "redirect:/admin/autores";
    }
}
