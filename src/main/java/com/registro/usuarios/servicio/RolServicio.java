package com.registro.usuarios.servicio;

import java.util.List;
import org.springframework.stereotype.Service;
import com.registro.usuarios.modelo.Rol;
import com.registro.usuarios.repositorio.RolRepositorio;

@Service
public class RolServicio { // Clase renombrada y ya NO implementa ninguna interfaz

    // Práctica recomendada: Inyección de dependencias por constructor (final)
    private final RolRepositorio rolRepositorio;

    public RolServicio(RolRepositorio rolRepositorio) {
        this.rolRepositorio = rolRepositorio;
    }

    public Rol guardar(Rol rol) { 
        return rolRepositorio.save(rol); 
    }

    public Rol actualizar(Rol rol) { 
        return rolRepositorio.save(rol); 
    }

    public void eliminar(Long id) { 
        rolRepositorio.deleteById(id); 
    }

    public List<Rol> listarTodos() { 
        return rolRepositorio.findAll(); 
    }

    public Rol buscarPorId(Long id) { 
        return rolRepositorio.findById(id).orElse(null); 
    }

    public long contarRoles() {
        return rolRepositorio.count();
    }
}