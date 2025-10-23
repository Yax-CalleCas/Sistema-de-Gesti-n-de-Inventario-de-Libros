package com.registro.usuarios.repositorio;

import com.registro.usuarios.modelo.Editorial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EditorialRepositorio extends JpaRepository<Editorial, Long> {
    Editorial findByNombre(String nombre);
}
