package com.techsolutions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * DTO para solicitudes de modificación de stock
 */
@Schema(description = "Solicitud para modificar el stock de un producto")
public class StockRequestDTO {
    
    @Schema(description = "Cantidad a modificar", example = "10", requiredMode = RequiredMode.REQUIRED, minimum = "1")
    private Integer cantidad;
    
    @Schema(description = "Nuevo stock mínimo", example = "15", minimum = "0")
    private Integer stockMinimo;
    
    // Constructores
    public StockRequestDTO() {}
    
    public StockRequestDTO(Integer cantidad) {
        this.cantidad = cantidad;
    }
    
    // Getters y Setters
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
}
