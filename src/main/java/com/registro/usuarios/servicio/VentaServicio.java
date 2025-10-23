package com.registro.usuarios.servicio;

import com.registro.usuarios.modelo.Venta;
import com.registro.usuarios.modelo.Usuario;
import com.registro.usuarios.modelo.DetalleVenta;
import com.registro.usuarios.repositorio.VentaRepositorio;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VentaServicio {

    private final VentaRepositorio ventaRepositorio;

    public VentaServicio(VentaRepositorio ventaRepositorio) {
        this.ventaRepositorio = ventaRepositorio;
    }

    //  Guardar una venta normal
    public Venta guardarVenta(Venta venta) {
        return ventaRepositorio.save(venta);
    }

    //  Listar todas las ventas
    public List<Venta> listarVentas() {
        return ventaRepositorio.findAll();
    }

    //  Buscar una venta por ID
    public Venta buscarPorId(Long id) {
        return ventaRepositorio.findById(id).orElse(null);
    }

    //  Sumar el total de todas las ventas
    public BigDecimal calcularTotalVentas() {
        List<Venta> ventas = ventaRepositorio.findAll();
        return ventas.stream()
                .map(Venta::getTotal)
                .filter(total -> total != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Contar ventas totales
    public long contarVentas() {
        return ventaRepositorio.count();
    }

    //  Nuevo: crear venta desde carrito
    public Venta registrarVenta(Usuario usuario, List<DetalleVenta> detalles, String metodoPago) {
        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setFecha(LocalDateTime.now());
        venta.setMetodoPago(metodoPago);
        venta.setDetalles(detalles);

        // Asociar detalles a la venta
        detalles.forEach(d -> d.setVenta(venta));

        // Calcular total
        BigDecimal total = detalles.stream()
                .map(DetalleVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        venta.setTotal(total);

        return ventaRepositorio.save(venta);
    }
}
