package com.registro.usuarios.servicio;

import com.registro.usuarios.modelo.Editorial;
import com.registro.usuarios.repositorio.EditorialRepositorio;

import org.springframework.stereotype.Service;
import java.util.List;

// Clase final renombrada y sin implementar la interfaz
@Service
public class EditorialServicio { 

    private final EditorialRepositorio editorialRepositorio;

    public EditorialServicio(EditorialRepositorio editorialRepositorio) {
        this.editorialRepositorio = editorialRepositorio;
    }

    // Se eliminan los @Override
    public List<Editorial> listarTodos() {
        return editorialRepositorio.findAll();
    }

    public Editorial guardar(Editorial editorial) {
        return editorialRepositorio.save(editorial);
    }

    public Editorial buscarPorId(Long id) {
        return editorialRepositorio.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        editorialRepositorio.deleteById(id);
    }
    
}