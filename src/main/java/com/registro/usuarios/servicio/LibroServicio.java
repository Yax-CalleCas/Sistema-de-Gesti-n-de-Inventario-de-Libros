package com.registro.usuarios.servicio;

import com.registro.usuarios.modelo.Libro;
import java.util.List;

public interface LibroServicio {
	List<Libro> listarTodos();
    Libro guardar(Libro libro);
    Libro buscarPorId(Long id);
    void eliminar(Long id);

    // Métodos adicionales para catálogo
    List<Libro> buscarPorCategoria(Long categoriaId);
    List<Libro> buscarPorAutor(Long autorId);
    List<Libro> buscarPorEditorial(Long editorialId);
    List<Libro> buscarConDescuento();
    List<Libro> buscarPorTitulo(String titulo);
    Libro buscarPorIsbn(String isbn);
    
    // Método adicional para el panel  (Stock bajo fijo en 10)
    long contarLibros();
    // ⬇️ MÉTODO CORREGIDO ⬇️
    List<Libro> listarLibrosConStockBajo();
}