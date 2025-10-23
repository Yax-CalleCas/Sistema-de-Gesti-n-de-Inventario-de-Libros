package com.registro.usuarios.controlador;

import com.registro.usuarios.modelo.*;   
import com.registro.usuarios.servicio.*;
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.stream.Collectors;
import java.security.Principal;

@Controller
@RequestMapping("/trabajador")
public class TrabajadorControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;
    @Autowired
    private LibroServicio libroServicio;
    @Autowired
    private EditorialServicio editorialServicio;
    @Autowired
    private ProveedorServicio proveedorServicio;
    @Autowired
    private CategoriaServicio categoriaServicio;
    @Autowired
    private AutorServicio autorServicio;
    // DASHBOARD PRINCIPAL
    @GetMapping("")
    public String vistaTrabajador(Model model, Principal principal) {
        //  Cargar trabajador logueado
        Usuario usuario = usuarioServicio.buscarPorEmail(principal.getName());
        model.addAttribute("usuario", usuario);

        //  Métricas principales
        model.addAttribute("totalLibros", libroServicio.contarLibros());
        model.addAttribute("totalEditoriales", editorialServicio.listarTodos().size());
        model.addAttribute("totalProveedores", proveedorServicio.listarTodos().size());

        //  Libros con stock menor a 10
        List<Libro> librosStockBajo = libroServicio.listarTodos()
                .stream()
                .filter(libro -> libro.getStock() < 10)
                .collect(Collectors.toList());

        model.addAttribute("librosStockBajo", librosStockBajo);

        //  Mensaje de alerta (SweetAlert2)
        if (!librosStockBajo.isEmpty()) {
            model.addAttribute("alertaStock",
                    "Hay " + librosStockBajo.size() + " libros con stock crítico (menos de 10 unidades).");
        }

        return "trabajador/dashboard";
    }
    // CRUD LIBROS
    @GetMapping("/libros")
    public String listarLibros(Model modelo) {
        modelo.addAttribute("libros", libroServicio.listarTodos());
        return "trabajador/libros/listar";
    }

    @GetMapping("/libros/nuevo")
    public String mostrarFormularioLibro(Model modelo) {
        modelo.addAttribute("libro", new Libro());
        modelo.addAttribute("editoriales", editorialServicio.listarTodos());
        modelo.addAttribute("proveedores", proveedorServicio.listarTodos());
        modelo.addAttribute("categorias", categoriaServicio.listarTodos());
        modelo.addAttribute("autores", autorServicio.listarTodos());
        return "trabajador/libros/formulario";
    }

    @PostMapping("/libros/guardar")
    public String guardarLibro(@ModelAttribute("libro") Libro libro, RedirectAttributes atributos) {
        libroServicio.guardar(libro);
        atributos.addFlashAttribute("msgExito", "✅ Libro guardado exitosamente.");
        return "redirect:/trabajador/libros";
    }

    @GetMapping("/libros/editar/{id}")
    public String mostrarFormularioEditarLibro(@PathVariable Long id, Model modelo) {
        modelo.addAttribute("libro", libroServicio.buscarPorId(id));
        modelo.addAttribute("editoriales", editorialServicio.listarTodos());
        modelo.addAttribute("proveedores", proveedorServicio.listarTodos());
        modelo.addAttribute("categorias", categoriaServicio.listarTodos());
        modelo.addAttribute("autores", autorServicio.listarTodos());
        return "trabajador/libros/formulario";
    }

    @GetMapping("/libros/eliminar/{id}")
    public String eliminarLibro(@PathVariable Long id, RedirectAttributes atributos) {
        libroServicio.eliminar(id);
        atributos.addFlashAttribute("msgExito", "Libro eliminado correctamente.");
        return "redirect:/trabajador/libros";
    }

 // CRUD EDITORIALES
    @GetMapping("/editoriales")
    public String listarEditoriales(
        Model modelo, 
        @RequestParam(required = false) Long editId // Captura el parámetro de edición
    ) {
        // 1. Siempre lista todas las editoriales para la tabla.
        modelo.addAttribute("editoriales", editorialServicio.listarTodos());

        // 2. Controla qué objeto se carga en el formulario (editorialActual).
        if (editId != null) {
        	// Modo Edición: Cargar la editorial existente o una nueva si no se encuentra.
            modelo.addAttribute("editorialActual", editorialServicio.buscarPorId(editId)
                 != null ? editorialServicio.buscarPorId(editId) : new Editorial());
        } else {
            // Modo Creación: Siempre cargar un objeto nuevo y vacío.
            modelo.addAttribute("editorialActual", new Editorial());
        }

        return "trabajador/editoriales/listar";
    }


    @PostMapping("/editoriales/guardar")
    public String guardarEditorial(
        @ModelAttribute("editorialActual") Editorial editorial, 
        RedirectAttributes atributos
    ) {
        editorialServicio.guardar(editorial);
        atributos.addFlashAttribute("msgExito", "Editorial guardada exitosamente.");
        return "redirect:/trabajador/editoriales";
    }

    @GetMapping("/editoriales/eliminar/{id}")
    public String eliminarEditorial(@PathVariable Long id, RedirectAttributes atributos) {
        editorialServicio.eliminar(id);
        atributos.addFlashAttribute("msgExito", "Editorial eliminada correctamente.");
        return "redirect:/trabajador/editoriales";
    }
 // CRUD PROVEEDORES

 // Método unificado para listar y preparar el formulario (Crear/Editar)
 @GetMapping("/proveedores")
 public String listarProveedores(
     Model modelo, 
     @RequestParam(required = false) Long editId // Parámetro para modo edición
 ) {
     // 1. Listar todos los proveedores para la tabla
     modelo.addAttribute("proveedores", proveedorServicio.listarTodos());

     // 2. Controlar el objeto para el formulario (Crear o Editar)
     if (editId != null) {
         modelo.addAttribute("proveedorActual", proveedorServicio.buscarPorId(editId)
              != null ? proveedorServicio.buscarPorId(editId) : new Proveedor());
     } else {
         // Modo Creación: Cargar un objeto nuevo y vacío
         modelo.addAttribute("proveedorActual", new Proveedor());
     }
     return "trabajador/proveedores/listar"; 
 }


 @PostMapping("/proveedores/guardar")
 public String guardarProveedor(
     @ModelAttribute("proveedorActual") Proveedor proveedor, 
     RedirectAttributes atributos
 ) {
     proveedorServicio.guardar(proveedor);
     atributos.addFlashAttribute("msgExito", "Proveedor guardado exitosamente.");
     return "redirect:/trabajador/proveedores";
 }
 
 @GetMapping("/proveedores/eliminar/{id}")
 public String eliminarProveedor(@PathVariable Long id, RedirectAttributes atributos) {
     proveedorServicio.eliminar(id);
     atributos.addFlashAttribute("msgExito", "Proveedor eliminado correctamente.");
     return "redirect:/trabajador/proveedores";
 }

//CRUD CATEGORÍAS

@GetMapping("/categorias")
public String listarCategorias(
  Model modelo, 
  @RequestParam(required = false) Long editId // Captura el parámetro de edición
) {
  // 1. Listar todas las categorías para la tabla
  modelo.addAttribute("categorias", categoriaServicio.listarTodos());

  // 2. Controlar el objeto para el formulario (Crear o Editar)
  if (editId != null) {
      // Modo Edición: Cargar la categoría existente
      modelo.addAttribute("categoriaActual", categoriaServicio.buscarPorId(editId)
            != null ? categoriaServicio.buscarPorId(editId) : new Categoria());
  } else {
      // Modo Creación: Cargar un objeto nuevo y vacío
      modelo.addAttribute("categoriaActual", new Categoria());
  }

  return "trabajador/categorias/listar"; 
}

@PostMapping("/categorias/guardar")
public String guardarCategoria(
  @ModelAttribute("categoriaActual") Categoria categoria, 
  RedirectAttributes atributos
) {
  categoriaServicio.guardar(categoria);
  atributos.addFlashAttribute("msgExito", "Categoría guardada exitosamente.");
  return "redirect:/trabajador/categorias";
}

@GetMapping("/categorias/eliminar/{id}")
public String eliminarCategoria(@PathVariable Long id, RedirectAttributes atributos) {
  try {
      categoriaServicio.eliminar(id);
      atributos.addFlashAttribute("msgExito", "Categoría eliminada correctamente.");
  } catch (Exception e) {
      atributos.addFlashAttribute("msgError", "No se pudo eliminar la categoría. Verifica si tiene libros asociados.");
  }
  return "redirect:/trabajador/categorias";
}
    // PERFIL / CONFIGURACIÓN DEL TRABAJADOR
    @GetMapping("/configuracion")
    public String vistaConfiguracion(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Usuario usuarioActual = usuarioServicio.buscarPorEmail(principal.getName());
        model.addAttribute("usuario", usuarioActual);
        return "trabajador/configuracion";
    }

    @PostMapping("/configuracion/actualizar")
    public String actualizarPerfilTrabajador(@ModelAttribute("usuario") Usuario usuarioForm,
                                             Principal principal,
                                             RedirectAttributes atributos) {
        if (principal == null) {
            return "redirect:/login";
        }

        Usuario usuarioActual = usuarioServicio.buscarPorEmail(principal.getName());

        if (!usuarioForm.getEmail().equals(usuarioActual.getEmail()) &&
                usuarioServicio.buscarPorEmail(usuarioForm.getEmail()) != null) {
            atributos.addFlashAttribute("msgError", "El email ingresado ya está registrado por otro usuario.");
            return "redirect:/trabajador/configuracion";
        }

        usuarioActual.setNombre(usuarioForm.getNombre());
        usuarioActual.setApellido(usuarioForm.getApellido());
        usuarioActual.setEmail(usuarioForm.getEmail());
        usuarioActual.setImagenUrl(usuarioForm.getImagenUrl());

        if (usuarioForm.getPassword() != null && !usuarioForm.getPassword().isEmpty()) {
            usuarioActual.setPassword(usuarioForm.getPassword());
        }

        usuarioServicio.actualizar(usuarioActual);
        atributos.addFlashAttribute("msgExito", " Perfil actualizado exitosamente.");

        return "redirect:/trabajador/configuracion";
    }
    // LOGIN
    @GetMapping("/login")
    public String vistaLogin() { 
        return "login";
    }
}
