package com.techsolutions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

/**
 * DTO para respuesta de procesamiento de pago
 */
@Schema(description = "Respuesta del procesamiento de un pago")
public class PagoResponseDTO {
    
    @Schema(description = "Indica si el pago fue exitoso", example = "true")
    private Boolean exitoso;
    
    @Schema(description = "Mensaje descriptivo del resultado", example = "Pago procesado correctamente")
    private String mensaje;
    
    @Schema(description = "Pasarela utilizada", example = "yape")
    private String pasarela;
    
    @Schema(description = "Monto procesado", example = "150.50")
    private BigDecimal monto;
    
    @Schema(description = "Referencia de la transacción", example = "ORD-2024-001")
    private String referencia;
    
    @Schema(description = "ID de transacción generado por la pasarela", example = "TXN-YAPE-123456")
    private String transaccionId;
    
    // Constructores
    public PagoResponseDTO() {}
    
    // Getters y Setters
    public Boolean getExitoso() { return exitoso; }
    public void setExitoso(Boolean exitoso) { this.exitoso = exitoso; }
    
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    
    public String getPasarela() { return pasarela; }
    public void setPasarela(String pasarela) { this.pasarela = pasarela; }
    
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    
    public String getTransaccionId() { return transaccionId; }
    public void setTransaccionId(String transaccionId) { this.transaccionId = transaccionId; }
}
