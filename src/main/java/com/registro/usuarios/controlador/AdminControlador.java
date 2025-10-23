package com.registro.usuarios.controlador;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.registro.usuarios.modelo.*;
import com.registro.usuarios.servicio.*;

@Controller
@RequestMapping("/admin")
public class AdminControlador { 

    private final UsuarioServicio usuarioServicio;
    private final RolServicio rolServicio;
    private final LibroServicio libroServicio;
    private final VentaServicio ventaServicio;

    public AdminControlador(UsuarioServicio usuarioServicio,
                            RolServicio rolServicio,
                            LibroServicio libroServicio,
                            VentaServicio ventaServicio) {
        this.usuarioServicio = usuarioServicio;
        this.rolServicio = rolServicio;
        this.libroServicio = libroServicio;
        this.ventaServicio = ventaServicio;
    }
    // Panel PRINCIPAL (Dashboard) - Mapeo: /admin
    @GetMapping("")
    public String vistaAdmin(Model model, Principal principal) {
        Usuario usuario = usuarioServicio.buscarPorEmail(principal.getName());
        model.addAttribute("usuario", usuario);

        // Se mantiene la lógica de cálculo de métricas para el Dashboard
        List<Usuario> usuarios = usuarioServicio.listarTodos();
        List<Venta> ventas = ventaServicio.listarVentas();

        // Cálculo de Clientes Activos
        long clientesActivos = usuarios.stream()
                .filter(u -> u.getRol() != null &&
                        "CLIENTE".equalsIgnoreCase(u.getRol().getNombre()))
                .count();

        // Cálculo del Total de Ventas
        BigDecimal totalVentas = ventas.stream()
                .map(Venta::getTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Métricas para el Dashboard
        model.addAttribute("totalUsuarios", usuarioServicio.contarUsuarios());
        model.addAttribute("clientesActivos", clientesActivos);
        model.addAttribute("totalLibros", libroServicio.contarLibros());
        model.addAttribute("totalRoles", rolServicio.contarRoles());
        model.addAttribute("totalVentas", totalVentas);
		// Datos de Ventas por Mes para el Gráfico
        LocalDate hoy = LocalDate.now();
        Map<String, Double> ventasPorMes = new LinkedHashMap<>();

        for (int i = 5; i >= 0; i--) {
            LocalDate fechaMes = hoy.minusMonths(i);
            @SuppressWarnings("deprecation")
			String nombreMes = fechaMes.getMonth()
                    .getDisplayName(TextStyle.SHORT, new Locale("es", "ES"));

            BigDecimal totalMes = ventas.stream()
                    .filter(v -> v.getFecha() != null &&
                            v.getFecha().getMonthValue() == fechaMes.getMonthValue() &&
                            v.getFecha().getYear() == fechaMes.getYear())
                    .map(Venta::getTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            ventasPorMes.put(nombreMes, totalMes.doubleValue());
        }

        model.addAttribute("labelsMeses", ventasPorMes.keySet());
        model.addAttribute("dataVentas", ventasPorMes.values());
        model.addAttribute("libros", libroServicio.listarTodos());

        return "admin_dashboard";
    }
    // CONFIGURACIÓN DE PERFIL - Mapeo: /admin/configuracion
    
    @GetMapping("/configuracion")
    public String configuracion(Model model, Principal principal) {
        Usuario usuario = usuarioServicio.buscarPorEmail(principal.getName());
        model.addAttribute("usuario", usuario);
        return "admin_configuracion";
    }
    
    @PostMapping("/configuracion/actualizar")
    public String actualizarConfiguracion(@ModelAttribute Usuario usuarioActualizado, Principal principal, RedirectAttributes ra) {
        Usuario usuarioExistente = usuarioServicio.buscarPorEmail(principal.getName());

        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setApellido(usuarioActualizado.getApellido());
        
        // Solo actualiza el email si el nuevo no está vacío
        if (usuarioActualizado.getEmail() != null && !usuarioActualizado.getEmail().isEmpty()) {
             usuarioExistente.setEmail(usuarioActualizado.getEmail());
        }

        // Si el usuario quiere cambiar la contraseña
        if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isEmpty()) {
            usuarioExistente.setPassword(usuarioActualizado.getPassword());
        }

        // Si quiere actualizar su imagen
        if (usuarioActualizado.getImagenUrl() != null && !usuarioActualizado.getImagenUrl().isEmpty()) {
            usuarioExistente.setImagenUrl(usuarioActualizado.getImagenUrl());
        }

        usuarioServicio.actualizar(usuarioExistente);
        ra.addFlashAttribute("msgExito", "Configuración de perfil actualizada. ");
        
        return "redirect:/admin/configuracion";
    }
}