package com.registro.usuarios.controlador;

import com.registro.usuarios.modelo.Libro;
import com.registro.usuarios.modelo.Usuario;
import com.registro.usuarios.modelo.Venta;
import com.registro.usuarios.servicio.CarritoServicio;
import com.registro.usuarios.servicio.LibroServicio;
import com.registro.usuarios.servicio.UsuarioServicio;
import com.registro.usuarios.servicio.VentaServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cliente")
public class ClienteControlador {

    private final CarritoServicio carritoServicio;
    private final VentaServicio ventaServicio;
    private final LibroServicio libroServicio;
    private final UsuarioServicio usuarioServicio;

    public ClienteControlador(CarritoServicio carritoServicio,
                              VentaServicio ventaServicio,
                              LibroServicio libroServicio,
                              UsuarioServicio usuarioServicio) {
        this.carritoServicio = carritoServicio;
        this.ventaServicio = ventaServicio;
        this.libroServicio = libroServicio;
        this.usuarioServicio = usuarioServicio;
    }
    // PANEL PRINCIPAL DEL CLIENTE
    @GetMapping
    public String mostrarDashboardCliente(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        String email = principal.getName();
        Usuario usuario = usuarioServicio.buscarPorEmail(email);

        List<Venta> comprasUsuario = ventaServicio.listarVentas().stream()
                .filter(v -> v.getUsuario() != null && v.getUsuario().getEmail().equals(email))
                .collect(Collectors.toList());

        model.addAttribute("usuario", usuario);
        model.addAttribute("cantidadCarrito", carritoServicio.obtenerItems().size());
        model.addAttribute("totalCompras", comprasUsuario.size());
        model.addAttribute("totalPromociones", libroServicio.buscarConDescuento().size());
        model.addAttribute("librosRecomendados", libroServicio.listarTodos());
        model.addAttribute("comprasRecientes", comprasUsuario.stream().limit(3).collect(Collectors.toList()));

        return "cliente_dashboard";
    }
    

    // CATÁLOGO DE LIBROS
    @GetMapping("/catalogo")
    public String verCatalogo(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        model.addAttribute("libros", libroServicio.listarTodos());
        model.addAttribute("cantidadCarrito", carritoServicio.obtenerItems().size());
        return "cliente_catalogo";
    }
    // DETALLE DE LIBRO
    @GetMapping("/libro/{id}")
    public String verDetalleLibro(@PathVariable("id") Long id, Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        Libro libro = libroServicio.buscarPorId(id);
        if (libro == null) {
            return "redirect:/cliente/catalogo";
        }

        model.addAttribute("libro", libro);
        model.addAttribute("cantidadCarrito", carritoServicio.obtenerItems().size());
        return "cliente_detalle_libro";
    }
    // AGREGAR LIBRO AL CARRITO
    @PostMapping("/carrito/agregar")
    public String agregarAlCarrito(@RequestParam("idLibro") Long idLibro,
                                   @RequestParam(defaultValue = "1") int cantidad,
                                   Principal principal) {
        if (principal == null) return "redirect:/login";

        carritoServicio.agregarLibro(idLibro, cantidad);
        return "redirect:/cliente/carrito";
    }
    // VER CARRITO
    @GetMapping("/carrito")
    public String verCarrito(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        model.addAttribute("items", carritoServicio.obtenerItems());
        model.addAttribute("total", carritoServicio.calcularTotal());
        model.addAttribute("cantidadTotal", carritoServicio.obtenerCantidadTotalLibros());
        return "cliente_carrito";
    }
    // ELIMINAR DEL CARRITO
    @PostMapping("/carrito/eliminar")
    public String eliminarDelCarrito(@RequestParam("idLibro") Long idLibro) {
        carritoServicio.eliminarLibro(idLibro);
        return "redirect:/cliente/carrito";
    }
    // CONFIRMAR COMPRA
    @PostMapping("/carrito/comprar")
    public String confirmarCompra(@RequestParam("metodoPago") String metodoPago, Principal principal) {
        if (principal == null) return "redirect:/login";

        String email = principal.getName();
        Usuario usuario = usuarioServicio.buscarPorEmail(email);

        Venta venta = carritoServicio.confirmarCompra(usuario, metodoPago);
        return "redirect:/cliente/boleta/" + venta.getId();
    }
    // PROMOCIONES
    @GetMapping("/promociones")
    public String verPromociones(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        model.addAttribute("promociones", libroServicio.buscarConDescuento());
        model.addAttribute("totalPromociones", libroServicio.buscarConDescuento().size());
        return "cliente_promociones";
    }
    // HISTORIAL DE COMPRAS
    @GetMapping("/compras")
    public String verHistorialCompras(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        String email = principal.getName();
        Usuario usuario = usuarioServicio.buscarPorEmail(email);

        List<Venta> comprasUsuario = ventaServicio.listarVentas().stream()
                .filter(v -> v.getUsuario() != null && v.getUsuario().getEmail().equals(email))
                .collect(Collectors.toList());

        model.addAttribute("usuario", usuario);
        model.addAttribute("compras", comprasUsuario);
        return "cliente_compras";
    }

    // 🔹 Mostrar detalle de una compra específica
    @GetMapping("/compra/{id}")
    public String verDetalleCompra(@PathVariable Long id, Model model) {
        Venta venta = ventaServicio.buscarPorId(id);
        if (venta == null) {
            return "redirect:/cliente/compras?error";
        }
        model.addAttribute("venta", venta);
        return "cliente_detalle_compra"; // vista nueva o modal
    }
    // VER BOLETA
    @GetMapping("/boleta/{id}")
    public String verBoleta(@PathVariable Long id, Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        Venta venta = ventaServicio.buscarPorId(id);
        model.addAttribute("venta", venta);
        model.addAttribute("detalles", venta.getDetalles());
        return "cliente_boleta";
    }
    // PERFIL Y CONFIGURACIÓN DEL CLIENTE 
    @GetMapping("/configuracion")
    public String verConfiguracion(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        String email = principal.getName();
        Usuario usuario = usuarioServicio.buscarPorEmail(email);
        model.addAttribute("cliente", usuario);

        return "cliente_configuracion";
    }

    @PostMapping("/configuracion/actualizar")
    public String actualizarPerfil(@ModelAttribute("cliente") Usuario cliente,
                                   @RequestParam(value = "imagen", required = false) MultipartFile imagen,
                                   Principal principal) {

        if (principal == null) return "redirect:/login";

        Usuario usuarioActual = usuarioServicio.buscarPorEmail(principal.getName());

        // Guardar imagen si se sube
        if (imagen != null && !imagen.isEmpty()) {
            try {
                String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
                Path ruta = Paths.get("uploads/" + nombreArchivo);
                Files.createDirectories(ruta.getParent());
                Files.write(ruta, imagen.getBytes());
                usuarioActual.setImagenUrl("/uploads/" + nombreArchivo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Actualizar datos
        usuarioActual.setNombre(cliente.getNombre());
        usuarioActual.setApellido(cliente.getApellido());
        usuarioActual.setEmail(cliente.getEmail());
       

        if (cliente.getPassword() != null && !cliente.getPassword().isEmpty()) {
            usuarioActual.setPassword(cliente.getPassword());
        }

        usuarioServicio.actualizar(usuarioActual);

        return "redirect:/cliente/configuracion?success";
    }
}
