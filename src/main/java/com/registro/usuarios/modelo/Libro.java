package com.registro.usuarios.modelo;


import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode; // Nueva importación para RoundingMode
import java.time.LocalDate;

@Entity
@Table(name = "libro")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(length = 500)
    private String descripcion;

    @Column(precision = 10, scale = 2) // Asegura precisión para el dinero
    private BigDecimal precio;
    private int stock;

    // Portada como URL
    @Column(length = 500)
    private String imagenUrl;

    // Descuento en porcentaje (ej. 0.15 = 15%). Precision para decimales.
    @Column(precision = 5, scale = 2) 
    private BigDecimal descuento;

    // ISBN único
    @Column(unique = true, length = 20)
    private String isbn;

    // Fecha de publicación
    @NotNull // Usando la anotación estándar de Bean Validation
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaPublicacion;

    // Idioma del libro
    private String idioma;

    // Número de páginas
    private int numeroPaginas;

    // =====================
    // Relaciones (ManyToOne)
    // =====================
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "autor_id")
    private Autor autor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "editorial_id")
    private Editorial editorial;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    // =====================
    // Constructores
    // =====================
    public Libro() {}

    public Libro(String titulo, String descripcion, BigDecimal precio, int stock, String imagenUrl,
                 BigDecimal descuento, String isbn, LocalDate fechaPublicacion, String idioma, int numeroPaginas,
                 Categoria categoria, Autor autor, Editorial editorial, Proveedor proveedor) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.imagenUrl = imagenUrl;
        this.descuento = descuento;
        this.isbn = isbn;
        this.fechaPublicacion = fechaPublicacion;
        this.idioma = idioma;
        this.numeroPaginas = numeroPaginas;
        this.categoria = categoria;
        this.autor = autor;
        this.editorial = editorial;
        this.proveedor = proveedor;
    }

    
    /**
     * Devuelve el precio final aplicando el descuento.
     * Si no hay descuento, retorna el precio original.
     */
    public BigDecimal getPrecioFinal() {
        if (precio == null || descuento == null || descuento.compareTo(BigDecimal.ZERO) <= 0) {
            return precio;
        }
        // Cálculo del descuento: precio - (precio * descuento)
        BigDecimal precioConDescuento = precio.multiply(BigDecimal.ONE.subtract(descuento));
        
        // Usar RoundingMode.HALF_UP (más moderno que la constante obsoleta)
        return precioConDescuento.setScale(2, RoundingMode.HALF_UP);
    }


    // =====================
    // Getters y Setters
    // (Se mantienen los que ya tenías)
    // =====================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public LocalDate getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(LocalDate fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }

    public int getNumeroPaginas() { return numeroPaginas; }
    public void setNumeroPaginas(int numeroPaginas) { this.numeroPaginas = numeroPaginas; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public Autor getAutor() { return autor; }
    public void setAutor(Autor autor) { this.autor = autor; }

    public Editorial getEditorial() { return editorial; }
    public void setEditorial(Editorial editorial) { this.editorial = editorial; }

    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }

	public Object listarTodos() {
		// TODO Auto-generated method stub
		return null;
	}
}