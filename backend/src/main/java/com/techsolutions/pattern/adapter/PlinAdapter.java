package com.techsolutions.pattern.adapter;

import java.math.BigDecimal;

/**
 * Adaptador para Plin - implementa la interfaz común PasarelaPago
 * Patrón Adapter: Adapta la API de Plin a nuestra interfaz común
 */
public class PlinAdapter implements PasarelaPago {
    
    private boolean habilitada;
    
    public PlinAdapter() {
        this.habilitada = true;
    }
    
    @Override
    public boolean procesarPago(BigDecimal monto, String referencia) {
        if (!habilitada) {
            System.out.println("Plin no está habilitada");
            return false;
        }
        
        // Simulación de llamada a API de Plin
        System.out.println("Procesando pago con Plin...");
        System.out.println("Monto: S/ " + monto);
        System.out.println("Referencia: " + referencia);
        
        return true;
    }
    
    @Override
    public String verificarEstado(String referencia) {
        return "COMPLETADO - Plin";
    }
    
    @Override
    public String getNombre() {
        return "Plin";
    }
    
    @Override
    public boolean estaHabilitada() {
        return habilitada;
    }
    
    public void setHabilitada(boolean habilitada) {
        this.habilitada = habilitada;
    }
}
