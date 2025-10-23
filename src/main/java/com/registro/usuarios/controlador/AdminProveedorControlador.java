package com.registro.usuarios.controlador;

import com.registro.usuarios.modelo.Proveedor;
import com.registro.usuarios.servicio.ProveedorServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/proveedores")
public class AdminProveedorControlador {

    private final ProveedorServicio proveedorServicio;

    public AdminProveedorControlador(ProveedorServicio proveedorServicio) {
        this.proveedorServicio = proveedorServicio;
    }

    @GetMapping("")
    public String listar(Model model) {
        // Inicializa el objeto para el formulario de 'Nuevo'
        model.addAttribute("proveedor", new Proveedor());
        model.addAttribute("proveedores", proveedorServicio.listarTodos());
        return "admin_proveedores"; // La vista única
    }

    // Eliminamos el @GetMapping("/nuevo")

    @PostMapping("")
    public String guardar(@ModelAttribute Proveedor proveedor) {
        // No necesitamos lógica compleja de hijos, el guardado es directo
        proveedorServicio.guardar(proveedor);
        return "redirect:/admin/proveedores";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        // Carga el proveedor para editar y lo pasa al formulario
        model.addAttribute("proveedor", proveedorServicio.buscarPorId(id));
        model.addAttribute("proveedores", proveedorServicio.listarTodos());
        return "admin_proveedores"; // Retorna a la vista única
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        proveedorServicio.eliminar(id);
        return "redirect:/admin/proveedores";
    }
}