package com.registro.usuarios.modelo;


import java.util.ArrayList; // Importamos ArrayList para inicializar la lista
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String descripcion;

    private String urlIcono;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Libro> libros;

    // =====================
    // Constructores
    // =====================
    
    // Constructor por defecto (Asegura que la lista se inicialice)
    public Categoria() {
        this.libros = new ArrayList<>();
    }

    // Constructor completo
    public Categoria(String nombre, String descripcion, String urlIcono) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.urlIcono = urlIcono;
        this.libros = new ArrayList<>(); // Inicializamos la lista
    }

    // Constructor de sobrecarga para compatibilidad
    public Categoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.libros = new ArrayList<>(); // Inicializamos la lista
    }

    // =====================
    // Getters y Setters
    // =====================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getUrlIcono() { return urlIcono; }
    public void setUrlIcono(String urlIcono) { this.urlIcono = urlIcono; }

    // ¡CORRECCIÓN! El getter debe devolver el tipo List<Libro>
    public List<Libro> getLibros() {
        return libros;
    }
    
    // ¡Asegúrate de incluir el setter para la lista!
    public void setLibros(List<Libro> libros) {
        this.libros = libros;
    }

    public String getCategoria() {
        return this.nombre;
    }
}