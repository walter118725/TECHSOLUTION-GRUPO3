package com.techsolutions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * DTO para solicitud de generación de reportes
 */
@Schema(description = "Solicitud para generar un reporte financiero")
public class ReporteRequestDTO {
    
    @Schema(description = "Nombre de usuario", example = "gerente", requiredMode = RequiredMode.REQUIRED)
    private String username;
    
    @Schema(description = "Estado del usuario (activo/inactivo)", example = "true")
    private boolean activo = true;
    
    @Schema(description = "Roles del usuario", example = "[\"GERENTE\"]", requiredMode = RequiredMode.REQUIRED)
    private String[] roles;
    
    @Schema(description = "Fecha de inicio del reporte (formato: yyyy-MM-dd)", example = "2024-01-01")
    private String fechaInicio;
    
    @Schema(description = "Fecha de fin del reporte (formato: yyyy-MM-dd)", example = "2024-12-31")
    private String fechaFin;
    
    @Schema(description = "Mes para el reporte (1-12)", example = "11")
    private int mes;
    
    @Schema(description = "Año para el reporte", example = "2024")
    private int anio;
    
    @Schema(description = "Período del reporte", example = "Último trimestre")
    private String periodo;
    
    @Schema(description = "Nombre del archivo PDF a generar", example = "reporte_ventas_noviembre")
    private String nombreArchivo;
    
    // Constructores
    public ReporteRequestDTO() {}
    
    // Getters y Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    
    public String[] getRoles() { return roles; }
    public void setRoles(String[] roles) { this.roles = roles; }
    
    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }
    
    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }
    
    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }
    
    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }
    
    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }
    
    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }
}
