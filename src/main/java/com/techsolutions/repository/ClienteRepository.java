package com.techsolutions.repository;

import com.techsolutions.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio para la entidad Cliente
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    /**
     * Busca un cliente por su documento
     */
    Optional<Cliente> findByDocumento(String documento);
    
    /**
     * Busca un cliente por su email
     */
    Optional<Cliente> findByEmail(String email);
}
