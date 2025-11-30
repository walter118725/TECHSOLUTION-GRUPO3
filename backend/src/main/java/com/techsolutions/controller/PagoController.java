package com.techsolutions.controller;

import com.techsolutions.service.PagoService;
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
public class PagoController {
    
    @Autowired
    private PagoService pagoService;
    
    /**
     * RF1: Lista todas las pasarelas de pago disponibles
     * GET /api/pagos/pasarelas
     */
    @GetMapping("/pasarelas")
    public ResponseEntity<List<Map<String, Object>>> listarPasarelas() {
        return ResponseEntity.ok(pagoService.listarPasarelas());
    }
    
    /**
     * RF1: Procesa un pago usando una pasarela específica
     * POST /api/pagos/procesar
     * Body: {
     *   "pasarela": "paypal" | "yape" | "plin",
     *   "monto": 100.50,
     *   "referencia": "REF-001"
     * }
     */
    @PostMapping("/procesar")
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
    public ResponseEntity<Map<String, Object>> configurarPasarela(
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
    public ResponseEntity<Map<String, Boolean>> obtenerEstadoPasarelas() {
        return ResponseEntity.ok(pagoService.obtenerEstadoPasarelas());
    }
    
    /**
     * Verifica el estado de una transacción
     * GET /api/pagos/verificar/{pasarela}/{referencia}
     */
    @GetMapping("/verificar/{pasarela}/{referencia}")
    public ResponseEntity<Map<String, String>> verificarEstado(
            @PathVariable String pasarela,
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
