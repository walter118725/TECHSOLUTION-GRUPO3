package com.techsolutions.repository;

import com.techsolutions.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio para la entidad Categoria
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    /**
     * Busca categorías activas
     */
    List<Categoria> findByActivaTrue();
}
