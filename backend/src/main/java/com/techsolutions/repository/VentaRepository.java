package com.techsolutions.repository;

import com.techsolutions.model.Venta;
import com.techsolutions.model.Cliente;
import com.techsolutions.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Venta
 */
@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    
    /**
     * Busca ventas por cliente
     */
    List<Venta> findByCliente(Cliente cliente);
    
    /**
     * Busca ventas por usuario (vendedor)
     */
    List<Venta> findByUsuario(Usuario usuario);
    
    /**
     * Busca ventas por estado
     */
    List<Venta> findByEstado(Venta.EstadoVenta estado);
    
    /**
     * Busca ventas en un rango de fechas
     */
    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    
    /**
     * Obtiene el total de ventas en un rango de fechas
     */
    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.fecha BETWEEN :inicio AND :fin AND v.estado = 'COMPLETADA'")
    Double getTotalVentasPorPeriodo(LocalDateTime inicio, LocalDateTime fin);
}
