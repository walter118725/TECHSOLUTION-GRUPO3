package com.techsolutions.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO genérico para respuestas de la API
 */
@Schema(description = "Respuesta genérica de la API")
public class ApiResponseDTO<T> {
    
    @Schema(description = "Indica si la operación fue exitosa", example = "true")
    private Boolean exitoso;
    
    @Schema(description = "Mensaje descriptivo", example = "Operación completada exitosamente")
    private String mensaje;
    
    @Schema(description = "Datos de la respuesta")
    private T datos;
    
    @Schema(description = "Código de error (si aplica)", example = "ERR_001")
    private String codigoError;
    
    // Constructores
    public ApiResponseDTO() {}
    
    public ApiResponseDTO(Boolean exitoso, String mensaje) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
    }
    
    public ApiResponseDTO(Boolean exitoso, String mensaje, T datos) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
        this.datos = datos;
    }
    
    // Factory methods
    public static <T> ApiResponseDTO<T> success(String mensaje, T datos) {
        return new ApiResponseDTO<>(true, mensaje, datos);
    }
    
    public static <T> ApiResponseDTO<T> success(String mensaje) {
        return new ApiResponseDTO<>(true, mensaje);
    }
    
    public static <T> ApiResponseDTO<T> error(String mensaje) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>(false, mensaje);
        return response;
    }
    
    public static <T> ApiResponseDTO<T> error(String mensaje, String codigoError) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>(false, mensaje);
        response.setCodigoError(codigoError);
        return response;
    }
    
    // Getters y Setters
    public Boolean getExitoso() { return exitoso; }
    public void setExitoso(Boolean exitoso) { this.exitoso = exitoso; }
    
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    
    public T getDatos() { return datos; }
    public void setDatos(T datos) { this.datos = datos; }
    
    public String getCodigoError() { return codigoError; }
    public void setCodigoError(String codigoError) { this.codigoError = codigoError; }
}
