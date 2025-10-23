package com.registro.usuarios.repositorio;

import com.registro.usuarios.modelo.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorRepositorio extends JpaRepository<Autor, Long> {
    Autor findByNombre(String nombre);
}
