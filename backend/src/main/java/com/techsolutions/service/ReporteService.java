package com.techsolutions.service;

import com.techsolutions.model.Usuario;
import com.techsolutions.pattern.proxy.ProxyReporteFinanciero;
import com.techsolutions.pattern.proxy.ReporteFinanciero;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Servicio que gestiona los reportes financieros con control de acceso
 * GRASP: Controller - Coordina la generación de reportes
 * RF3-RF4: Utiliza el Proxy para validar acceso antes de generar reportes
 */
@Service
public class ReporteService {
    
    /**
     * RF3-RF4: Crea un proxy que valida el acceso según el usuario
     * El Proxy protege el acceso validando roles (GERENTE o CONTADOR)
     */
    public ReporteFinanciero obtenerReporteConProxy(Usuario usuario) {
        return new ProxyReporteFinanciero(usuario);
    }
    
    /**
     * Genera un reporte de ventas para el usuario especificado
     * @param usuario Usuario que solicita el reporte
     * @param fechaInicio Fecha inicial del periodo
     * @param fechaFin Fecha final del periodo
     * @return Datos del reporte
     * @throws SecurityException si el usuario no tiene permisos
     */
    public Map<String, Object> generarReporteVentas(Usuario usuario, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        ReporteFinanciero reporte = obtenerReporteConProxy(usuario);
        return reporte.generarReporteVentas(fechaInicio, fechaFin);
    }
    
    /**
     * Genera un reporte de ingresos y gastos para el usuario especificado
     * @param usuario Usuario que solicita el reporte
     * @param mes Mes del reporte
     * @param anio Año del reporte
     * @return Datos del reporte
     * @throws SecurityException si el usuario no tiene permisos
     */
    public Map<String, Object> generarReporteIngresosGastos(Usuario usuario, int mes, int anio) {
        ReporteFinanciero reporte = obtenerReporteConProxy(usuario);
        return reporte.generarReporteIngresosGastos(mes, anio);
    }
    
    /**
     * Genera un reporte de utilidades para el usuario especificado
     * @param usuario Usuario que solicita el reporte
     * @param fechaInicio Fecha inicial del periodo
     * @param fechaFin Fecha final del periodo
     * @return Datos del reporte
     * @throws SecurityException si el usuario no tiene permisos
     */
    public Map<String, Object> generarReporteUtilidades(Usuario usuario, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        ReporteFinanciero reporte = obtenerReporteConProxy(usuario);
        return reporte.generarReporteUtilidades(fechaInicio, fechaFin);
    }
    
    /**
     * Exporta un reporte a PDF para el usuario especificado
     * @param usuario Usuario que solicita la exportación
     * @param tipoReporte Tipo de reporte a exportar
     * @param datos Datos del reporte
     * @return Ruta del archivo generado
     * @throws SecurityException si el usuario no tiene permisos
     */
    public String exportarAPDF(Usuario usuario, String tipoReporte, Map<String, Object> datos) {
        ReporteFinanciero reporte = obtenerReporteConProxy(usuario);
        return reporte.exportarAPDF(tipoReporte, datos);
    }
}
