package com.techsolutions.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.math.BigDecimal;

/**
 * DTO para solicitud de procesamiento de pago
 */
@Schema(description = "Solicitud para procesar un pago")
public class PagoRequestDTO {
    
    @Schema(description = "Identificador de la pasarela de pago", 
            example = "yape", 
            allowableValues = {"paypal", "yape", "plin"},
            requiredMode = RequiredMode.REQUIRED)
    private String pasarela;
    
    @Schema(description = "Monto a pagar en soles", example = "150.50", requiredMode = RequiredMode.REQUIRED)
    private BigDecimal monto;
    
    @Schema(description = "Referencia única de la transacción", example = "ORD-2024-001", requiredMode = RequiredMode.REQUIRED)
    private String referencia;
    
    // Constructores
    public PagoRequestDTO() {}
    
    public PagoRequestDTO(String pasarela, BigDecimal monto, String referencia) {
        this.pasarela = pasarela;
        this.monto = monto;
        this.referencia = referencia;
    }
    
    // Getters y Setters
    public String getPasarela() { return pasarela; }
    public void setPasarela(String pasarela) { this.pasarela = pasarela; }
    
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
}
