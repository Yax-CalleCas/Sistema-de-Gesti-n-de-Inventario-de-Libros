package com.registro.usuarios.repositorio;

import com.registro.usuarios.modelo.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepositorio extends JpaRepository<Categoria, Long> {
    Categoria findByNombre(String nombre);
}
