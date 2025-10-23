package com.registro.usuarios.servicio;

import com.registro.usuarios.modelo.*;
import com.registro.usuarios.repositorio.LibroRepositorio;
import com.registro.usuarios.repositorio.VentaRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service   
public class CarritoServicio {

    private final LibroRepositorio libroRepositorio;
    private final VentaRepositorio ventaRepositorio;

    // Carrito en memoria (idealmente se debe manejar por sesión de usuario)
    private final Map<Long, CarritoItem> carrito = new HashMap<>();

    public CarritoServicio(LibroRepositorio libroRepositorio, VentaRepositorio ventaRepositorio) {
        this.libroRepositorio = libroRepositorio;
        this.ventaRepositorio = ventaRepositorio;
    }
    // Operaciones básicas del carrito

    public void agregarLibro(Long libroId, int cantidad) {
        Libro libro = libroRepositorio.findById(libroId)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado con ID: " + libroId));

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0");
        }

        CarritoItem item = carrito.get(libroId);
        if (item == null) {
            carrito.put(libroId, new CarritoItem(libro, cantidad));
        } else {
            item.setCantidad(item.getCantidad() + cantidad);
        }
    }

    public void agregarLibro(Libro libro) {
        agregarLibro(libro.getId(), 1);
    }

    public void actualizarCantidad(Long libroId, int cantidad) {
        CarritoItem item = carrito.get(libroId);
        if (item != null) {
            if (cantidad > 0) {
                item.setCantidad(cantidad);
            } else {
                carrito.remove(libroId);
            }
        }
    }

    public void eliminarLibro(Long libroId) {
        carrito.remove(libroId);
    }

    public void vaciar() {
        carrito.clear();
    }

    public List<CarritoItem> obtenerItems() {
        return new ArrayList<>(carrito.values());
    }
    // Cálculos

    public BigDecimal calcularTotal() {
        return carrito.values().stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal obtenerSubtotalLibro(Long libroId) {
        CarritoItem item = carrito.get(libroId);
        return (item != null) ? item.getSubtotal() : BigDecimal.ZERO;
    }

    public int obtenerCantidadTotalLibros() {
        return carrito.values().stream()
                .mapToInt(CarritoItem::getCantidad)
                .sum();
    }

    public boolean estaVacio() {
        return carrito.isEmpty();
    }
    // Conversión a detalles de venta

    public List<DetalleVenta> obtenerDetalles() {
        List<DetalleVenta> detalles = new ArrayList<>();
        for (CarritoItem item : carrito.values()) {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setLibro(item.getLibro());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getLibro().getPrecioFinal());
            detalles.add(detalle);
        }
        return detalles;
    } 
    // Confirmación directa de compra

    @Transactional
    public Venta confirmarCompra(Usuario usuario, String metodoPago) {
        if (usuario == null) {
            throw new RuntimeException("Debe iniciar sesión para confirmar la compra");
        }
        if (carrito.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setFecha(LocalDateTime.now());
        venta.setMetodoPago(metodoPago);
        venta.setTotal(calcularTotal());

        List<DetalleVenta> detalles = new ArrayList<>();

        for (CarritoItem item : carrito.values()) {
            Libro libro = item.getLibro();

            if (libro.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el libro: " + libro.getTitulo());
            }

            libro.setStock(libro.getStock() - item.getCantidad());
            libroRepositorio.save(libro);

            DetalleVenta detalle = new DetalleVenta(venta, libro, item.getCantidad(), libro.getPrecioFinal());
            detalles.add(detalle);
        }

        venta.setDetalles(detalles);
        ventaRepositorio.save(venta);

        carrito.clear();

        return venta;
    }
}
