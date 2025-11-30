package com.techsolutions.repository;

import com.techsolutions.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio para la entidad Usuario
 * Aplica el patrón Repository de Spring Data JPA
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    /**
     * Busca un usuario por su username
     */
    Optional<Usuario> findByUsername(String username);
    
    /**
     * Busca un usuario por su email
     */
    Optional<Usuario> findByEmail(String email);
    
    /**
     * Verifica si existe un usuario con el username dado
     */
    boolean existsByUsername(String username);
}
