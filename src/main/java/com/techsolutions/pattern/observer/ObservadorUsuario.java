package com.techsolutions.pattern.observer;

import com.techsolutions.model.Producto;
import com.techsolutions.model.Usuario;

/**
 * Observador concreto para usuarios
 * Implementa la interfaz ObservadorInventario
 */
public class ObservadorUsuario implements ObservadorInventario {
    
    private final Usuario usuario;
    
    public ObservadorUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    @Override
    public void notificarStockBajo(Producto producto) {
        System.out.println("====================================");
        System.out.println("NOTIFICACIÓN DE STOCK BAJO");
        System.out.println("Para: " + usuario.getNombreCompleto() + " (" + usuario.getRoles() + ")");
        System.out.println("Producto: " + producto.getNombre());
        System.out.println("Stock actual: " + producto.getStock());
        System.out.println("Stock mínimo: " + producto.getStockMinimo());
        System.out.println("¡ACCIÓN REQUERIDA: Reponer inventario!");
        System.out.println("====================================");
    }
    
    @Override
    public String getRol() {
        // Retorna el primer rol del usuario
        return usuario.getRoles().isEmpty() ? "" : usuario.getRoles().iterator().next();
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
}
