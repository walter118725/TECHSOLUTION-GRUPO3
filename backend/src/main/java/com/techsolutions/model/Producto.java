package com.techsolutions.model;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad Producto - Representa los productos del inventario
 * Aplicando GRASP: Information Expert - conoce su propio stock y precio
 */
@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa un producto del inventario")
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del producto", example = "1")
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    @Schema(description = "Código único del producto", example = "TECH-001", requiredMode = RequiredMode.REQUIRED)
    private String codigo;
    
    @Column(nullable = false, length = 200)
    @Schema(description = "Nombre del producto", example = "Laptop HP ProBook 450", requiredMode = RequiredMode.REQUIRED)
    private String nombre;
    
    @Column(length = 1000)
    @Schema(description = "Descripción detallada del producto", example = "Laptop empresarial con procesador Intel Core i5")
    private String descripcion;
    
    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(description = "Precio del producto en soles", example = "2599.99", requiredMode = RequiredMode.REQUIRED)
    private BigDecimal precio;
    
    @Column(nullable = false)
    @Schema(description = "Cantidad en stock", example = "25", minimum = "0")
    private Integer stock = 0;
    
    @Column(name = "stock_minimo", nullable = false)
    @Schema(description = "Stock mínimo para alertas (Patrón Observer)", example = "10", minimum = "0")
    private Integer stockMinimo = 10;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    @Schema(description = "Categoría del producto")
    private Categoria categoria;
    
    @Column(name = "imagen_url", length = 500)
    @Schema(description = "URL de la imagen del producto", example = "/images/laptop-hp.jpg")
    private String imagenUrl;
    
    @Column(nullable = false)
    @Schema(description = "Indica si el producto está activo", example = "true")
    private Boolean activo = true;
    
    @Column(name = "fecha_creacion")
    @Schema(description = "Fecha de creación del producto")
    private LocalDateTime fechaCreacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
    
    /**
     * Verifica si el producto necesita reposición
     * GRASP: Information Expert - el producto conoce su propio stock
     * Patrón Observer: Este método será usado para notificar cuando hay stock bajo
     */
    public boolean necesitaReposicion() {
        return stock <= stockMinimo;
    }
    
    /**
     * Reduce el stock del producto
     * GRASP: Information Expert - el producto gestiona su propio stock
     */
    public void reducirStock(int cantidad) {
        if (this.stock >= cantidad) {
            this.stock -= cantidad;
        } else {
            throw new IllegalStateException("Stock insuficiente para el producto: " + nombre);
        }
    }
    
    /**
     * Aumenta el stock del producto
     */
    public void aumentarStock(int cantidad) {
        this.stock += cantidad;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
