package com.registro.usuarios.controlador;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;  
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.registro.usuarios.modelo.Usuario;
import com.registro.usuarios.servicio.UsuarioServicio;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class ReporteUsuarioControlador {

    private final UsuarioServicio usuarioServicio;

    public ReporteUsuarioControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping("/admin/reportes/usuarios")
    public String mostrarReporteUsuarios(Model model, Principal principal) {
        // Añadir el usuario logueado para fragments/header
        Usuario usuarioLogueado = usuarioServicio.buscarPorEmail(principal.getName());
        model.addAttribute("usuario", usuarioLogueado);

        // Cargar todos los usuarios para el reporte
        List<Usuario> listaUsuarios = usuarioServicio.listarTodos();
        model.addAttribute("usuarios", listaUsuarios);

        // Retorna el nombre de la plantilla HTML
        return "reporteUsuarios";
    }

    // ===== EXPORTACIÓN A PDF =====
    @GetMapping("/reporte-usuarios/pdf")
    public void exportarPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_usuarios.pdf");

        List<Usuario> usuarios = usuarioServicio.listarTodos();
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document documento = new Document(pdf, PageSize.A4);
        documento.setMargins(36, 36, 80, 36);

        // ===== ESTILOS =====
        com.itextpdf.kernel.font.PdfFontFactory.registerSystemDirectories();
        com.itextpdf.kernel.font.PdfFont tituloFont = com.itextpdf.kernel.font.PdfFontFactory.createRegisteredFont("Helvetica-Bold");
        com.itextpdf.kernel.font.PdfFont subtituloFont = com.itextpdf.kernel.font.PdfFontFactory.createRegisteredFont("Helvetica");
        com.itextpdf.kernel.font.PdfFont headerFont = com.itextpdf.kernel.font.PdfFontFactory.createRegisteredFont("Helvetica-Bold");
        com.itextpdf.kernel.font.PdfFont cellFont = com.itextpdf.kernel.font.PdfFontFactory.createRegisteredFont("Helvetica");

        // ===== ENCABEZADO =====
        Table header = new Table(new float[]{1, 4});
        header.setWidth(UnitValue.createPercentValue(100));

        try {
            Image logo = new Image(ImageDataFactory.create("https://images.vexels.com/media/users/3/229082/isolated/preview/6fabc24c3830d75486725cc6d786dfbb-logotipo-de-circulos-de-libro.png"));
            logo.scaleToFit(60, 60);
            Cell logoCell = new Cell().add(logo);
            logoCell.setBorder(null);
            header.addCell(logoCell);
        } catch (Exception e) {
            Cell alt = new Cell().add(new Paragraph("BookFlow").setFont(tituloFont).setFontSize(18).setFontColor(ColorConstants.BLUE));
            alt.setBorder(null);
            header.addCell(alt);
        }

        Cell titleCell = new Cell().add(new Paragraph("Reporte General de Usuarios").setFont(tituloFont).setFontSize(18).setFontColor(ColorConstants.BLUE));
        titleCell.setBorder(null);
        titleCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        header.addCell(titleCell);
        documento.add(header);

        documento.add(new Paragraph(" "));
        documento.add(new Paragraph("Fecha de generación: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())).setFont(subtituloFont).setFontSize(11).setFontColor(ColorConstants.GRAY));
        documento.add(new Paragraph("Generado por: Sistema BookFlow").setFont(subtituloFont).setFontSize(11).setFontColor(ColorConstants.GRAY));
        documento.add(new Paragraph(" "));
        documento.add(new com.itextpdf.layout.element.LineSeparator(new com.itextpdf.kernel.pdf.canvas.draw.SolidLine()));
        documento.add(new Paragraph(" "));

        // ===== TABLA PRINCIPAL =====
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{1f, 2f, 3f, 3f, 2f}));
        tabla.setWidth(UnitValue.createPercentValue(100));

        String[] columnas = {"ID", "Imagen", "Nombre Completo", "Email", "Rol"};
        for (String columna : columnas) {
            Cell celda = new Cell().add(new Paragraph(columna).setFont(headerFont).setFontSize(11).setFontColor(ColorConstants.WHITE));
            celda.setBackgroundColor(new com.itextpdf.kernel.colors.DeviceRgb(13, 110, 253)); // Azul Bootstrap
            celda.setTextAlignment(TextAlignment.CENTER);
            celda.setPadding(6f);
            tabla.addCell(celda);
        }

        for (Usuario usuario : usuarios) {
            tabla.addCell(new Cell().add(new Paragraph(String.valueOf(usuario.getId())).setFont(cellFont).setFontSize(10)));

            // Imagen o texto alternativo
            if (usuario.getImagenUrl() != null && !usuario.getImagenUrl().isEmpty()) {
                try {
                    Image imagen = new Image(ImageDataFactory.create(usuario.getImagenUrl()));
                    imagen.scaleAbsolute(40, 50);
                    Cell imagenCelda = new Cell().add(imagen);
                    imagenCelda.setTextAlignment(TextAlignment.CENTER);
                    tabla.addCell(imagenCelda);
                } catch (Exception e) {
                    tabla.addCell(new Cell().add(new Paragraph("Sin imagen").setFont(cellFont).setFontSize(10)));
                }
            } else {
                tabla.addCell(new Cell().add(new Paragraph("Sin imagen").setFont(cellFont).setFontSize(10)));
            }

            tabla.addCell(new Cell().add(new Paragraph(usuario.getNombre() + " " + usuario.getApellido()).setFont(cellFont).setFontSize(10)));
            tabla.addCell(new Cell().add(new Paragraph(usuario.getEmail()).setFont(cellFont).setFontSize(10)));
            tabla.addCell(new Cell().add(new Paragraph(
                    usuario.getRol() != null ? usuario.getRol().getNombre() : "N/A").setFont(cellFont).setFontSize(10)));
        }

        documento.add(tabla);
        documento.add(new Paragraph(" "));
        documento.add(new com.itextpdf.layout.element.LineSeparator(new com.itextpdf.kernel.pdf.canvas.draw.SolidLine()));
        documento.add(new Paragraph(" "));

        // ===== PIE DE PÁGINA =====
        Paragraph footer = new Paragraph("Reporte generado automáticamente por el sistema BookFlow © 2025")
                .setFont(subtituloFont)
                .setFontSize(9)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER);
        documento.add(footer);

        documento.close();
    }
}