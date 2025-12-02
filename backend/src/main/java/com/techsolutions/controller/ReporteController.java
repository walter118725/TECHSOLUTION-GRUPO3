package com.techsolutions.controller;

import com.techsolutions.model.Usuario;
import com.techsolutions.pattern.proxy.ReporteFinanciero;
import com.techsolutions.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * Controlador REST para acceder a reportes financieros
 * Utiliza el patrón Proxy para validar credenciales y roles
 */
@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "📊 API de Reportes Financieros - Implementa Patrón Proxy para control de acceso por roles (RF3, RF4)")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    /**
     * Genera reporte de ventas
     * Solo accesible para usuarios con rol GERENTE
     */
    @Operation(
        summary = "📈 Generar reporte de ventas (RF3, RF4)",
        description = """
            Genera un reporte detallado de ventas del último mes.
            
            **RF3**: El sistema protege el acceso validando credenciales y roles.
            **RF4**: Solo usuarios con rol GERENTE pueden acceder.
            
            El **Patrón Proxy** intercepta la solicitud y valida los permisos antes
            de permitir el acceso al reporte real.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "✅ Reporte generado exitosamente",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "exitoso": true,
                      "mensaje": "✅ Reporte de ventas generado exitosamente",
                      "usuario": "gerente",
                      "roles": ["GERENTE"],
                      "periodo": "Último mes",
                      "datos": {
                        "totalVentas": 15,
                        "montoTotal": 25430.50,
                        "promedioVenta": 1695.37
                      }
                    }
                    """))),
        @ApiResponse(responseCode = "403", description = "❌ Acceso denegado - Usuario sin permisos",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "exitoso": false,
                      "mensaje": "❌ Acceso denegado: Solo usuarios con rol de GERENTE pueden acceder",
                      "usuario": "cliente"
                    }
                    """))),
        @ApiResponse(responseCode = "500", description = "❌ Error interno del servidor")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos del usuario solicitante",
        required = true,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Usuario GERENTE (✅ Acceso permitido)", value = """
                    {
                      "username": "gerente",
                      "activo": true,
                      "roles": ["GERENTE"]
                    }
                    """),
                @ExampleObject(name = "Usuario CLIENTE (❌ Acceso denegado)", value = """
                    {
                      "username": "cliente",
                      "activo": true,
                      "roles": ["CLIENTE"]
                    }
                    """)
            }))
    @PostMapping("/ventas")
    public ResponseEntity<?> generarReporteVentas(
            @RequestBody UsuarioRequest request) {
        try {
            // Crear usuario desde la petición
            Usuario usuario = crearUsuarioDesdeRequest(request);
            
            // Obtener proxy con validación de acceso
            ReporteFinanciero reporte = reporteService.obtenerReporteConProxy(usuario);
            
            // Generar reporte (el proxy valida el acceso)
            java.time.LocalDateTime fechaInicio = java.time.LocalDateTime.now().minusMonths(1);
            java.time.LocalDateTime fechaFin = java.time.LocalDateTime.now();
            
            Map<String, Object> resultado = reporte.generarReporteVentas(fechaInicio, fechaFin);
            
            return ResponseEntity.ok(Map.of(
                "exitoso", true,
                "mensaje", "✅ Reporte de ventas generado exitosamente",
                "usuario", usuario.getUsername(),
                "roles", usuario.getRoles(),
                "periodo", "Último mes",
                "datos", resultado
            ));
            
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "exitoso", false,
                "mensaje", "❌ Acceso denegado: " + e.getMessage(),
                "usuario", request.getUsername()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "exitoso", false,
                "mensaje", "❌ Error al generar reporte: " + e.getMessage()
            ));
        }
    }

    /**
     * Genera reporte de ingresos y gastos
     * Solo accesible para usuarios con rol GERENTE
     */
    @Operation(
        summary = "💰 Generar reporte de ingresos y gastos (RF3, RF4)",
        description = """
            Genera un reporte de ingresos y gastos para un mes y año específico.
            
            **RF3**: El sistema protege el acceso validando credenciales y roles.
            **RF4**: Solo usuarios con rol GERENTE pueden acceder.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "✅ Reporte generado exitosamente",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "exitoso": true,
                      "mensaje": "Reporte de ingresos y gastos generado exitosamente",
                      "usuario": "gerente",
                      "roles": ["GERENTE"],
                      "datos": {
                        "mes": 11,
                        "anio": 2024,
                        "ingresos": 45000.00,
                        "gastos": 28500.00,
                        "balance": 16500.00
                      }
                    }
                    """))),
        @ApiResponse(responseCode = "403", description = "❌ Acceso denegado"),
        @ApiResponse(responseCode = "500", description = "❌ Error interno")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos del usuario con mes y año",
        required = true,
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "username": "gerente",
                  "activo": true,
                  "roles": ["GERENTE"],
                  "mes": 11,
                  "anio": 2024
                }
                """)))
    @PostMapping("/ingresos-gastos")
    public ResponseEntity<?> generarReporteIngresosGastos(
            @RequestBody UsuarioRequest request) {
        try {
            Usuario usuario = crearUsuarioDesdeRequest(request);
            ReporteFinanciero reporte = reporteService.obtenerReporteConProxy(usuario);
            
            Map<String, Object> resultado = reporte.generarReporteIngresosGastos(
                request.getMes(), 
                request.getAnio()
            );
            
            return ResponseEntity.ok(Map.of(
                "exitoso", true,
                "mensaje", "Reporte de ingresos y gastos generado exitosamente",
                "usuario", usuario.getUsername(),
                "roles", usuario.getRoles(),
                "datos", resultado
            ));
            
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "exitoso", false,
                "mensaje", "Acceso denegado: " + e.getMessage(),
                "usuario", request.getUsername()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "exitoso", false,
                "mensaje", "Error al generar reporte: " + e.getMessage()
            ));
        }
    }

    /**
     * Genera reporte de utilidades
     * Solo accesible para usuarios con rol GERENTE
     */
    @Operation(
        summary = "📊 Generar reporte de utilidades (RF3, RF4)",
        description = """
            Genera un reporte de utilidades del último trimestre.
            
            **RF3**: El sistema protege el acceso validando credenciales y roles.
            **RF4**: Solo usuarios con rol GERENTE pueden acceder.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "✅ Reporte generado exitosamente",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "exitoso": true,
                      "mensaje": "✅ Reporte de utilidades generado exitosamente",
                      "usuario": "gerente",
                      "roles": ["GERENTE"],
                      "periodo": "Último trimestre",
                      "datos": {
                        "utilidadBruta": 75000.00,
                        "utilidadNeta": 52500.00,
                        "margenUtilidad": "70%"
                      }
                    }
                    """))),
        @ApiResponse(responseCode = "403", description = "❌ Acceso denegado"),
        @ApiResponse(responseCode = "500", description = "❌ Error interno")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos del usuario solicitante",
        required = true,
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "username": "gerente",
                  "activo": true,
                  "roles": ["GERENTE"]
                }
                """)))
    @PostMapping("/utilidades")
    public ResponseEntity<?> generarReporteUtilidades(
            @RequestBody UsuarioRequest request) {
        try {
            Usuario usuario = crearUsuarioDesdeRequest(request);
            ReporteFinanciero reporte = reporteService.obtenerReporteConProxy(usuario);
            
            java.time.LocalDateTime periodoInicio = java.time.LocalDateTime.now().minusMonths(3);
            java.time.LocalDateTime periodoFin = java.time.LocalDateTime.now();
            
            Map<String, Object> resultado = reporte.generarReporteUtilidades(periodoInicio, periodoFin);
            
            return ResponseEntity.ok(Map.of(
                "exitoso", true,
                "mensaje", "✅ Reporte de utilidades generado exitosamente",
                "usuario", usuario.getUsername(),
                "roles", usuario.getRoles(),
                "periodo", "Último trimestre",
                "datos", resultado
            ));
            
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "exitoso", false,
                "mensaje", "❌ Acceso denegado: " + e.getMessage(),
                "usuario", request.getUsername()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "exitoso", false,
                "mensaje", "❌ Error al generar reporte: " + e.getMessage()
            ));
        }
    }

    /**
     * Exporta reporte a PDF
     * Solo accesible para usuarios con rol GERENTE
     */
    @Operation(
        summary = "📄 Exportar reporte a PDF (RF3, RF4)",
        description = """
            Exporta un reporte financiero a formato PDF.
            
            **RF3**: El sistema protege el acceso validando credenciales y roles.
            **RF4**: Solo usuarios con rol GERENTE pueden exportar.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "✅ PDF exportado exitosamente",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "exitoso": true,
                      "mensaje": "✅ Reporte exportado a PDF exitosamente",
                      "usuario": "gerente",
                      "roles": ["GERENTE"],
                      "rutaPDF": "/reportes/reporte_ventas_noviembre.pdf"
                    }
                    """))),
        @ApiResponse(responseCode = "403", description = "❌ Acceso denegado"),
        @ApiResponse(responseCode = "500", description = "❌ Error al exportar")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos del usuario y nombre de archivo",
        required = true,
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "username": "gerente",
                  "activo": true,
                  "roles": ["GERENTE"],
                  "nombreArchivo": "reporte_ventas_noviembre"
                }
                """)))
    @PostMapping("/exportar-pdf")
    public ResponseEntity<?> exportarAPDF(
            @RequestBody UsuarioRequest request) {
        try {
            Usuario usuario = crearUsuarioDesdeRequest(request);
            ReporteFinanciero reporte = reporteService.obtenerReporteConProxy(usuario);
            
            // Generar datos del reporte para exportar
            java.time.LocalDateTime fechaInicio = java.time.LocalDateTime.now().minusMonths(1);
            java.time.LocalDateTime fechaFin = java.time.LocalDateTime.now();
            Map<String, Object> datosReporte = reporte.generarReporteVentas(fechaInicio, fechaFin);
            
            String nombreArchivo = request.getNombreArchivo() != null ? 
                request.getNombreArchivo() : "reporte_financiero";
            String rutaPDF = reporte.exportarAPDF(nombreArchivo, datosReporte);
            
            return ResponseEntity.ok(Map.of(
                "exitoso", true,
                "mensaje", "✅ Reporte exportado a PDF exitosamente",
                "usuario", usuario.getUsername(),
                "roles", usuario.getRoles(),
                "rutaPDF", rutaPDF
            ));
            
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "exitoso", false,
                "mensaje", "❌ Acceso denegado: " + e.getMessage(),
                "usuario", request.getUsername()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "exitoso", false,
                "mensaje", "❌ Error al exportar reporte: " + e.getMessage()
            ));
        }
    }

    /**
     * Crea un objeto Usuario desde los datos de la petición
     */
    private Usuario crearUsuarioDesdeRequest(UsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setActivo(request.isActivo());
        
        // Agregar roles
        if (request.getRoles() != null) {
            usuario.getRoles().addAll(Arrays.asList(request.getRoles()));
        }
        
        return usuario;
    }

    /**
     * Clase interna para recibir datos del usuario en las peticiones
     */
    public static class UsuarioRequest {
        private String username;
        private boolean activo = true;
        private String[] roles;
        private String fechaInicio;
        private String fechaFin;
        private int mes;
        private int anio;
        private String periodo;
        private String nombreArchivo;

        // Getters y Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public boolean isActivo() {
            return activo;
        }

        public void setActivo(boolean activo) {
            this.activo = activo;
        }

        public String[] getRoles() {
            return roles;
        }

        public void setRoles(String[] roles) {
            this.roles = roles;
        }

        public String getFechaInicio() {
            return fechaInicio;
        }

        public void setFechaInicio(String fechaInicio) {
            this.fechaInicio = fechaInicio;
        }

        public String getFechaFin() {
            return fechaFin;
        }

        public void setFechaFin(String fechaFin) {
            this.fechaFin = fechaFin;
        }

        public int getMes() {
            return mes;
        }

        public void setMes(int mes) {
            this.mes = mes;
        }

        public int getAnio() {
            return anio;
        }

        public void setAnio(int anio) {
            this.anio = anio;
        }

        public String getPeriodo() {
            return periodo;
        }

        public void setPeriodo(String periodo) {
            this.periodo = periodo;
        }

        public String getNombreArchivo() {
            return nombreArchivo;
        }

        public void setNombreArchivo(String nombreArchivo) {
            this.nombreArchivo = nombreArchivo;
        }
    }
}
