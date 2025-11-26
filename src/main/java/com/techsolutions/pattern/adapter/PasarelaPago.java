package com.techsolutions.pattern.adapter;

import java.math.BigDecimal;

/**
 * Patrón Adapter - Interfaz común para todas las pasarelas de pago
 * Permite integrar múltiples pasarelas de pago con una interfaz unificada
 * RF1: El sistema debe integrar múltiples pasarelas de pago mediante un adaptador común
 */
public interface PasarelaPago {
    
    /**
     * Procesa un pago a través de la pasarela
     * @param monto Monto a procesar
     * @param referencia Referencia de la transacción
     * @return true si el pago fue exitoso, false en caso contrario
     */
    boolean procesarPago(BigDecimal monto, String referencia);
    
    /**
     * Verifica el estado de una transacción
     * @param referencia Referencia de la transacción
     * @return Estado de la transacción
     */
    String verificarEstado(String referencia);
    
    /**
     * Obtiene el nombre de la pasarela
     * @return Nombre de la pasarela
     */
    String getNombre();
    
    /**
     * Verifica si la pasarela está habilitada
     * @return true si está habilitada
     */
    boolean estaHabilitada();
}
