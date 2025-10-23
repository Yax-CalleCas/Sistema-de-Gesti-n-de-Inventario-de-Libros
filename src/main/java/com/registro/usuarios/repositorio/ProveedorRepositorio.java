package com.registro.usuarios.repositorio;

import com.registro.usuarios.modelo.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepositorio extends JpaRepository<Proveedor, Long> {
    Proveedor findByNombre(String nombre);
}
