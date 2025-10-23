package com.registro.usuarios.repositorio;

import com.registro.usuarios.modelo.Venta;



import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepositorio extends JpaRepository<Venta, Long> {

	long countByUsuario_Nombre(String username);
}

