package com.techsolutions.pattern.adapter;

import java.math.BigDecimal;

/**
 * Adaptador para Yape - implementa la interfaz común PasarelaPago
 * Patrón Adapter: Adapta la API de Yape a nuestra interfaz común
 */
public class YapeAdapter implements PasarelaPago {
    
    private boolean habilitada;
    
    public YapeAdapter() {
        this.habilitada = true;
    }
    
    @Override
    public boolean procesarPago(BigDecimal monto, String referencia) {
        if (!habilitada) {
            System.out.println("Yape no está habilitada");
            return false;
        }
        
        // Simulación de llamada a API de Yape
        System.out.println("Procesando pago con Yape...");
        System.out.println("Monto: S/ " + monto);
        System.out.println("Referencia: " + referencia);
        
        return true;
    }
    
    @Override
    public String verificarEstado(String referencia) {
        return "COMPLETADO - Yape";
    }
    
    @Override
    public String getNombre() {
        return "Yape";
    }
    
    @Override
    public boolean estaHabilitada() {
        return habilitada;
    }
    
    public void setHabilitada(boolean habilitada) {
        this.habilitada = habilitada;
    }
}
