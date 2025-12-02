package com.techsolutions;

import com.techsolutions.model.Usuario;
import com.techsolutions.pattern.proxy.ProxyReporteFinanciero;
import com.techsolutions.pattern.proxy.ReporteFinanciero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para el Patrón Proxy - Control de Acceso a Reportes
 * RF3: El sistema debe proteger el acceso validando credenciales y roles
 * RF4: Solo usuarios con rol GERENTE pueden acceder a reportes financieros
 */
@SuppressWarnings("all")
@DisplayName("Tests de Patrón Proxy - Control de Acceso (RF3, RF4)")
class ProxyReporteFinancieroTest {

    private Usuario usuarioGerente;
    private Usuario usuarioAdmin;
    private Usuario usuarioCliente;
    private Usuario usuarioInactivo;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        // Usuario GERENTE (tiene acceso)
        usuarioGerente = crearUsuario("gerente01", "Juan Pérez", "GERENTE", true);
        
        // Usuario ADMIN (NO tiene acceso)
        usuarioAdmin = crearUsuario("admin01", "María López", "ADMIN", true);
        
        // Usuario CLIENTE (NO tiene acceso)
        usuarioCliente = crearUsuario("cliente01", "Pedro Gómez", "CLIENTE", true);
        
        // Usuario inactivo (NO tiene acceso)
        usuarioInactivo = crearUsuario("inactivo01", "Ana Torres", "GERENTE", false);
        
        // Fechas de prueba
        fechaInicio = LocalDateTime.now().minusMonths(1);
        fechaFin = LocalDateTime.now();
    }

    private Usuario crearUsuario(String username, String nombreCompleto, String rol, boolean activo) {
        Usuario usuario = new Usuario();
        usuario.setId(System.currentTimeMillis());
        usuario.setUsername(username);
        usuario.setNombreCompleto(nombreCompleto);
        usuario.setEmail(username + "@techsolutions.com");
        usuario.setPassword("password123");
        usuario.setActivo(activo);
        
        Set<String> roles = new HashSet<>();
        roles.add(rol);
        usuario.setRoles(roles);
        
        return usuario;
    }

    @SuppressWarnings("unused")
    @Nested
    @DisplayName("RF4: Acceso de Usuario GERENTE")
    class AccesoGerenteTests {

        @Test
        @DisplayName("GERENTE puede acceder a reporte de ventas")
        void gerente_puedeAccederReporteVentas() {
            // Arrange
            ReporteFinanciero proxy = new ProxyReporteFinanciero(usuarioGerente);

            // Act
            Map<String, Object> reporte = proxy.generarReporteVentas(fechaInicio, fechaFin);

            // Assert
            assertNotNull(reporte);
            assertEquals("Reporte de Ventas", reporte.get("titulo"));
            assertEquals("gerente01", reporte.get("generadoPor"));
        }

        @Test
        @DisplayName("GERENTE puede acceder a reporte de ingresos y gastos")
        void gerente_puedeAccederReporteIngresosGastos() {
            // Arrange
            ReporteFinanciero proxy = new ProxyReporteFinanciero(usuarioGerente);

            // Act
            Map<String, Object> reporte = proxy.generarReporteIngresosGastos(11, 2025);

            // Assert
            assertNotNull(reporte);
            assertEquals("Reporte de Ingresos y Gastos", reporte.get("titulo"));
        }

        @Test
        @DisplayName("GERENTE puede acceder a reporte de utilidades")
        void gerente_puedeAccederReporteUtilidades() {
            // Arrange
            ReporteFinanciero proxy = new ProxyReporteFinanciero(usuarioGerente);

            // Act
            Map<String, Object> reporte = proxy.generarReporteUtilidades(fechaInicio, fechaFin);

            // Assert
            assertNotNull(reporte);
            assertEquals("Reporte de Utilidades", reporte.get("titulo"));
        }

        @Test
        @DisplayName("GERENTE puede exportar reporte a PDF")
        void gerente_puedeExportarPDF() {
            // Arrange
            ReporteFinanciero proxy = new ProxyReporteFinanciero(usuarioGerente);
            Map<String, Object> datos = Map.of("contenido", "datos de prueba");

            // Act
            String resultado = proxy.exportarAPDF("ventas", datos);

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.contains("ventas"));
        }
    }

    @SuppressWarnings("unused")
    @Nested
    @DisplayName("RF4: Bloqueo de Usuario ADMIN")
    class BloqueoAdminTests {

        @Test
        @DisplayName("ADMIN no puede acceder a reporte de ventas")
        void admin_noPuedeAccederReporteVentas() {
            // Arrange
            ReporteFinanciero proxy = new ProxyReporteFinanciero(usuarioAdmin);

            // Act & Assert
            SecurityException exception = assertThrows(SecurityException.class, () -> 
                proxy.generarReporteVentas(fechaInicio, fechaFin)
            );
            
            assertTrue(exception.getMessage().contains("Acceso denegado"));
            assertTrue(exception.getMessage().contains("GERENTE"));
        }

        @Test
        @DisplayName("ADMIN no puede acceder a reporte de ingresos y gastos")
        void admin_noPuedeAccederReporteIngresosGastos() {
            // Arrange
            ReporteFinanciero proxy = new ProxyReporteFinanciero(usuarioAdmin);

            // Act & Assert
            SecurityException exception = assertThrows(SecurityException.class, () -> 
                proxy.generarReporteIngresosGastos(11, 2025)
            );
            assertTrue(exception.getMessage().contains("Acceso denegado"));
        }
    }

    @SuppressWarnings("unused")
    @Nested
    @DisplayName("RF4: Bloqueo de Usuario CLIENTE")
    class BloqueoClienteTests {

        @Test
        @DisplayName("CLIENTE no puede acceder a reportes")
        void cliente_noPuedeAccederReportes() {
            // Arrange
            ReporteFinanciero proxy = new ProxyReporteFinanciero(usuarioCliente);

            // Act & Assert
            SecurityException exception = assertThrows(SecurityException.class, () -> 
                proxy.generarReporteVentas(fechaInicio, fechaFin)
            );
            
            assertTrue(exception.getMessage().contains("Acceso denegado"));
        }
    }

    @SuppressWarnings("unused")
    @Nested
    @DisplayName("RF3: Validación de Credenciales")
    class ValidacionCredencialesTests {

        @Test
        @DisplayName("Usuario null no puede acceder")
        void usuarioNull_noPuedeAcceder() {
            // Arrange
            ReporteFinanciero proxy = new ProxyReporteFinanciero(null);

            // Act & Assert
            SecurityException exception = assertThrows(SecurityException.class, () -> 
                proxy.generarReporteVentas(fechaInicio, fechaFin)
            );
            
            assertTrue(exception.getMessage().contains("no autenticado"));
        }

        @Test
        @DisplayName("Usuario inactivo no puede acceder aunque sea GERENTE")
        void usuarioInactivo_noPuedeAcceder() {
            // Arrange
            ReporteFinanciero proxy = new ProxyReporteFinanciero(usuarioInactivo);

            // Act & Assert
            SecurityException exception = assertThrows(SecurityException.class, () -> 
                proxy.generarReporteVentas(fechaInicio, fechaFin)
            );
            
            assertTrue(exception.getMessage().contains("inactivo"));
        }
    }

    @SuppressWarnings("unused")
    @Nested
    @DisplayName("Tests de Auditoría")
    class AuditoriaTests {

        @Test
        @DisplayName("Reporte incluye información del usuario que lo generó")
        void reporte_incluyeInformacionAuditoria() {
            // Arrange
            ReporteFinanciero proxy = new ProxyReporteFinanciero(usuarioGerente);

            // Act
            Map<String, Object> reporte = proxy.generarReporteVentas(fechaInicio, fechaFin);

            // Assert
            assertEquals("gerente01", reporte.get("generadoPor"));
            assertNotNull(reporte.get("rolesUsuario"));
            assertTrue(reporte.get("rolesUsuario").toString().contains("GERENTE"));
        }
    }
}
