package com.registro.usuarios.controlador;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.registro.usuarios.controlador.dto.UsuarioRegistroDTO;
import com.registro.usuarios.servicio.UsuarioServicio;

@Controller
@RequestMapping("/registro")
public class RegistroUsuarioControlador {

    private final UsuarioServicio usuarioServicio;

    public RegistroUsuarioControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @ModelAttribute("usuario")
    public UsuarioRegistroDTO retornarNuevoUsuarioRegistroDTO() {
        UsuarioRegistroDTO dto = new UsuarioRegistroDTO();
        dto.setRol("ROLE_CLIENTE"); //  asignamos rol por defecto
        return dto;
    }

    @GetMapping
    public String mostrarFormularioDeRegistro() {
        return "registro";
    }

    @PostMapping
    public String registrarCuentaDeUsuario(
            @ModelAttribute("usuario") UsuarioRegistroDTO registroDTO,
            RedirectAttributes redirectAttributes) {

        try {
            //  rol de cliente Solo admite ROL CLIENTE
            registroDTO.setRol("ROLE_CLIENTE");
            
            usuarioServicio.guardar(registroDTO);
            redirectAttributes.addFlashAttribute("mensaje", "¡Usuario registrado correctamente!");
            redirectAttributes.addFlashAttribute("tipo", "success");
            return "redirect:/registro";

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("mensaje", "El correo electrónico ya está registrado.");
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/registro";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Ocurrió un error inesperado al registrar el usuario.");
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/registro";
        }
    }
}
