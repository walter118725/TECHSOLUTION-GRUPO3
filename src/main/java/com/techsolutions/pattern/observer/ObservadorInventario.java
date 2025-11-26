package com.techsolutions.pattern.observer;

import com.techsolutions.model.Producto;

/**
 * Patrón Observer - Interfaz para observadores de inventario
 * RF5: Enviar notificaciones cuando el stock caiga por debajo del mínimo
 */
public interface ObservadorInventario {
    
    /**
     * Notifica cuando un producto tiene stock bajo
     * @param producto Producto con stock bajo
     */
    void notificarStockBajo(Producto producto);
    
    /**
     * Obtiene el rol del observador
     * @return Rol del observador (GERENTE, COMPRAS)
     */
    String getRol();
}
