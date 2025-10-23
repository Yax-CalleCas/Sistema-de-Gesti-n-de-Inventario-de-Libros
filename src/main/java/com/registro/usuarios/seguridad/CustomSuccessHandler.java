package com.registro.usuarios.seguridad;

import java.io.IOException;
import java.util.Collection;



import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Obtiene la colección de roles (GrantedAuthority) del usuario autenticado.
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String redirectUrl = request.getContextPath(); 

        //  Itera sobre los roles para decidir la redirección.
        for (GrantedAuthority auth : authorities) {
            String role = auth.getAuthority();
            switch (role) {
               
                case "ROLE_ADMIN":
                    redirectUrl = "/admin";
                    break;
                case "ROLE_CLIENTE":
                    redirectUrl = "/cliente";
                    break;
                case "ROLE_TRABAJADOR":
                    redirectUrl = "/trabajador";
                    break;
                default:
                  
                    redirectUrl = "/";
                    break;
            }
        }

        // Ejecuta la redirección final al path determinado por el rol.
        response.sendRedirect(redirectUrl);
    }
}