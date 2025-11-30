package com.techsolutions.controller;

import com.techsolutions.model.Producto;
import com.techsolutions.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para gestión de inventario
 * Implementa el patrón Observer para notificaciones de stock bajo
 * RF5: Sistema envía notificaciones cuando el stock cae por debajo del mínimo
 */
@RestController
@RequestMapping("/api/inventario")
public class InventarioController {
    
    @Autowired
    private InventarioService inventarioService;
    
    /**
     * Reduce el stock de un producto
     * POST /api/inventario/{id}/reducir
     */
    @PostMapping("/{id}/reducir")
    public ResponseEntity<Map<String, Object>> reducirStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        
        try {
            Integer cantidad = request.get("cantidad");
            if (cantidad == null || cantidad <= 0) {
                return ResponseEntity.badRequest().body(Map.of(
                    "exitoso", false,
                    "mensaje", "La cantidad debe ser mayor a 0"
                ));
            }
            
            inventarioService.reducirStock(id, cantidad);
            Producto producto = inventarioService.obtenerProducto(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("exitoso", true);
            response.put("mensaje", "Stock reducido exitosamente");
            response.put("producto", producto.getNombre());
            response.put("stockActual", producto.getStock());
            response.put("stockMinimo", producto.getStockMinimo());
            response.put("necesitaReposicion", producto.necesitaReposicion());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "exitoso", false,
                "mensaje", e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Aumenta el stock de un producto
     * POST /api/inventario/{id}/aumentar
     */
    @PostMapping("/{id}/aumentar")
    public ResponseEntity<Map<String, Object>> aumentarStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        
        try {
            Integer cantidad = request.get("cantidad");
            if (cantidad == null || cantidad <= 0) {
                return ResponseEntity.badRequest().body(Map.of(
                    "exitoso", false,
                    "mensaje", "La cantidad debe ser mayor a 0"
                ));
            }
            
            inventarioService.aumentarStock(id, cantidad);
            Producto producto = inventarioService.obtenerProducto(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("exitoso", true);
            response.put("mensaje", "Stock aumentado exitosamente");
            response.put("producto", producto.getNombre());
            response.put("stockActual", producto.getStock());
            response.put("stockMinimo", producto.getStockMinimo());
            response.put("necesitaReposicion", producto.necesitaReposicion());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Configura el stock mínimo de un producto
     * PUT /api/inventario/{id}/stock-minimo
     * RF5: El nivel mínimo de stock debe ser configurable por producto
     */
    @PutMapping("/{id}/stock-minimo")
    public ResponseEntity<Map<String, Object>> configurarStockMinimo(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        
        try {
            Integer stockMinimo = request.get("stockMinimo");
            if (stockMinimo == null || stockMinimo < 0) {
                return ResponseEntity.badRequest().body(Map.of(
                    "exitoso", false,
                    "mensaje", "El stock mínimo debe ser mayor o igual a 0"
                ));
            }
            
            inventarioService.configurarStockMinimo(id, stockMinimo);
            Producto producto = inventarioService.obtenerProducto(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("exitoso", true);
            response.put("mensaje", "⚙️ Stock mínimo configurado exitosamente");
            response.put("producto", producto.getNombre());
            response.put("stockActual", producto.getStock());
            response.put("stockMinimoNuevo", producto.getStockMinimo());
            response.put("necesitaReposicion", producto.necesitaReposicion());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(Map.of(
                "exitoso", false,
                "mensaje", e.getMessage()
            ));
        }
    }
    
    /**
     * Obtiene el estado del inventario de un producto
     * GET /api/inventario/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerEstadoInventario(@PathVariable Long id) {
        try {
            Producto producto = inventarioService.obtenerProducto(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", producto.getId());
            response.put("codigo", producto.getCodigo());
            response.put("nombre", producto.getNombre());
            response.put("stockActual", producto.getStock());
            response.put("stockMinimo", producto.getStockMinimo());
            response.put("necesitaReposicion", producto.necesitaReposicion());
            response.put("activo", producto.getActivo());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
