package com.registro.usuarios.controlador.dto;

public class UsuarioRegistroDTO {
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String rol; // Nombre del rol (e.g., "ROLE_ADMIN")
    private String imagenUrl;

    // Constructor vacío
    public UsuarioRegistroDTO() {}

    // Constructor completo
    public UsuarioRegistroDTO(String nombre, String apellido, String email, String password, String rol, String imagenUrl) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.imagenUrl = imagenUrl;
    }

    // Getters y setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
}