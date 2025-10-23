package com.registro.usuarios.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicoControlador {

    @GetMapping("/login")
    public String vistaLogin() {
        return "login"; 
    }

   
}