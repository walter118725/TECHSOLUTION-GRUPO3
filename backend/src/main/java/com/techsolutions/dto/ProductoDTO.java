package com.techsolutions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.math.BigDecimal;

/**
 * DTO para transferencia de datos de Producto
 */
@Schema(description = "Datos de un producto del inventario")
public class ProductoDTO {
    
    @Schema(description = "ID único del producto", example = "1")
    private Long id;
    
    @Schema(description = "Código único del producto", example = "TECH-001", requiredMode = RequiredMode.REQUIRED)
    private String codigo;
    
    @Schema(description = "Nombre del producto", example = "Laptop HP ProBook 450", requiredMode = RequiredMode.REQUIRED)
    private String nombre;
    
    @Schema(description = "Descripción detallada del producto", example = "Laptop empresarial con procesador Intel Core i5")
    private String descripcion;
    
    @Schema(description = "Precio del producto en soles", example = "2599.99", requiredMode = RequiredMode.REQUIRED)
    private BigDecimal precio;
    
    @Schema(description = "Cantidad en stock", example = "25", requiredMode = RequiredMode.REQUIRED)
    private Integer stock;
    
    @Schema(description = "Stock mínimo para alertas", example = "10")
    private Integer stockMinimo;
    
    @Schema(description = "Categoría del producto", example = "Electrónicos")
    private String categoria;
    
    @Schema(description = "URL de la imagen del producto", example = "/images/laptop-hp.jpg")
    private String imagen;
    
    @Schema(description = "Estado del producto", example = "true")
    private Boolean activo;
    
    // Constructores
    public ProductoDTO() {}
    
    public ProductoDTO(Long id, String codigo, String nombre, String descripcion, 
                       BigDecimal precio, Integer stock, Integer stockMinimo, 
                       String categoria, String imagen, Boolean activo) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.categoria = categoria;
        this.imagen = imagen;
        this.activo = activo;
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
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
