package com.techsolutions.service;

import com.techsolutions.model.Producto;
import com.techsolutions.model.Categoria;
import com.techsolutions.pattern.observer.GestorInventarioObservable;
import com.techsolutions.repository.ProductoRepository;
import com.techsolutions.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio de Gestión de Inventario
 * Implementa el patrón Observer para notificaciones de stock bajo
 * RF5: Envía notificaciones cuando el stock cae por debajo del mínimo
 */
@Service
@SuppressWarnings("null")
public class InventarioService {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
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
    
    /**
     * Obtiene todos los productos
     */
    public List<Producto> obtenerTodosProductos() {
        return productoRepository.findAll();
    }
    
    /**
     * Obtiene productos activos
     */
    public List<Producto> obtenerProductosActivos() {
        return productoRepository.findByActivoTrue();
    }
    
    /**
     * Agrega un nuevo producto al inventario
     */
    @Transactional
    public Producto agregarProducto(String codigo, String nombre, String descripcion, 
                                     BigDecimal precio, Integer stock, Integer stockMinimo,
                                     String categoriaNombre, String imagenUrl) {
        
        // Verificar si el código ya existe
        if (productoRepository.findByCodigo(codigo).isPresent()) {
            throw new IllegalArgumentException("Ya existe un producto con el código: " + codigo);
        }
        
        Producto producto = new Producto();
        producto.setCodigo(codigo);
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setStockMinimo(stockMinimo != null ? stockMinimo : 5);
        producto.setImagenUrl(imagenUrl);
        producto.setActivo(true);
        
        // Buscar categoría por nombre
        if (categoriaNombre != null && !categoriaNombre.isEmpty()) {
            List<Categoria> categorias = categoriaRepository.findByActivaTrue();
            Categoria categoria = categorias.stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(categoriaNombre))
                .findFirst()
                .orElseGet(() -> {
                    Categoria nuevaCategoria = new Categoria();
                    nuevaCategoria.setNombre(categoriaNombre);
                    nuevaCategoria.setDescripcion("Categoría " + categoriaNombre);
                    nuevaCategoria.setActiva(true);
                    return categoriaRepository.save(nuevaCategoria);
                });
            producto.setCategoria(categoria);
        }
        
        Producto productoGuardado = productoRepository.save(producto);
        System.out.println("✅ Producto agregado al inventario: " + nombre + " (Código: " + codigo + ")");
        
        return productoGuardado;
    }
    
    /**
     * Elimina un producto del inventario (lo desactiva)
     */
    @Transactional
    public void eliminarProducto(Long productoId) {
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + productoId));
        
        producto.setActivo(false);
        productoRepository.save(producto);
        System.out.println("🗑️ Producto eliminado del inventario: " + producto.getNombre());
    }
    
    /**
     * Elimina permanentemente un producto del inventario
     */
    @Transactional
    public void eliminarProductoPermanente(Long productoId) {
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + productoId));
        
        String nombreProducto = producto.getNombre();
        productoRepository.delete(producto);
        System.out.println("🗑️ Producto eliminado PERMANENTEMENTE: " + nombreProducto);
    }
    
    /**
     * Actualiza un producto existente
     */
    @Transactional
    public Producto actualizarProducto(Long productoId, String nombre, String descripcion,
                                        BigDecimal precio, Integer stock, Integer stockMinimo,
                                        String categoriaNombre, String imagenUrl, Boolean activo) {
        
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + productoId));
        
        if (nombre != null) producto.setNombre(nombre);
        if (descripcion != null) producto.setDescripcion(descripcion);
        if (precio != null) producto.setPrecio(precio);
        if (stock != null) producto.setStock(stock);
        if (stockMinimo != null) producto.setStockMinimo(stockMinimo);
        if (imagenUrl != null) producto.setImagenUrl(imagenUrl);
        if (activo != null) producto.setActivo(activo);
        
        if (categoriaNombre != null && !categoriaNombre.isEmpty()) {
            List<Categoria> categorias = categoriaRepository.findByActivaTrue();
            Categoria categoria = categorias.stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(categoriaNombre))
                .findFirst()
                .orElseGet(() -> {
                    Categoria nuevaCategoria = new Categoria();
                    nuevaCategoria.setNombre(categoriaNombre);
                    nuevaCategoria.setDescripcion("Categoría " + categoriaNombre);
                    nuevaCategoria.setActiva(true);
                    return categoriaRepository.save(nuevaCategoria);
                });
            producto.setCategoria(categoria);
        }
        
        Producto productoActualizado = productoRepository.save(producto);
        System.out.println("✏️ Producto actualizado: " + producto.getNombre());
        
        // Verificar si necesita notificación de stock bajo
        gestorInventario.verificarYNotificarStock(productoActualizado);
        
        return productoActualizado;
    }
}
