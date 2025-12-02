package com.techsolutions.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para información de pasarela de pago
 */
@Schema(description = "Información de una pasarela de pago")
public class PasarelaDTO {
    
    @Schema(description = "Identificador de la pasarela", example = "yape")
    private String id;
    
    @Schema(description = "Nombre de la pasarela", example = "Yape")
    private String nombre;
    
    @Schema(description = "Indica si la pasarela está habilitada", example = "true")
    private Boolean habilitada;
    
    @Schema(description = "Descripción de la pasarela", example = "Pagos mediante billetera móvil Yape")
    private String descripcion;
    
    @Schema(description = "Icono o logo de la pasarela", example = "/icons/yape.png")
    private String icono;
    
    // Constructores
    public PasarelaDTO() {}
    
    public PasarelaDTO(String id, String nombre, Boolean habilitada) {
        this.id = id;
        this.nombre = nombre;
        this.habilitada = habilitada;
    }
    
    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public Boolean getHabilitada() { return habilitada; }
    public void setHabilitada(Boolean habilitada) { this.habilitada = habilitada; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }
}
