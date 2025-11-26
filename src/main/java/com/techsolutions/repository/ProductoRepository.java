package com.techsolutions.repository;

import com.techsolutions.model.Producto;
import com.techsolutions.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Producto
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    /**
     * Busca un producto por su código
     */
    Optional<Producto> findByCodigo(String codigo);
    
    /**
     * Busca productos activos
     */
    List<Producto> findByActivoTrue();
    
    /**
     * Busca productos por categoría
     */
    List<Producto> findByCategoria(Categoria categoria);
    
    /**
     * Busca productos con stock bajo (stock <= stockMinimo)
     * Para usar con el patrón Observer
     */
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo AND p.activo = true")
    List<Producto> findProductosConStockBajo();
    
    /**
     * Busca productos por nombre (búsqueda parcial)
     */
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
