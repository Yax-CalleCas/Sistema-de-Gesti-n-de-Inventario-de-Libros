package com.registro.usuarios.repositorio;

import com.registro.usuarios.modelo.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface LibroRepositorio extends JpaRepository<Libro, Long> {

    // Búsquedas de catálogo por ID de entidades relacionadas
    List<Libro> findByCategoria_Id(Long categoriaId);
    List<Libro> findByAutor_Id(Long autorId);
    List<Libro> findByEditorial_Id(Long editorialId);

    // Búsqueda para alertas de inventario (stock bajo)
    List<Libro> findByStockLessThan(int stock);

    // Buscar libros en oferta: descuento no nulo, mayor que cero, ordenado descendentemente
    List<Libro> findByDescuentoNotNullAndDescuentoGreaterThanOrderByDescuentoDesc(BigDecimal descuento);

    // Buscar por título parcial, ignorando mayúsculas/minúsculas
    List<Libro> findByTituloContainingIgnoreCase(String titulo);

    // Buscar por ISBN único
    Libro findByIsbn(String isbn);
    
    // NOTA: Métodos como findAll(), save(), findById(), count() ya están heredados de JpaRepository.
}