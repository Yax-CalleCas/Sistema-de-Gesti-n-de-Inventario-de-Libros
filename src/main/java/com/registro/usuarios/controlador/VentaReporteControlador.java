package com.registro.usuarios.controlador;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
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
import com.registro.usuarios.modelo.DetalleVenta;
import com.registro.usuarios.modelo.Venta;
import com.registro.usuarios.servicio.VentaServicio;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/admin/reportes/ventas")
public class VentaReporteControlador {

    private final VentaServicio ventaServicio;

    public VentaReporteControlador(VentaServicio ventaServicio) {
        this.ventaServicio = ventaServicio;
    }

    @GetMapping("/vista")
    public String mostrarReporteDeVentas(Model model) {
        List<Venta> ventas = ventaServicio.listarVentas();

        // CÁLCULO DEL TOTAL GENERAL
        BigDecimal totalGeneral = ventas.stream()
                .map(Venta::getTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("ventas", ventas);
        model.addAttribute("totalGeneral", totalGeneral); // Incluye el total general

        return "admin/ventas/ventas";
    }

    @GetMapping("/pdf")
    public void exportarPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_ventas.pdf");

        List<Venta> ventas = ventaServicio.listarVentas();

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document documento = new Document(pdf, PageSize.A4);
        documento.setMargins(36, 36, 80, 36);

        // Estilos
        com.itextpdf.kernel.font.PdfFontFactory.registerSystemDirectories();
        com.itextpdf.kernel.font.PdfFont tituloFont = com.itextpdf.kernel.font.PdfFontFactory.createRegisteredFont("Helvetica-Bold");
        com.itextpdf.kernel.font.PdfFont subtituloFont = com.itextpdf.kernel.font.PdfFontFactory.createRegisteredFont("Helvetica");
        com.itextpdf.kernel.font.PdfFont headerFont = com.itextpdf.kernel.font.PdfFontFactory.createRegisteredFont("Helvetica-Bold");
        com.itextpdf.kernel.font.PdfFont cellFont = com.itextpdf.kernel.font.PdfFontFactory.createRegisteredFont("Helvetica");
        DecimalFormat df = new DecimalFormat("#0.00");

        // Encabezado con logo
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

        Cell titleCell = new Cell().add(new Paragraph("Reporte General de Ventas").setFont(tituloFont).setFontSize(18).setFontColor(ColorConstants.BLUE));
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

        BigDecimal totalGeneral = BigDecimal.ZERO;

        for (Venta venta : ventas) {
            // Info principal de la venta
            Table infoVenta = new Table(1);
            infoVenta.setWidth(UnitValue.createPercentValue(100));
            String usuarioNombre = venta.getUsuario() != null ? venta.getUsuario().getNombre() + " " + venta.getUsuario().getApellido() : "N/A";
            Cell infoCell = new Cell().add(new Paragraph(
                    "Venta N°: " + venta.getId() +
                    " | Cliente: " + usuarioNombre +
                    " | Fecha: " + (venta.getFecha() != null ? venta.getFecha() : "N/A") +
                    " | Método de Pago: " + (venta.getMetodoPago() != null ? venta.getMetodoPago() : "N/A")
            ).setFont(subtituloFont).setFontSize(11));
            infoCell.setBackgroundColor(new DeviceRgb(230, 230, 250));
            infoCell.setPadding(6f);
            infoVenta.addCell(infoCell);
            documento.add(infoVenta);
            documento.add(new Paragraph(" "));

            // Tabla de detalle
            Table tabla = new Table(UnitValue.createPercentArray(new float[]{4, 1, 2, 2, 2}));
            tabla.setWidth(UnitValue.createPercentValue(100));

            String[] headers = {"Libro", "Cantidad", "Precio Unitario (S/.)", "Subtotal (S/.)", "Descuento (%)"};
            for (String h : headers) {
                Cell cell = new Cell().add(new Paragraph(h).setFont(headerFont).setFontSize(11).setFontColor(ColorConstants.WHITE));
                cell.setBackgroundColor(new DeviceRgb(13, 110, 253));
                cell.setTextAlignment(TextAlignment.CENTER);
                cell.setPadding(6f);
                tabla.addCell(cell);
            }

            for (DetalleVenta d : venta.getDetalles()) {
                tabla.addCell(new Cell().add(new Paragraph(d.getLibro() != null ? d.getLibro().getTitulo() : "N/A").setFont(cellFont).setFontSize(10)));
                tabla.addCell(new Cell().add(new Paragraph(String.valueOf(d.getCantidad())).setFont(cellFont).setFontSize(10)));
                tabla.addCell(new Cell().add(new Paragraph(df.format(d.getPrecioUnitario())).setFont(cellFont).setFontSize(10)));
                tabla.addCell(new Cell().add(new Paragraph(df.format(d.getSubtotal())).setFont(cellFont).setFontSize(10)));
                BigDecimal descuento = d.getLibro() != null && d.getLibro().getDescuento() != null ? d.getLibro().getDescuento().multiply(new BigDecimal(100)) : BigDecimal.ZERO;
                tabla.addCell(new Cell().add(new Paragraph(df.format(descuento)).setFont(cellFont).setFontSize(10)));
            }

            documento.add(tabla);

            // Total por venta
            Paragraph totalVenta = new Paragraph("Total Venta: S/. " + df.format(venta.getTotal())).setFont(subtituloFont).setFontSize(11);
            totalVenta.setTextAlignment(TextAlignment.RIGHT);
            documento.add(totalVenta);
            documento.add(new Paragraph(" "));

            if (venta.getTotal() != null) {
                totalGeneral = totalGeneral.add(venta.getTotal());
            }
        }

        // Total general
        documento.add(new com.itextpdf.layout.element.LineSeparator(new com.itextpdf.kernel.pdf.canvas.draw.SolidLine()));
        Paragraph totalGral = new Paragraph("TOTAL GENERAL DE VENTAS: S/. " + df.format(totalGeneral)).setFont(tituloFont).setFontSize(18).setFontColor(ColorConstants.BLUE);
        totalGral.setTextAlignment(TextAlignment.RIGHT);
        documento.add(totalGral);
        documento.add(new Paragraph(" "));

        // Pie del reporte
        documento.add(new com.itextpdf.layout.element.LineSeparator(new com.itextpdf.kernel.pdf.canvas.draw.SolidLine()));
        Paragraph footer = new Paragraph("Reporte generado automáticamente por el sistema BookFlow © 2025")
                .setFont(subtituloFont)
                .setFontSize(9)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER);
        documento.add(footer);

        documento.close();
    }

    @GetMapping("/excel")
    public void exportarExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_ventas.xlsx");

        List<Venta> ventas = ventaServicio.listarVentas();

        try (org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Ventas");

            // Estilos
            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

            org.apache.poi.ss.usermodel.CellStyle dataStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font dataFont = workbook.createFont();
            dataFont.setFontHeightInPoints((short) 11);
            dataStyle.setFont(dataFont);

            int rowCount = 0;

            // Encabezado del archivo
            org.apache.poi.ss.usermodel.Row tituloRow = sheet.createRow(rowCount++);
            org.apache.poi.ss.usermodel.Cell tituloCell = tituloRow.createCell(0);
            tituloCell.setCellValue("Reporte General de Ventas - BookFlow");
            tituloCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 6));

            rowCount++; // espacio

            // Cabecera de la tabla
            String[] headers = {"N° Venta", "Cliente", "Fecha", "Método de Pago", "Libro", "Cantidad", "Subtotal (S/.)"};
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(rowCount++);
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos de ventas
            BigDecimal totalGeneral = BigDecimal.ZERO;
            for (Venta venta : ventas) {
                for (DetalleVenta detalle : venta.getDetalles()) {
                    org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowCount++);
                    row.createCell(0).setCellValue(venta.getId());
                    String usuarioNombre = venta.getUsuario() != null ? venta.getUsuario().getNombre() + " " + venta.getUsuario().getApellido() : "N/A";
                    row.createCell(1).setCellValue(usuarioNombre);
                    row.createCell(2).setCellValue(venta.getFecha() != null ? String.valueOf(venta.getFecha()) : "N/A");
                    row.createCell(3).setCellValue(venta.getMetodoPago() != null ? venta.getMetodoPago() : "N/A");
                    row.createCell(4).setCellValue(detalle.getLibro() != null ? detalle.getLibro().getTitulo() : "N/A");
                    row.createCell(5).setCellValue(detalle.getCantidad());
                    row.createCell(6).setCellValue(detalle.getSubtotal() != null ? detalle.getSubtotal().doubleValue() : 0.0);

                    if (detalle.getSubtotal() != null) {
                        totalGeneral = totalGeneral.add(detalle.getSubtotal());
                    }
                }
            }

            // Total General
            rowCount++;
            org.apache.poi.ss.usermodel.Row totalRow = sheet.createRow(rowCount++);
            totalRow.createCell(5).setCellValue("TOTAL GENERAL:");
            totalRow.createCell(6).setCellValue(totalGeneral.doubleValue());

            // Ajustar anchos de columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Escribir el archivo
            workbook.write(response.getOutputStream());
        }
    }
}