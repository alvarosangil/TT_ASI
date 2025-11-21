package com.congresos.backend.repository;

import com.congresos.backend.model.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Usuario
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por email
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el email dado
     */
    boolean existsByEmail(String email);
}
