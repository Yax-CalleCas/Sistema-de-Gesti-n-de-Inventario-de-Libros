package com.registro.usuarios.controlador;

import com.registro.usuarios.modelo.*;
import com.registro.usuarios.servicio.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;


@Controller
@RequestMapping("/admin/ventas")
public class VentaGestionControlador {

    private final VentaServicio ventaServicio;
    private final UsuarioServicio usuarioServicio;
    private final LibroServicio libroServicio;

    public VentaGestionControlador(VentaServicio ventaServicio,
                                   UsuarioServicio usuarioServicio,
                                   LibroServicio libroServicio) {
        this.ventaServicio = ventaServicio;
        this.usuarioServicio = usuarioServicio;
        this.libroServicio = libroServicio;
    }

    // Listar todas las ventas
    @GetMapping
    public String listarVentas(Model model) {
        model.addAttribute("ventas", ventaServicio.listarVentas());
        return "admin/ventas/ventas";
    }

    //  Mostrar formulario para registrar una nueva venta
 
    @GetMapping("/nueva")
    public String formularioVentas(Model model) {
        model.addAttribute("venta", new Venta());
        model.addAttribute("usuarios", usuarioServicio.listarTodos());
        model.addAttribute("libros", libroServicio.listarTodos());
        return "admin/ventas/formularioVentas";
    }

    // Guardar la venta y descontar el stock de los libros vendidos
    @PostMapping("/guardar")
    public String guardarVenta(@ModelAttribute("venta") Venta venta,
                               RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioServicio.buscarPorId(venta.getUsuario().getId());
            if (usuario == null) {
                throw new RuntimeException("El cliente seleccionado no existe.");
            }

            if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
                throw new RuntimeException("Debe agregar al menos un libro a la venta.");
            }

            BigDecimal totalVenta = BigDecimal.ZERO; //  Acumulador del total

            for (DetalleVenta detalle : venta.getDetalles()) {
                Libro libro = libroServicio.buscarPorId(detalle.getLibro().getId());
                if (libro == null) {
                    throw new RuntimeException("Libro no encontrado.");
                }

                if (libro.getStock() < detalle.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente para el libro: " + libro.getTitulo());
                }

                //  Calcular precio unitario con descuento
                BigDecimal precioUnitario = libro.getPrecioFinal();

                //  Calcular subtotal = precio * cantidad
                BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(detalle.getCantidad()));

                //  Asignar valores al detalle
                detalle.setLibro(libro);
                detalle.setPrecioUnitario(precioUnitario);
                detalle.setSubtotal(subtotal);
                detalle.setVenta(venta);
                // Descontar stock del libro
                libro.setStock(libro.getStock() - detalle.getCantidad());
                libroServicio.guardar(libro);

                //  Sumar al total general
                totalVenta = totalVenta.add(subtotal);
            }

            //  Establecer campos finales de la venta
            venta.setUsuario(usuario);
            venta.setFecha(java.time.LocalDateTime.now());
            venta.setTotal(totalVenta);
            venta.setDetalles(venta.getDetalles());

            //  Registrar la venta completa
            ventaServicio.guardarVenta(venta);

            redirectAttributes.addFlashAttribute("exito", "Venta registrada con éxito y stock actualizado ✅");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/ventas/nueva";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error interno al registrar la venta: " + e.getMessage());
            return "redirect:/admin/ventas/nueva";
        }

        return "redirect:/admin/ventas";
    }
}
