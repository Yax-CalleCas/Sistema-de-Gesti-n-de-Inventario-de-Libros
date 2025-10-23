package com.registro.usuarios.servicio;

import com.registro.usuarios.modelo.Proveedor;
import com.registro.usuarios.repositorio.ProveedorRepositorio;

import org.springframework.stereotype.Service;
import java.util.List;

// Clase renombrada y sin implementar la interfaz ProveedorServicio
@Service
public class ProveedorServicio { 

    private final ProveedorRepositorio proveedorRepositorio;

    public ProveedorServicio(ProveedorRepositorio proveedorRepositorio) {
        this.proveedorRepositorio = proveedorRepositorio;
    }

    // Se eliminan los @Override
    public List<Proveedor> listarTodos() {
        return proveedorRepositorio.findAll();
    }

    public Proveedor guardar(Proveedor proveedor) {
        return proveedorRepositorio.save(proveedor);
    }

    public Proveedor buscarPorId(Long id) {
        return proveedorRepositorio.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        proveedorRepositorio.deleteById(id);
    }
}