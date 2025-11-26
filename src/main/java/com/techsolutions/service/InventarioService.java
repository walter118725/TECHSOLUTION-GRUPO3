package com.techsolutions.service;

import com.techsolutions.model.Producto;
import com.techsolutions.pattern.observer.GestorInventarioObservable;
import com.techsolutions.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de Gestión de Inventario
 * Implementa el patrón Observer para notificaciones de stock bajo
 * RF5: Envía notificaciones cuando el stock cae por debajo del mínimo
 */
@Service
public class InventarioService {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private GestorInventarioObservable gestorInventario;
    
    /**
     * Reduce el stock de un producto y verifica si necesita notificación
     * @param productoId ID del producto
     * @param cantidad Cantidad a reducir
     */
    @Transactional
    public void reducirStock(Long productoId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + productoId));
        
        int stockAnterior = producto.getStock();
        producto.reducirStock(cantidad);
        productoRepository.save(producto);
        
        System.out.println("📦 Stock reducido - Producto: " + producto.getNombre() + 
                         " | Stock anterior: " + stockAnterior + 
                         " | Stock actual: " + producto.getStock() + 
                         " | Stock mínimo: " + producto.getStockMinimo());
        
        // Verificar y notificar si el stock cayó por debajo del mínimo
        gestorInventario.verificarYNotificarStock(producto);
    }
    
    /**
     * Aumenta el stock de un producto
     * @param productoId ID del producto
     * @param cantidad Cantidad a aumentar
     */
    @Transactional
    public void aumentarStock(Long productoId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + productoId));
        
        producto.aumentarStock(cantidad);
        productoRepository.save(producto);
        
        System.out.println("📦 Stock aumentado - Producto: " + producto.getNombre() + 
                         " | Nuevo stock: " + producto.getStock());
    }
    
    /**
     * Configura el stock mínimo de un producto
     * RF5: El nivel mínimo de stock debe ser configurable por producto
     * @param productoId ID del producto
     * @param stockMinimo Nuevo stock mínimo
     */
    @Transactional
    public void configurarStockMinimo(Long productoId, Integer stockMinimo) {
        if (stockMinimo < 0) {
            throw new IllegalArgumentException("El stock mínimo no puede ser negativo");
        }
        
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + productoId));
        
        Integer stockMinimoAnterior = producto.getStockMinimo();
        producto.setStockMinimo(stockMinimo);
        productoRepository.save(producto);
        
        System.out.println("⚙️ Stock mínimo configurado - Producto: " + producto.getNombre() + 
                         " | Anterior: " + stockMinimoAnterior + 
                         " | Nuevo: " + stockMinimo);
        
        // Verificar si con el nuevo stock mínimo se debe notificar
        gestorInventario.verificarYNotificarStock(producto);
    }
    
    /**
     * Obtiene un producto por ID
     */
    public Producto obtenerProducto(Long productoId) {
        return productoRepository.findById(productoId)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + productoId));
    }
}
