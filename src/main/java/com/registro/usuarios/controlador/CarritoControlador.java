package com.registro.usuarios.controlador;

import com.registro.usuarios.modelo.Libro;
import com.registro.usuarios.modelo.Usuario;
import com.registro.usuarios.servicio.CarritoServicio;
import com.registro.usuarios.servicio.LibroServicio;
import com.registro.usuarios.servicio.UsuarioServicio;
import com.registro.usuarios.servicio.VentaServicio;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequestMapping("/carrito")
public class CarritoControlador {

    private final CarritoServicio carritoServicio;
    private final LibroServicio libroServicio;
    private final UsuarioServicio usuarioServicio;


    public CarritoControlador(CarritoServicio carritoServicio,
                              LibroServicio libroServicio,
                              UsuarioServicio usuarioServicio,
                              VentaServicio ventaServicio) {
        this.carritoServicio = carritoServicio;
        this.libroServicio = libroServicio;
        this.usuarioServicio = usuarioServicio;
        
    }

    /**
     * Mostrar carrito actual del cliente
     */
    @GetMapping
    public String verCarrito(Model model) {
        model.addAttribute("items", carritoServicio.obtenerItems());
        model.addAttribute("total", carritoServicio.calcularTotal());
        return "cliente_carrito"; 
    }

    /**
     * Agregar un libro al carrito
     */
    @PostMapping("/agregar/{id}")
    public String agregarAlCarrito(@PathVariable Long id,
                                   @RequestParam(defaultValue = "1") int cantidad) {
        Libro libro = libroServicio.buscarPorId(id);
        if (libro != null) {
            carritoServicio.agregarLibro(id, cantidad);
        }
        return "redirect:/carrito";
    }

    /**
     * Eliminar un libro del carrito
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarDelCarrito(@PathVariable Long id) {
        carritoServicio.eliminarLibro(id);
        return "redirect:/carrito";
    }

    /**
     * Confirmar compra desde el carrito
     */
    @PostMapping("/confirmar")
    public String confirmarCompra(@RequestParam("metodoPago") String metodoPago,
                                  @AuthenticationPrincipal User user,
                                  RedirectAttributes redirectAttributes) { // 🎯 Añadimos RedirectAttributes

        if (user == null) {
            // Manejar sesión expirada o no iniciada
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para finalizar la compra.");
            return "redirect:/login";
        }

        Usuario usuario = usuarioServicio.buscarPorEmail(user.getUsername());

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Error al identificar el usuario.");
            return "redirect:/carrito";
        }
        
        if (carritoServicio.estaVacio()) {
            redirectAttributes.addFlashAttribute("error", "El carrito está vacío.");
            return "redirect:/carrito";
        }
        try {  
          
            // 3. Vacía el carrito.
            carritoServicio.confirmarCompra(usuario, metodoPago);
            
            // Mensaje de éxito
            redirectAttributes.addFlashAttribute("exito", "¡Compra finalizada con éxito! Te enviaremos un correo de confirmación.");

        } catch (RuntimeException e) {
            // Manejo de error de stock insuficiente o base de datos
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/carrito"; 
        }

        return "redirect:/cliente/compras"; 
    }
}
