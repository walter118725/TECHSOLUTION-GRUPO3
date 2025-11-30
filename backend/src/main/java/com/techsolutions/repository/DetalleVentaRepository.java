package com.techsolutions.repository;

import com.techsolutions.model.DetalleVenta;
import com.techsolutions.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio para la entidad DetalleVenta
 */
@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    
    /**
     * Busca detalles por venta
     */
    List<DetalleVenta> findByVenta(Venta venta);
}
