package com.techsolutions;

import com.techsolutions.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la clase Usuario
 * Verifica la lógica de roles y permisos
 */
@SuppressWarnings("all")
@DisplayName("Tests de Usuario")
class UsuarioTest {

    private Usuario usuario;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");
        usuario.setPassword("password123");
        usuario.setNombreCompleto("Usuario de Prueba");
        usuario.setEmail("test@techsolutions.com");
        usuario.setActivo(true);
        usuario.setRoles(new HashSet<>());
    }

    @SuppressWarnings("unused")
    @Nested
    @DisplayName("Tests de Roles")
    class RolesTests {

        @Test
        @DisplayName("Usuario con rol GERENTE debe tener el rol")
        void usuarioConRolGerente_debeRetornarTrue() {
            // Arrange
            Set<String> roles = new HashSet<>();
            roles.add("GERENTE");
            usuario.setRoles(roles);

            // Act & Assert
            assertTrue(usuario.tieneRol("GERENTE"));
        }

        @Test
        @DisplayName("Usuario sin rol GERENTE no debe tener el rol")
        void usuarioSinRolGerente_debeRetornarFalse() {
            // Arrange
            Set<String> roles = new HashSet<>();
            roles.add("CLIENTE");
            usuario.setRoles(roles);

            // Act & Assert
            assertFalse(usuario.tieneRol("GERENTE"));
        }

        @Test
        @DisplayName("Usuario con múltiples roles debe tener todos")
        void usuarioConMultiplesRoles_debeTenerTodos() {
            // Arrange
            Set<String> roles = new HashSet<>();
            roles.add("GERENTE");
            roles.add("ADMIN");
            usuario.setRoles(roles);

            // Act & Assert
            assertTrue(usuario.tieneRol("GERENTE"));
            assertTrue(usuario.tieneRol("ADMIN"));
            assertFalse(usuario.tieneRol("CLIENTE"));
        }

        @Test
        @DisplayName("Usuario sin roles no debe tener ningún rol")
        void usuarioSinRoles_noDebeTenerNingunRol() {
            // Arrange - roles ya está vacío por setUp

            // Act & Assert
            assertFalse(usuario.tieneRol("GERENTE"));
            assertFalse(usuario.tieneRol("ADMIN"));
            assertFalse(usuario.tieneRol("CLIENTE"));
        }
    }

    @SuppressWarnings("unused")
    @Nested
    @DisplayName("Tests de Acceso a Reportes Financieros")
    class AccesoReportesTests {

        @Test
        @DisplayName("GERENTE puede acceder a reportes financieros")
        void gerente_puedeAccederAReportes() {
            // Arrange
            Set<String> roles = new HashSet<>();
            roles.add("GERENTE");
            usuario.setRoles(roles);

            // Act & Assert
            assertTrue(usuario.puedeAccederReportesFinancieros());
        }

        @Test
        @DisplayName("ADMIN no puede acceder a reportes financieros")
        void admin_noPuedeAccederAReportes() {
            // Arrange
            Set<String> roles = new HashSet<>();
            roles.add("ADMIN");
            usuario.setRoles(roles);

            // Act & Assert
            assertFalse(usuario.puedeAccederReportesFinancieros());
        }

        @Test
        @DisplayName("CLIENTE no puede acceder a reportes financieros")
        void cliente_noPuedeAccederAReportes() {
            // Arrange
            Set<String> roles = new HashSet<>();
            roles.add("CLIENTE");
            usuario.setRoles(roles);

            // Act & Assert
            assertFalse(usuario.puedeAccederReportesFinancieros());
        }

        @Test
        @DisplayName("Usuario sin roles no puede acceder a reportes")
        void usuarioSinRoles_noPuedeAccederAReportes() {
            // Arrange - roles está vacío

            // Act & Assert
            assertFalse(usuario.puedeAccederReportesFinancieros());
        }
    }

    @SuppressWarnings("unused")
    @Nested
    @DisplayName("Tests de Estado del Usuario")
    class EstadoUsuarioTests {

        @Test
        @DisplayName("Usuario activo por defecto")
        void usuarioNuevo_debeEstarActivo() {
            // Arrange
            Usuario nuevoUsuario = new Usuario();
            
            // Act & Assert
            assertTrue(nuevoUsuario.getActivo());
        }

        @Test
        @DisplayName("Usuario puede ser desactivado")
        void usuario_puedeSerDesactivado() {
            // Arrange
            usuario.setActivo(false);

            // Act & Assert
            assertFalse(usuario.getActivo());
        }
    }

    @SuppressWarnings("unused")
    @Nested
    @DisplayName("Tests de Getters y Setters")
    class GettersSettersTests {

        @Test
        @DisplayName("Getters y Setters funcionan correctamente")
        void gettersSetters_funcionanCorrectamente() {
            // Arrange
            Usuario u = new Usuario();
            
            // Act
            u.setId(100L);
            u.setUsername("admin");
            u.setPassword("secret");
            u.setNombreCompleto("Administrador");
            u.setEmail("admin@test.com");
            
            // Assert
            assertEquals(100L, u.getId());
            assertEquals("admin", u.getUsername());
            assertEquals("secret", u.getPassword());
            assertEquals("Administrador", u.getNombreCompleto());
            assertEquals("admin@test.com", u.getEmail());
        }
    }
}
