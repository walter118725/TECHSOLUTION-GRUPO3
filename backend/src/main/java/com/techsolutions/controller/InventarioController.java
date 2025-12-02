package com.techsolutions.controller;

import com.techsolutions.model.Producto;
import com.techsolutions.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Inventario", description = "📦 API para gestión de productos e inventario - Implementa Patrón Observer para alertas de stock bajo")
public class InventarioController {
    
    @Autowired
    private InventarioService inventarioService;
    
    /**
     * Obtiene todos los productos del inventario
     * GET /api/inventario/productos
     */
    @GetMapping("/productos")
    @Operation(
        summary = "📋 Listar todos los productos",
        description = "Obtiene la lista completa de productos del inventario con información de stock, precio y categoría"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "✅ Lista de productos obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    [
                      {
                        "id": 1,
                        "codigo": "TECH-001",
                        "nombre": "Laptop HP ProBook",
                        "descripcion": "Laptop empresarial",
                        "precio": 2599.99,
                        "stock": 25,
                        "stockMinimo": 10,
                        "categoria": "Electrónicos",
                        "activo": true
                      }
                    ]
                    """)))
    })
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
    @Operation(
        summary = "➕ Agregar nuevo producto",
        description = "Crea un nuevo producto en el inventario. Si no se proporciona código, se genera automáticamente."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "✅ Producto creado exitosamente",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "exitoso": true,
                      "mensaje": "✅ Producto agregado exitosamente",
                      "id": 6,
                      "codigo": "TECH-123456",
                      "nombre": "Monitor Samsung 24\\"",
                      "precio": 599.99,
                      "stock": 15
                    }
                    """))),
        @ApiResponse(responseCode = "400", description = "❌ Datos inválidos - El nombre es requerido")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos del nuevo producto",
        required = true,
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "codigo": "TECH-006",
                  "nombre": "Monitor Samsung 24\\"",
                  "descripcion": "Monitor Full HD LED",
                  "precio": 599.99,
                  "stock": 15,
                  "stockMinimo": 5,
                  "categoria": "Electrónicos",
                  "imagen": "/images/monitor.jpg"
                }
                """)))
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
    @Operation(
        summary = "✏️ Actualizar producto",
        description = "Actualiza los datos de un producto existente. Solo se actualizan los campos proporcionados."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "✅ Producto actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "❌ Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "❌ Producto no encontrado")
    })
    public ResponseEntity<Map<String, Object>> actualizarProducto(
            @Parameter(description = "ID del producto a actualizar", example = "1", required = true)
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
    @Operation(
        summary = "🗑️ Eliminar producto (soft delete)",
        description = "Desactiva un producto del inventario. El producto no se elimina físicamente."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "✅ Producto desactivado exitosamente"),
        @ApiResponse(responseCode = "404", description = "❌ Producto no encontrado")
    })
    public ResponseEntity<Map<String, Object>> eliminarProducto(
            @Parameter(description = "ID del producto a eliminar", example = "1", required = true)
            @PathVariable Long id) {
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
    @Operation(
        summary = "⚠️ Eliminar producto permanentemente",
        description = "Elimina físicamente un producto de la base de datos. Esta acción no se puede deshacer."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "✅ Producto eliminado permanentemente"),
        @ApiResponse(responseCode = "404", description = "❌ Producto no encontrado")
    })
    public ResponseEntity<Map<String, Object>> eliminarProductoPermanente(
            @Parameter(description = "ID del producto a eliminar", example = "1", required = true)
            @PathVariable Long id) {
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
    @Operation(
        summary = "📉 Reducir stock (Patrón Observer)",
        description = """
            Reduce el stock de un producto. 
            **Patrón Observer**: Si el stock queda por debajo del mínimo, se notifica automáticamente 
            a los usuarios con rol GERENTE y COMPRAS.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "✅ Stock reducido exitosamente",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "exitoso": true,
                      "mensaje": "Stock reducido exitosamente",
                      "producto": "Mouse Inalámbrico",
                      "stockActual": 5,
                      "stockMinimo": 10,
                      "necesitaReposicion": true
                    }
                    """))),
        @ApiResponse(responseCode = "400", description = "❌ Stock insuficiente o cantidad inválida"),
        @ApiResponse(responseCode = "404", description = "❌ Producto no encontrado")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Cantidad a reducir",
        required = true,
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "cantidad": 5
                }
                """)))
    public ResponseEntity<Map<String, Object>> reducirStock(
            @Parameter(description = "ID del producto", example = "2", required = true)
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
    @Operation(
        summary = "📈 Aumentar stock",
        description = "Aumenta el stock de un producto (ej: recepción de mercadería)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "✅ Stock aumentado exitosamente"),
        @ApiResponse(responseCode = "400", description = "❌ Cantidad inválida"),
        @ApiResponse(responseCode = "404", description = "❌ Producto no encontrado")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Cantidad a aumentar",
        required = true,
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "cantidad": 20
                }
                """)))
    public ResponseEntity<Map<String, Object>> aumentarStock(
            @Parameter(description = "ID del producto", example = "2", required = true)
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
    @Operation(
        summary = "⚙️ Configurar stock mínimo (RF5)",
        description = """
            Configura el nivel mínimo de stock para un producto.
            **RF5**: El nivel mínimo de stock debe ser configurable por producto.
            Si el stock actual es menor al nuevo mínimo, se activará la notificación.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "✅ Stock mínimo configurado"),
        @ApiResponse(responseCode = "400", description = "❌ Valor inválido"),
        @ApiResponse(responseCode = "404", description = "❌ Producto no encontrado")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Nuevo stock mínimo",
        required = true,
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "stockMinimo": 15
                }
                """)))
    public ResponseEntity<Map<String, Object>> configurarStockMinimo(
            @Parameter(description = "ID del producto", example = "1", required = true)
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
    @Operation(
        summary = "🔍 Obtener estado de producto",
        description = "Obtiene información detallada del stock de un producto incluyendo si necesita reposición"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "✅ Información obtenida",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "id": 2,
                      "codigo": "TECH-002",
                      "nombre": "Mouse Inalámbrico Logitech",
                      "stockActual": 8,
                      "stockMinimo": 15,
                      "necesitaReposicion": true,
                      "activo": true
                    }
                    """))),
        @ApiResponse(responseCode = "404", description = "❌ Producto no encontrado")
    })
    public ResponseEntity<Map<String, Object>> obtenerEstadoInventario(
            @Parameter(description = "ID del producto", example = "2", required = true)
            @PathVariable Long id) {
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
