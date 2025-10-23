package com.registro.usuarios.servicio;

import com.registro.usuarios.modelo.Autor;
import com.registro.usuarios.repositorio.AutorRepositorio;
import org.springframework.stereotype.Service;
import java.util.List;

// 1. ELIMINAMOS 'Impl' del nombre de la clase
@Service
public class AutorServicio { // ANTES: AutorServicioImpl

    private final AutorRepositorio autorRepositorio;

    public AutorServicio(AutorRepositorio autorRepositorio) {
        this.autorRepositorio = autorRepositorio;
    }

    // 2. ELIMINAMOS @Override
    public List<Autor> listarTodos() {
        return autorRepositorio.findAll();
    }

    // ELIMINAMOS @Override
    public Autor guardar(Autor autor) {
        return autorRepositorio.save(autor);
    }

    // ELIMINAMOS @Override
    public Autor buscarPorId(Long id) {
        return autorRepositorio.findById(id).orElse(null);
    }

    // ELIMINAMOS @Override
    public void eliminar(Long id) {
        autorRepositorio.deleteById(id);
    }
    
    // Si necesitas el método findByNombre:
    public Autor buscarPorNombre(String nombre) {
        return autorRepositorio.findByNombre(nombre);
    }
}