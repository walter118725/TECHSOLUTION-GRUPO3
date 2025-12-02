package com.techsolutions.controller;

import com.techsolutions.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar pagos
 * RF1: Integración de múltiples pasarelas de pago
 * RF2: Habilitar/deshabilitar pasarelas desde panel de configuración
 */
@RestController
@RequestMapping("/api/pagos")
@Tag(name = "Pagos", description = "💳 API para procesamiento de pagos - Implementa Patrón Adapter para múltiples pasarelas (PayPal, Yape, Plin)")
public class PagoController {
    
    @Autowired
    private PagoService pagoService;
    
    /**
     * RF1: Lista todas las pasarelas de pago disponibles
     * GET /api/pagos/pasarelas
     */
    @GetMapping("/pasarelas")
    @Operation(
        summary = "📋 Listar pasarelas disponibles (RF1)",
        description = """
            Obtiene todas las pasarelas de pago integradas en el sistema.
            **RF1**: El sistema debe integrar múltiples pasarelas de pago mediante un adaptador común.
            
            Pasarelas disponibles:
            - **PayPal**: Pagos internacionales con tarjeta
            - **Yape**: Billetera móvil BCP (Perú)
            - **Plin**: Billetera móvil interbancaria (Perú)
            """
    )
    @ApiResponse(responseCode = "200", description = "✅ Lista de pasarelas obtenida",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                [
                  {
                    "id": "paypal",
                    "nombre": "PayPal",
                    "habilitada": true,
                    "descripcion": "Pagos internacionales"
                  },
                  {
                    "id": "yape",
                    "nombre": "Yape",
                    "habilitada": true,
                    "descripcion": "Billetera móvil BCP"
                  },
                  {
                    "id": "plin",
                    "nombre": "Plin",
                    "habilitada": true,
                    "descripcion": "Billetera interbancaria"
                  }
                ]
                """)))
    public ResponseEntity<List<Map<String, Object>>> listarPasarelas() {
        return ResponseEntity.ok(pagoService.listarPasarelas());
    }
    
    /**
     * RF1: Procesa un pago usando una pasarela específica
     * POST /api/pagos/procesar
     */
    @PostMapping("/procesar")
    @Operation(
        summary = "💰 Procesar pago (RF1 - Patrón Adapter)",
        description = """
            Procesa un pago usando la pasarela especificada.
            **RF1**: El sistema integra múltiples pasarelas mediante un adaptador común.
            
            El Patrón Adapter permite que todas las pasarelas implementen la misma interfaz,
            facilitando el procesamiento uniforme independientemente del proveedor.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "✅ Pago procesado exitosamente",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "exitoso": true,
                      "mensaje": "Pago procesado correctamente",
                      "pasarela": "yape",
                      "monto": 150.50,
                      "referencia": "ORD-2024-001"
                    }
                    """))),
        @ApiResponse(responseCode = "400", description = "❌ Error al procesar el pago",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "exitoso": false,
                      "mensaje": "La pasarela yape no está habilitada"
                    }
                    """)))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos del pago a procesar",
        required = true,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Pago con Yape", value = """
                    {
                      "pasarela": "yape",
                      "monto": 150.50,
                      "referencia": "ORD-2024-001"
                    }
                    """),
                @ExampleObject(name = "Pago con PayPal", value = """
                    {
                      "pasarela": "paypal",
                      "monto": 299.99,
                      "referencia": "ORD-2024-002"
                    }
                    """),
                @ExampleObject(name = "Pago con Plin", value = """
                    {
                      "pasarela": "plin",
                      "monto": 75.00,
                      "referencia": "ORD-2024-003"
                    }
                    """)
            }))
    public ResponseEntity<Map<String, Object>> procesarPago(@RequestBody Map<String, Object> request) {
        try {
            // Aceptar tanto "pasarela" como "pasarelaId"
            String pasarela = (String) request.getOrDefault("pasarela", request.get("pasarelaId"));
            BigDecimal monto = new BigDecimal(request.get("monto").toString());
            String referencia = (String) request.get("referencia");
            
            boolean exitoso = pagoService.procesarPago(pasarela, monto, referencia);
            
            Map<String, Object> response = new HashMap<>();
            response.put("exitoso", exitoso);
            response.put("mensaje", exitoso ? "Pago procesado correctamente" : "Error al procesar el pago");
            response.put("pasarela", pasarela);
            response.put("monto", monto);
            response.put("referencia", referencia);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("exitoso", false);
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * RF2: Habilita o deshabilita una pasarela de pago
     * PUT /api/pagos/pasarelas/{nombre}/configurar
     * Body: {
     *   "habilitar": true | false
     * }
     */
    @PutMapping("/pasarelas/{nombre}/configurar")
    @Operation(
        summary = "⚙️ Configurar pasarela (RF2)",
        description = """
            Habilita o deshabilita una pasarela de pago.
            **RF2**: El administrador puede habilitar/deshabilitar pasarelas desde el panel de configuración.
            
            Una pasarela deshabilitada no procesará pagos y mostrará un mensaje de error.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "✅ Pasarela configurada exitosamente",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "exitoso": true,
                      "mensaje": "Pasarela yape deshabilitada",
                      "pasarela": "yape",
                      "habilitada": false
                    }
                    """))),
        @ApiResponse(responseCode = "400", description = "❌ Error al configurar pasarela")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Estado de habilitación",
        required = true,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Deshabilitar", value = """
                    {
                      "habilitar": false
                    }
                    """),
                @ExampleObject(name = "Habilitar", value = """
                    {
                      "habilitar": true
                    }
                    """)
            }))
    public ResponseEntity<Map<String, Object>> configurarPasarela(
            @Parameter(description = "Nombre de la pasarela", example = "yape", 
                       schema = @Schema(allowableValues = {"paypal", "yape", "plin"}))
            @PathVariable String nombre,
            @RequestBody Map<String, Boolean> request) {
        try {
            boolean habilitar = request.get("habilitar");
            pagoService.configurarPasarela(nombre, habilitar);
            
            Map<String, Object> response = new HashMap<>();
            response.put("exitoso", true);
            response.put("mensaje", "Pasarela " + nombre + (habilitar ? " habilitada" : " deshabilitada"));
            response.put("pasarela", nombre);
            response.put("habilitada", habilitar);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("exitoso", false);
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * RF2: Obtiene el estado actual de todas las pasarelas
     * GET /api/pagos/pasarelas/estado
     */
    @GetMapping("/pasarelas/estado")
    @Operation(
        summary = "📊 Estado de pasarelas (RF2)",
        description = "Obtiene el estado de habilitación de todas las pasarelas de pago"
    )
    @ApiResponse(responseCode = "200", description = "✅ Estado obtenido",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "paypal": true,
                  "yape": true,
                  "plin": false
                }
                """)))
    public ResponseEntity<Map<String, Boolean>> obtenerEstadoPasarelas() {
        return ResponseEntity.ok(pagoService.obtenerEstadoPasarelas());
    }
    
    /**
     * Verifica el estado de una transacción
     * GET /api/pagos/verificar/{pasarela}/{referencia}
     */
    @GetMapping("/verificar/{pasarela}/{referencia}")
    @Operation(
        summary = "🔍 Verificar transacción",
        description = "Verifica el estado de una transacción por su referencia y pasarela"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "✅ Estado de transacción",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "referencia": "ORD-2024-001",
                      "pasarela": "yape",
                      "estado": "COMPLETADO"
                    }
                    """))),
        @ApiResponse(responseCode = "400", description = "❌ Error al verificar")
    })
    public ResponseEntity<Map<String, String>> verificarEstado(
            @Parameter(description = "Pasarela de pago", example = "yape")
            @PathVariable String pasarela,
            @Parameter(description = "Referencia de la transacción", example = "ORD-2024-001")
            @PathVariable String referencia) {
        try {
            String estado = pagoService.verificarEstadoTransaccion(pasarela, referencia);
            
            Map<String, String> response = new HashMap<>();
            response.put("referencia", referencia);
            response.put("pasarela", pasarela);
            response.put("estado", estado);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
