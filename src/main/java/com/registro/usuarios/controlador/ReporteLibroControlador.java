package com.registro.usuarios.controlador;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.registro.usuarios.modelo.Libro;
//  IMPORTACIÓN CORREGIDA/VERIFICADA:
import com.registro.usuarios.servicio.LibroServicio; 

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class ReporteLibroControlador {

    //  Uso de la interfaz LibroServicio para inyección
    private final LibroServicio libroServicio;

    public ReporteLibroControlador(LibroServicio libroServicio) {
        this.libroServicio = libroServicio;
    }

    // Página principal del reporte
    @GetMapping("/admin/reportes/libros")
    public String reporteLibros(Model model) {
        model.addAttribute("libros", libroServicio.listarTodos());
        return "reporte_libros";
    }
    
    // Generar reporte PDF con diseño elegante
    @GetMapping("/admin/reportes/libros/pdf")
    public void generarPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_libros.pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(40, 40, 40, 40);

        // Colores
        DeviceRgb azul = new DeviceRgb(13, 110, 253);
        DeviceRgb grisFondo = new DeviceRgb(245, 247, 250);
        DeviceRgb grisTexto = new DeviceRgb(90, 90, 90);
        DeviceRgb blanco = new DeviceRgb(255, 255, 255);

        // === Encabezado con logo y título ===
        Table header = new Table(new float[]{1, 4});
        header.setWidth(UnitValue.createPercentValue(100));

        try {
           
            // Es mejor alojarla localmente o usar un servicio de confianza.
            ImageData logoData = ImageDataFactory.create("https://images.vexels.com/media/users/3/229082/isolated/preview/6fabc24c3830d75486725cc6d786dfbb-logotipo-de-circulos-de-libro.png");
            Image logo = new Image(logoData).scaleToFit(60, 60);
            header.addCell(new Cell().add(logo).setBorder(Border.NO_BORDER));
        } catch (Exception e) {
            header.addCell(new Cell().add(new Paragraph("BookFlow").setBold().setFontSize(14)).setBorder(Border.NO_BORDER));
        }

        header.addCell(new Cell()
                .add(new Paragraph("Reporte General de Libros")
                        .setBold()
                        .setFontSize(18)
                        .setFontColor(azul)
                        .setTextAlignment(TextAlignment.LEFT))
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(Border.NO_BORDER));
        document.add(header);

        document.add(new Paragraph("\nFecha de generación: " + new java.util.Date())
                .setFontSize(10)
                .setFontColor(grisTexto));
        document.add(new Paragraph("Generado por: Sistema BookFlow")
                .setFontSize(10)
                .setFontColor(grisTexto));
        document.add(new Paragraph("\n"));

        // === Tabla de datos ==
        float[] columnas = {30, 100, 80, 70, 70, 50, 50, 40, 70};
        Table tabla = new Table(columnas);
        tabla.setWidth(UnitValue.createPercentValue(100));

        // 🎯 Encabezados actualizados
        String[] encabezados = {"ID", "Título", "Autor", "Categoría", "Editorial", "Precio Orig. (S/.)", "Desc.", "Stock", "Imagen"};

        // Encabezado
        for (String e : encabezados) {
            tabla.addHeaderCell(new Cell()
                    .add(new Paragraph(e)
                            .setBold()
                            .setFontColor(blanco)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBackgroundColor(azul)
                    .setPadding(4) // Ajuste de padding
                    .setBorder(Border.NO_BORDER));
        }

        // Filas
        List<Libro> libros = libroServicio.listarTodos();
        boolean colorAlterno = false;

        for (Libro libro : libros) {
            DeviceRgb fondoFila = colorAlterno ? grisFondo : blanco;
            colorAlterno = !colorAlterno;
            
            // Calculamos el descuento en porcentaje para mostrar
            String descuentoPct = libro.getDescuento() != null && libro.getDescuento().doubleValue() > 0 ? 
                                  String.format("%.0f%%", libro.getDescuento().doubleValue() * 100) : "0%";

            tabla.addCell(new Cell().add(new Paragraph(String.valueOf(libro.getId())))
                    .setBackgroundColor(fondoFila)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(4));
            tabla.addCell(new Cell().add(new Paragraph(libro.getTitulo()))
                    .setBackgroundColor(fondoFila)
                    .setPadding(4));
            tabla.addCell(new Cell().add(new Paragraph(libro.getAutor() != null ? libro.getAutor().getNombre() : "—"))
                    .setBackgroundColor(fondoFila)
                    .setPadding(4));
            tabla.addCell(new Cell().add(new Paragraph(libro.getCategoria() != null ? libro.getCategoria().getCategoria() : "—"))
                    .setBackgroundColor(fondoFila)
                    .setPadding(4));
            tabla.addCell(new Cell().add(new Paragraph(libro.getEditorial() != null ? libro.getEditorial().getNombre() : "—"))
                    .setBackgroundColor(fondoFila)
                    .setPadding(4));
            // 🎯 Precio Original
            tabla.addCell(new Cell().add(new Paragraph(String.format("%.2f", libro.getPrecio())))
                    .setBackgroundColor(fondoFila)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setPadding(4));
            // 🎯 Descuento
            tabla.addCell(new Cell().add(new Paragraph(descuentoPct))
                    .setBackgroundColor(fondoFila)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(4));
            tabla.addCell(new Cell().add(new Paragraph(String.valueOf(libro.getStock())))
                    .setBackgroundColor(fondoFila)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(4));

            try {
                if (libro.getImagenUrl() != null && !libro.getImagenUrl().isEmpty()) {
                    ImageData data = ImageDataFactory.create(libro.getImagenUrl());
                    Image img = new Image(data).scaleToFit(40, 60);
                    tabla.addCell(new Cell().add(img)
                            .setBackgroundColor(fondoFila)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setPadding(4));
                } else {
                    tabla.addCell(new Cell().add(new Paragraph("Sin imagen")
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontColor(grisTexto))
                            .setBackgroundColor(fondoFila)
                            .setPadding(4));
                }
            } catch (Exception ex) {
                tabla.addCell(new Cell().add(new Paragraph("Error").setTextAlignment(TextAlignment.CENTER))
                        .setBackgroundColor(fondoFila)
                        .setPadding(4));
            }
        }

        document.add(tabla);

        document.add(new Paragraph("\nReporte generado automáticamente por el sistema BookFlow © 2025")
                .setFontSize(9)
                .setFontColor(grisTexto)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20));
        document.close();
    }

    // Generar reporte Excel con formato
    @GetMapping("/admin/reportes/libros/excel")
    public void generarExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_libros.xlsx");

        List<Libro> libros = libroServicio.listarTodos();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Libros");

            // Estilo del encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Estilo de datos de moneda
            CellStyle currencyStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            currencyStyle.setDataFormat(format.getFormat("S/ #,##0.00"));
            currencyStyle.setAlignment(HorizontalAlignment.RIGHT);
            
            // Estilo de datos de porcentaje
            CellStyle percentStyle = workbook.createCellStyle();
            percentStyle.setDataFormat(format.getFormat("0%"));
            percentStyle.setAlignment(HorizontalAlignment.CENTER);


            // Título
            Row tituloRow = sheet.createRow(0);
            org.apache.poi.ss.usermodel.Cell tituloCell = tituloRow.createCell(0);
            tituloCell.setCellValue("Reporte General de Libros - BookFlow");
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 7)); // Ajuste de rango

            // Cabecera
            // 🎯 Cabeceras actualizadas
            String[] columnas = {"ID", "Título", "Autor", "Categoría", "Editorial", "Precio Orig. (S/.)", "Desc.", "Stock"};
            Row headerRow = sheet.createRow(2);
            for (int i = 0; i < columnas.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            int rowIdx = 3;
            for (Libro libro : libros) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(libro.getId());
                row.createCell(1).setCellValue(libro.getTitulo());
                row.createCell(2).setCellValue(libro.getAutor() != null ? libro.getAutor().getNombre() : "—");
                row.createCell(3).setCellValue(libro.getCategoria() != null ? libro.getCategoria().getNombre() : "—");
                row.createCell(4).setCellValue(libro.getEditorial() != null ? libro.getEditorial().getNombre() : "—");
                
                // 🎯 Precio Original
                org.apache.poi.ss.usermodel.Cell precioCell = row.createCell(5);
                precioCell.setCellValue(libro.getPrecio().doubleValue());
                precioCell.setCellStyle(currencyStyle);
                
                // 🎯 Descuento
                org.apache.poi.ss.usermodel.Cell descuentoCell = row.createCell(6);
                double descuento = libro.getDescuento() != null ? libro.getDescuento().doubleValue() : 0.0;
                descuentoCell.setCellValue(descuento);
                descuentoCell.setCellStyle(percentStyle);
                
                // 🎯 Stock
                row.createCell(7).setCellValue(libro.getStock());
            }

            // Ajustar ancho de columnas
            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }
}