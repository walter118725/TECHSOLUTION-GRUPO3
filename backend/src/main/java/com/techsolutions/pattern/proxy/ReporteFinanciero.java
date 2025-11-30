package com.techsolutions.pattern.proxy;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Interfaz para acceder a reportes financieros
 * Patrón Proxy: Define el contrato que tanto el RealSubject como el Proxy deben cumplir
 */
public interface ReporteFinanciero {
    
    /**
     * Genera un reporte de ventas
     * @param fechaInicio Fecha inicial del periodo
     * @param fechaFin Fecha final del periodo
     * @return Datos del reporte
     */
    Map<String, Object> generarReporteVentas(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    /**
     * Genera un reporte de ingresos y gastos
     * @param mes Mes del reporte
     * @param anio Año del reporte
     * @return Datos del reporte
     */
    Map<String, Object> generarReporteIngresosGastos(int mes, int anio);
    
    /**
     * Genera un reporte de utilidades
     * @param fechaInicio Fecha inicial del periodo
     * @param fechaFin Fecha final del periodo
     * @return Datos del reporte
     */
    Map<String, Object> generarReporteUtilidades(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    /**
     * Exporta un reporte a PDF
     * @param tipoReporte Tipo de reporte a exportar
     * @param datos Datos del reporte
     * @return Ruta del archivo generado
     */
    String exportarAPDF(String tipoReporte, Map<String, Object> datos);
}
