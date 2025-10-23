package com.registro.usuarios.servicio;

import com.registro.usuarios.modelo.Libro;
import com.registro.usuarios.repositorio.LibroRepositorio;
import org.springframework.stereotype.Service;
import java.util.List;

import java.math.BigDecimal; 

import org.springframework.transaction.annotation.Transactional; 

@Service
public class LibroServicioImpl implements LibroServicio {

    // Constante para el umbral de stock bajo, fijado en 10.
    private static final int STOCK_UMBRAL = 10;
    
    private final LibroRepositorio libroRepositorio;

    // Inyección de dependencias por constructor
    public LibroServicioImpl(LibroRepositorio libroRepositorio) {
        this.libroRepositorio = libroRepositorio;
    }

    // Métodos CRUD básicos
    @Override
    public List<Libro> listarTodos() {
        return libroRepositorio.findAll();
    }

    @Override
    @Transactional
    public Libro guardar(Libro libro) {
        return libroRepositorio.save(libro);
    }

    @Override
    public Libro buscarPorId(Long id) {
        // Usar .orElse(null) para manejar el Optional
        return libroRepositorio.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        libroRepositorio.deleteById(id);
    }

    // Métodos adicionales para catálogo 
    @Override
    public List<Libro> buscarPorCategoria(Long categoriaId) {
        return libroRepositorio.findByCategoria_Id(categoriaId);
    }

    @Override
    public List<Libro> buscarPorAutor(Long autorId) {
        return libroRepositorio.findByAutor_Id(autorId);
    }

    @Override
    public List<Libro> buscarPorEditorial(Long editorialId) {
        return libroRepositorio.findByEditorial_Id(editorialId);
    }

    @Override
    public List<Libro> buscarConDescuento() {
        // Usa el método del repositorio y pasa el valor BigDecimal.ZERO
        return libroRepositorio.findByDescuentoNotNullAndDescuentoGreaterThanOrderByDescuentoDesc(BigDecimal.ZERO);
    }

    @Override
    public List<Libro> buscarPorTitulo(String titulo) {
        return libroRepositorio.findByTituloContainingIgnoreCase(titulo);
    }
    
    @Override
    public Libro buscarPorIsbn(String isbn) {
        return libroRepositorio.findByIsbn(isbn);
    }

    // Métodos para Dashboard/Inventario
    
    @Override
    public long contarLibros() {
        return libroRepositorio.count();
    }
    
    @Override
    public List<Libro> listarLibrosConStockBajo() {
        // Utiliza el método del repositorio con el umbral fijo
        return libroRepositorio.findByStockLessThan(STOCK_UMBRAL);
    }
}