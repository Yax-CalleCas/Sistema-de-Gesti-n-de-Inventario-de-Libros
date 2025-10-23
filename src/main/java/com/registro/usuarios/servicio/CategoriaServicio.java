package com.registro.usuarios.servicio;

import com.registro.usuarios.modelo.Categoria;
import com.registro.usuarios.repositorio.CategoriaRepositorio;

import org.springframework.stereotype.Service;
import java.util.List;

// ¡Clase renombrada y sin implementar la interfaz!
@Service
public class CategoriaServicio { 

    private final CategoriaRepositorio categoriaRepositorio;

    public CategoriaServicio(CategoriaRepositorio categoriaRepositorio) {
        this.categoriaRepositorio = categoriaRepositorio;
    }

    // Se eliminan los @Override
    public List<Categoria> listarTodos() {
        return categoriaRepositorio.findAll();
    }

    public Categoria guardar(Categoria categoria) {
        return categoriaRepositorio.save(categoria);
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepositorio.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        categoriaRepositorio.deleteById(id);
    }
    
    // Método extra del repositorio
    public Categoria buscarPorNombre(String nombre) {
        return categoriaRepositorio.findByNombre(nombre);
    }
}