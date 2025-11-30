package com.techsolutions.pattern.observer;

import com.techsolutions.model.Producto;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * Sujeto Observable - Gestiona observadores de inventario
 * Patrón Observer: Notifica a los observadores cuando hay cambios en el inventario
 * RF5: Sistema envía notificaciones cuando el stock cae por debajo del mínimo
 */
@Component
public class GestorInventarioObservable {
    
    private final List<ObservadorInventario> observadores = new ArrayList<>();
    
    /**
     * Agrega un observador
     * GRASP: Controller - gestiona la lista de observadores
     */
    public void agregarObservador(ObservadorInventario observador) {
        if (!observadores.contains(observador)) {
            observadores.add(observador);
            System.out.println("Observador agregado: " + observador.getRol());
        }
    }
    
    /**
     * Remueve un observador
     */
    public void removerObservador(ObservadorInventario observador) {
        observadores.remove(observador);
    }
    
    /**
     * Verifica el stock y notifica si es necesario
     * Este método se llamará después de cada operación que reduzca el stock
     */
    public void verificarYNotificarStock(Producto producto) {
        if (producto.necesitaReposicion()) {
            notificarStockBajo(producto);
        }
    }
    
    /**
     * Notifica a todos los observadores con roles GERENTE o COMPRAS
     */
    private void notificarStockBajo(Producto producto) {
        System.out.println("\n>>> Notificando stock bajo para producto: " + producto.getNombre());
        
        for (ObservadorInventario observador : observadores) {
            // RF5: Solo notificar a GERENTE y COMPRAS
            if ("GERENTE".equals(observador.getRol()) || "COMPRAS".equals(observador.getRol())) {
                observador.notificarStockBajo(producto);
            }
        }
    }
    
    /**
     * Obtiene la lista de observadores (para testing)
     */
    public List<ObservadorInventario> getObservadores() {
        return new ArrayList<>(observadores);
    }
}
