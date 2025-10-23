package com.registro.usuarios.controlador;

import com.registro.usuarios.modelo.Categoria;
import com.registro.usuarios.servicio.CategoriaServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categorias")
public class AdminCategoriaControlador {

    private final CategoriaServicio categoriaServicio;

    public AdminCategoriaControlador(CategoriaServicio categoriaServicio) {
        this.categoriaServicio = categoriaServicio;
    }

    @GetMapping("")
    public String listar(Model model) {
        // Inicializa el objeto para el formulario de 'Nueva'
        model.addAttribute("categoria", new Categoria());
        model.addAttribute("categorias", categoriaServicio.listarTodos());
        return "admin_categorias"; // La vista única
    }

    // Eliminamos el @GetMapping("/nuevo")

    @PostMapping("")
    public String guardar(@ModelAttribute Categoria categoriaDesdeFormulario) {
        
        if (categoriaDesdeFormulario.getId() != null) {
            // Lógica para ACTUALIZAR
            Categoria categoriaExistente = categoriaServicio.buscarPorId(categoriaDesdeFormulario.getId());
            

            if(categoriaExistente.getLibros() != null) {

                 categoriaDesdeFormulario.setLibros(categoriaExistente.getLibros());
            } else {
                 categoriaDesdeFormulario.setLibros(null); 
            }
            
            categoriaServicio.guardar(categoriaDesdeFormulario);
            
        } else {
            // Lógica para CREAR
            categoriaServicio.guardar(categoriaDesdeFormulario);
        }
        
        return "redirect:/admin/categorias";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        // Carga la categoría para editar y la pasa al mismo formulario
        model.addAttribute("categoria", categoriaServicio.buscarPorId(id));
        model.addAttribute("categorias", categoriaServicio.listarTodos());
        return "admin_categorias"; // Retorna a la vista única
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        categoriaServicio.eliminar(id);
        return "redirect:/admin/categorias";
    }
}