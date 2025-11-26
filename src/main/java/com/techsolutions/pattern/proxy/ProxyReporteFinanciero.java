package com.techsolutions.pattern.proxy;

import com.techsolutions.model.Usuario;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Proxy para controlar el acceso a reportes financieros
 * Patrón Proxy: Proxy - Controla el acceso al objeto real
 * RF3: Protege el acceso validando credenciales y roles
 * RF4: Solo GERENTE o CONTADOR pueden acceder a reportes completos
 * 
 * GRASP:
 * - Controller: Coordina la validación de seguridad
 * - Protected Variations: Protege al cliente de cambios en el control de acceso
 */
public class ProxyReporteFinanciero implements ReporteFinanciero {
    
    private ReporteFinancieroReal reporteReal;
    private Usuario usuarioActual;
    
    public ProxyReporteFinanciero(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        this.reporteReal = new ReporteFinancieroReal();
    }
    
    /**
     * RF3-RF4: Valida que el usuario tenga credenciales válidas y rol adecuado
     * @throws SecurityException si el usuario no tiene permisos
     */
    private void validarAcceso() {
        // RF3: Validar credenciales del usuario
        if (usuarioActual == null) {
            System.out.println("🚫 ACCESO DENEGADO: Usuario no autenticado");
            throw new SecurityException("Acceso denegado: Usuario no autenticado");
        }
        
        if (!usuarioActual.getActivo()) {
            System.out.println("🚫 ACCESO DENEGADO: Usuario inactivo");
            throw new SecurityException("Acceso denegado: Usuario inactivo");
        }
        
        // RF4: Validar que el usuario tenga rol de GERENTE o CONTADOR
        boolean tieneAcceso = usuarioActual.tieneRol("GERENTE") || 
                             usuarioActual.tieneRol("CONTADOR");
        
        if (!tieneAcceso) {
            String rolesUsuario = usuarioActual.getRoles().toString();
            System.out.println("🚫 ACCESO DENEGADO: Usuario '" + usuarioActual.getUsername() + 
                             "' con roles " + rolesUsuario + 
                             " no tiene permisos para acceder a reportes financieros");
            throw new SecurityException(
                "Acceso denegado: Solo usuarios con rol de GERENTE o CONTADOR " +
                "pueden acceder a reportes financieros. Sus roles actuales: " + rolesUsuario
            );
        }
        
        // Log de auditoría
        System.out.println("✅ ACCESO AUTORIZADO: Usuario '" + usuarioActual.getUsername() + 
                         "' con roles " + usuarioActual.getRoles() + 
                         " accediendo a reporte financiero");
    }
    
    @Override
    public Map<String, Object> generarReporteVentas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        System.out.println("\n🔐 Proxy: Validando acceso a Reporte de Ventas...");
        validarAcceso();
        
        // Si pasa la validación, delega al objeto real
        Map<String, Object> reporte = reporteReal.generarReporteVentas(fechaInicio, fechaFin);
        
        // Agregar información de auditoría
        reporte.put("generadoPor", usuarioActual.getUsername());
        reporte.put("rolesUsuario", usuarioActual.getRoles());
        
        return reporte;
    }
    
    @Override
    public Map<String, Object> generarReporteIngresosGastos(int mes, int anio) {
        System.out.println("\n🔐 Proxy: Validando acceso a Reporte de Ingresos y Gastos...");
        validarAcceso();
        
        Map<String, Object> reporte = reporteReal.generarReporteIngresosGastos(mes, anio);
        
        // Agregar información de auditoría
        reporte.put("generadoPor", usuarioActual.getUsername());
        reporte.put("rolesUsuario", usuarioActual.getRoles());
        
        return reporte;
    }
    
    @Override
    public Map<String, Object> generarReporteUtilidades(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        System.out.println("\n🔐 Proxy: Validando acceso a Reporte de Utilidades...");
        validarAcceso();
        
        Map<String, Object> reporte = reporteReal.generarReporteUtilidades(fechaInicio, fechaFin);
        
        // Agregar información de auditoría
        reporte.put("generadoPor", usuarioActual.getUsername());
        reporte.put("rolesUsuario", usuarioActual.getRoles());
        
        return reporte;
    }
    
    @Override
    public String exportarAPDF(String tipoReporte, Map<String, Object> datos) {
        System.out.println("\n🔐 Proxy: Validando acceso para exportar reporte...");
        validarAcceso();
        
        return reporteReal.exportarAPDF(tipoReporte, datos);
    }
}
