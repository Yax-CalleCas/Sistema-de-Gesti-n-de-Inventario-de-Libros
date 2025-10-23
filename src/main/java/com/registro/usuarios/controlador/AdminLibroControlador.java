package com.registro.usuarios.controlador;

import com.registro.usuarios.modelo.Libro;
import com.registro.usuarios.servicio.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/admin/libros")
public class AdminLibroControlador {

    private final LibroServicio libroServicio;
    private final CategoriaServicio categoriaServicio;
    private final AutorServicio autorServicio;
    private final EditorialServicio editorialServicio;
    private final ProveedorServicio proveedorServicio;

    public AdminLibroControlador(
            LibroServicio libroServicio,
            CategoriaServicio categoriaServicio,
            AutorServicio autorServicio,
            EditorialServicio editorialServicio,
            ProveedorServicio proveedorServicio) {

        this.libroServicio = libroServicio;
        this.categoriaServicio = categoriaServicio;
        this.autorServicio = autorServicio;
        this.editorialServicio = editorialServicio;
        this.proveedorServicio = proveedorServicio;
    }

    /** ===== Listar todos los libros ===== */
    @GetMapping("")
    public String listarLibros(Model model) {
        model.addAttribute("libros", libroServicio.listarTodos());
        return "admin/libros/listar"; // Nueva vista separada
    }

    /** ===== Mostrar formulario para nuevo libro ===== */
    @GetMapping("/nuevo")
    public String nuevoLibro(Model model) {
        model.addAttribute("libro", new Libro());
        cargarListasRelacionadas(model);
        return "admin/libros/formulario"; // Vista del formulario
    }

    /** ===== Guardar o actualizar libro ===== */
    @PostMapping("/guardar")
    public String guardarLibro(
            @ModelAttribute Libro libro,
            @RequestParam Long categoria,
            @RequestParam Long autor,
            @RequestParam Long editorial,
            @RequestParam Long proveedor,
            @RequestParam(required = false) String imagenUrl,
            @RequestParam(required = false) BigDecimal descuento,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaPublicacion,
            @RequestParam(required = false) String idioma,
            @RequestParam(required = false, defaultValue = "0") int numeroPaginas) {

        // Asignar relaciones
        libro.setCategoria(categoriaServicio.buscarPorId(categoria));
        libro.setAutor(autorServicio.buscarPorId(autor));
        libro.setEditorial(editorialServicio.buscarPorId(editorial));
        libro.setProveedor(proveedorServicio.buscarPorId(proveedor));

        // Campos opcionales
        libro.setImagenUrl(imagenUrl);
        libro.setDescuento(descuento != null ? descuento : BigDecimal.ZERO);
        libro.setIsbn(isbn);
        libro.setFechaPublicacion(fechaPublicacion);
        libro.setIdioma(idioma);
        libro.setNumeroPaginas(numeroPaginas);

        libroServicio.guardar(libro);
        return "redirect:/admin/libros?success";
    }

    /** ===== Editar libro existente ===== */
    @GetMapping("/editar/{id}")
    public String editarLibro(@PathVariable Long id, Model model) {
        Libro libro = libroServicio.buscarPorId(id);
        if (libro == null)
            return "redirect:/admin/libros?error=notfound";

        model.addAttribute("libro", libro);
        cargarListasRelacionadas(model);
        return "admin/libros/formulario"; // Misma vista que el de nuevo libro
    }

    /** ===== Eliminar libro ===== */
    @GetMapping("/eliminar/{id}")
    public String eliminarLibro(@PathVariable Long id) {
        if (libroServicio.buscarPorId(id) == null)
            return "redirect:/admin/libros?error=notfound";

        libroServicio.eliminar(id);
        return "redirect:/admin/libros?deleted";
    }

    /** ===== Método auxiliar para combos ===== */
    private void cargarListasRelacionadas(Model model) {
        model.addAttribute("categorias", categoriaServicio.listarTodos());
        model.addAttribute("autores", autorServicio.listarTodos());
        model.addAttribute("editoriales", editorialServicio.listarTodos());
        model.addAttribute("proveedores", proveedorServicio.listarTodos());
    }
}
