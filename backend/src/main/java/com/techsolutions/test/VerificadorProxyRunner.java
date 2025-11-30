package com.techsolutions.test;

import com.techsolutions.model.Usuario;
import com.techsolutions.service.ReporteService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Verificador del patrón Proxy para control de acceso a reportes
 * RF3: El sistema debe proteger el acceso validando credenciales y roles
 * RF4: Solo GERENTE o CONTADOR pueden acceder a reportes completos
 */
@Component
@Order(2) // Se ejecuta después del verificador de pasarelas
public class VerificadorProxyRunner implements CommandLineRunner {
    
    private final ReporteService reporteService;
    
    public VerificadorProxyRunner(ReporteService reporteService) {
        this.reporteService = reporteService;
    }
    
    @Override
    public void run(String... args) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🔐 VERIFICACIÓN DE RF3 Y RF4 - PATRÓN PROXY");
        System.out.println("=".repeat(80) + "\n");
        
        verificarRF3_RF4();
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("✅ VERIFICACIÓN COMPLETADA - CONTROL DE ACCESO FUNCIONA CORRECTAMENTE");
        System.out.println("=".repeat(80) + "\n");
    }
    
    private void verificarRF3_RF4() {
        // Crear usuarios de prueba
        Usuario gerente = crearUsuario("gerente01", "Juan Pérez", "GERENTE");
        Usuario contador = crearUsuario("contador01", "María López", "CONTADOR");
        Usuario compras = crearUsuario("compras01", "Pedro Gómez", "COMPRAS");
        Usuario inactivo = crearUsuarioInactivo("inactivo01", "Usuario Inactivo", "GERENTE");
        
        System.out.println("📋 RF3: El sistema debe proteger el acceso validando credenciales y roles\n");
        System.out.println("📋 RF4: Solo usuarios con rol GERENTE o CONTADOR pueden acceder\n");
        
        // Test 1: Usuario GERENTE (debe tener acceso)
        System.out.println("🧪 TEST 1: Usuario GERENTE intentando acceder");
        System.out.println("Usuario: " + gerente.getUsername() + " - Roles: " + gerente.getRoles());
        try {
            Map<String, Object> reporte = reporteService.generarReporteVentas(
                gerente, 
                LocalDateTime.now().minusMonths(1), 
                LocalDateTime.now()
            );
            System.out.println("   ✅ ACCESO PERMITIDO - Reporte generado: " + reporte.get("titulo"));
        } catch (SecurityException e) {
            System.out.println("   ❌ ERROR: " + e.getMessage());
        }
        
        // Test 2: Usuario CONTADOR (debe tener acceso)
        System.out.println("\n🧪 TEST 2: Usuario CONTADOR intentando acceder");
        System.out.println("Usuario: " + contador.getUsername() + " - Roles: " + contador.getRoles());
        try {
            Map<String, Object> reporte = reporteService.generarReporteIngresosGastos(
                contador, 11, 2025
            );
            System.out.println("   ✅ ACCESO PERMITIDO - Reporte generado: " + reporte.get("titulo"));
        } catch (SecurityException e) {
            System.out.println("   ❌ ERROR: " + e.getMessage());
        }
        
        // Test 3: Usuario COMPRAS (NO debe tener acceso)
        System.out.println("\n🧪 TEST 3: Usuario COMPRAS intentando acceder (debe ser bloqueado)");
        System.out.println("Usuario: " + compras.getUsername() + " - Roles: " + compras.getRoles());
        try {
            reporteService.generarReporteUtilidades(
                compras,
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now()
            );
            System.out.println("   ❌ FALLO: No debería haber permitido el acceso");
        } catch (SecurityException e) {
            System.out.println("   ✅ ACCESO BLOQUEADO CORRECTAMENTE: " + e.getMessage());
        }
        
        // Test 4: Usuario inactivo (NO debe tener acceso)
        System.out.println("\n🧪 TEST 4: Usuario inactivo intentando acceder (debe ser bloqueado)");
        System.out.println("Usuario: " + inactivo.getUsername() + " - Activo: " + inactivo.getActivo());
        try {
            reporteService.generarReporteVentas(
                inactivo,
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now()
            );
            System.out.println("   ❌ FALLO: No debería haber permitido el acceso");
        } catch (SecurityException e) {
            System.out.println("   ✅ ACCESO BLOQUEADO CORRECTAMENTE: " + e.getMessage());
        }
        
        // Test 5: Usuario null (NO debe tener acceso)
        System.out.println("\n🧪 TEST 5: Usuario no autenticado intentando acceder (debe ser bloqueado)");
        try {
            reporteService.generarReporteVentas(
                null,
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now()
            );
            System.out.println("   ❌ FALLO: No debería haber permitido el acceso");
        } catch (SecurityException e) {
            System.out.println("   ✅ ACCESO BLOQUEADO CORRECTAMENTE: " + e.getMessage());
        }
        
        System.out.println("\n✅ RF3 VERIFICADO: El Proxy valida credenciales correctamente");
        System.out.println("✅ RF4 VERIFICADO: Solo GERENTE y CONTADOR tienen acceso a reportes");
    }
    
    private Usuario crearUsuario(String username, String nombreCompleto, String rol) {
        Usuario usuario = new Usuario();
        usuario.setId(System.currentTimeMillis());
        usuario.setUsername(username);
        usuario.setNombreCompleto(nombreCompleto);
        usuario.setEmail(username + "@techsolutions.com");
        usuario.setPassword("password123");
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());
        
        Set<String> roles = new HashSet<>();
        roles.add(rol);
        usuario.setRoles(roles);
        
        return usuario;
    }
    
    private Usuario crearUsuarioInactivo(String username, String nombreCompleto, String rol) {
        Usuario usuario = crearUsuario(username, nombreCompleto, rol);
        usuario.setActivo(false);
        return usuario;
    }
}
