package com.techsolutions.controller;

import com.techsolutions.model.Producto;
import com.techsolutions.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.math.BigDecimal;

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
     * Obtiene todos los productos del inventario
     * GET /api/inventario/productos
     */
    @GetMapping("/productos")
    public ResponseEntity<List<Map<String, Object>>> obtenerTodosProductos() {
        List<Producto> productos = inventarioService.obtenerTodosProductos();
        
        List<Map<String, Object>> productosResponse = productos.stream()
            .map(p -> {
                Map<String, Object> prod = new HashMap<>();
                prod.put("id", p.getId());
                prod.put("codigo", p.getCodigo());
                prod.put("nombre", p.getNombre());
                prod.put("descripcion", p.getDescripcion());
                prod.put("precio", p.getPrecio());
                prod.put("stock", p.getStock());
                prod.put("stockMinimo", p.getStockMinimo());
                prod.put("categoria", p.getCategoria() != null ? p.getCategoria().getNombre() : null);
                prod.put("imagen", p.getImagenUrl());
                prod.put("activo", p.getActivo());
                return prod;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(productosResponse);
    }
    
    /**
     * Agrega un nuevo producto al inventario
     * POST /api/inventario/productos
     */
    @PostMapping("/productos")
    public ResponseEntity<Map<String, Object>> agregarProducto(@RequestBody Map<String, Object> request) {
        try {
            String codigo = (String) request.get("codigo");
            String nombre = (String) request.get("nombre");
            String descripcion = (String) request.get("descripcion");
            BigDecimal precio = request.get("precio") != null ? new BigDecimal(String.valueOf(request.get("precio"))) : BigDecimal.ZERO;
            Integer stock = request.get("stock") != null ? ((Number) request.get("stock")).intValue() : 0;
            Integer stockMinimo = request.get("stockMinimo") != null ? ((Number) request.get("stockMinimo")).intValue() : 5;
            String categoria = (String) request.get("categoria");
            String imagen = (String) request.get("imagen");
            
            if (nombre == null || nombre.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "exitoso", false,
                    "mensaje", "El nombre del producto es requerido"
                ));
            }
            
            // Generar código si no se proporciona
            if (codigo == null || codigo.isEmpty()) {
                codigo = "TECH-" + System.currentTimeMillis();
            }
            
            Producto producto = inventarioService.agregarProducto(
                codigo, nombre, descripcion, precio, stock, stockMinimo, categoria, imagen
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("exitoso", true);
            response.put("mensaje", "✅ Producto agregado exitosamente");
            response.put("id", producto.getId());
            response.put("codigo", producto.getCodigo());
            response.put("nombre", producto.getNombre());
            response.put("precio", producto.getPrecio());
            response.put("stock", producto.getStock());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "exitoso", false,
                "mensaje", e.getMessage()
            ));
        }
    }
    
    /**
     * Actualiza un producto existente
     * PUT /api/inventario/productos/{id}
     */
    @PutMapping("/productos/{id}")
    public ResponseEntity<Map<String, Object>> actualizarProducto(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            String nombre = (String) request.get("nombre");
            String descripcion = (String) request.get("descripcion");
            BigDecimal precio = request.get("precio") != null ? new BigDecimal(String.valueOf(request.get("precio"))) : null;
            Integer stock = request.get("stock") != null ? ((Number) request.get("stock")).intValue() : null;
            Integer stockMinimo = request.get("stockMinimo") != null ? ((Number) request.get("stockMinimo")).intValue() : null;
            String categoria = (String) request.get("categoria");
            String imagen = (String) request.get("imagen");
            Boolean activo = request.get("activo") != null ? (Boolean) request.get("activo") : null;
            
            Producto producto = inventarioService.actualizarProducto(
                id, nombre, descripcion, precio, stock, stockMinimo, categoria, imagen, activo
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("exitoso", true);
            response.put("mensaje", "✏️ Producto actualizado exitosamente");
            response.put("id", producto.getId());
            response.put("nombre", producto.getNombre());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "exitoso", false,
                "mensaje", e.getMessage()
            ));
        }
    }
    
    /**
     * Elimina un producto del inventario (lo desactiva)
     * DELETE /api/inventario/productos/{id}
     */
    @DeleteMapping("/productos/{id}")
    public ResponseEntity<Map<String, Object>> eliminarProducto(@PathVariable Long id) {
        try {
            Producto producto = inventarioService.obtenerProducto(id);
            String nombreProducto = producto.getNombre();
            
            inventarioService.eliminarProducto(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("exitoso", true);
            response.put("mensaje", "🗑️ Producto '" + nombreProducto + "' eliminado exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Elimina permanentemente un producto del inventario
     * DELETE /api/inventario/productos/{id}/permanente
     */
    @DeleteMapping("/productos/{id}/permanente")
    public ResponseEntity<Map<String, Object>> eliminarProductoPermanente(@PathVariable Long id) {
        try {
            Producto producto = inventarioService.obtenerProducto(id);
            String nombreProducto = producto.getNombre();
            
            inventarioService.eliminarProductoPermanente(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("exitoso", true);
            response.put("mensaje", "🗑️ Producto '" + nombreProducto + "' eliminado PERMANENTEMENTE");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
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
