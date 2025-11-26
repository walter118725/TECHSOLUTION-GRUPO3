package com.techsolutions.pattern.adapter;

import java.math.BigDecimal;

/**
 * Adaptador para PayPal - implementa la interfaz común PasarelaPago
 * Patrón Adapter: Adapta la API de PayPal a nuestra interfaz común
 */
public class PayPalAdapter implements PasarelaPago {
    
    private boolean habilitada;
    
    public PayPalAdapter() {
        this.habilitada = true; // Por defecto habilitada
    }
    
    @Override
    public boolean procesarPago(BigDecimal monto, String referencia) {
        if (!habilitada) {
            System.out.println("PayPal no está habilitada");
            return false;
        }
        
        // Simulación de llamada a API de PayPal
        System.out.println("Procesando pago con PayPal...");
        System.out.println("Monto: " + monto);
        System.out.println("Referencia: " + referencia);
        
        // Simulación de procesamiento exitoso
        return true;
    }
    
    @Override
    public String verificarEstado(String referencia) {
        return "COMPLETADO - PayPal";
    }
    
    @Override
    public String getNombre() {
        return "PayPal";
    }
    
    @Override
    public boolean estaHabilitada() {
        return habilitada;
    }
    
    public void setHabilitada(boolean habilitada) {
        this.habilitada = habilitada;
    }
}
