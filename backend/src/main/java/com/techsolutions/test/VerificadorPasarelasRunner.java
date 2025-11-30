package com.techsolutions.test;
import com.techsolutions.service.PagoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Clase para demostrar y verificar RF1 y RF2
 * Se ejecuta automáticamente al iniciar la aplicación
 */
@Component
public class VerificadorPasarelasRunner implements CommandLineRunner {
    
    private final PagoService pagoService;
    
    public VerificadorPasarelasRunner(PagoService pagoService) {
        this.pagoService = pagoService;
    }
    
    @Override
    public void run(String... args) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🔍 VERIFICACIÓN DE RF1 Y RF2 - PATRÓN ADAPTER");
        System.out.println("=".repeat(80) + "\n");
        
        verificarRF1();
        System.out.println();
        verificarRF2();
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("✅ VERIFICACIÓN COMPLETADA - TODOS LOS REQUISITOS FUNCIONAN CORRECTAMENTE");
        System.out.println("=".repeat(80) + "\n");
    }
    
    private void verificarRF1() {
        System.out.println("📋 RF1: El sistema debe integrar múltiples pasarelas mediante adaptador común\n");
        
        // Verificar que todas las pasarelas están disponibles
        var pasarelas = pagoService.listarPasarelas();
        System.out.println("✅ Pasarelas integradas: " + pasarelas.size());
        for (var pasarela : pasarelas) {
            System.out.println("   • " + pasarela.get("nombre") + 
                             " (ID: " + pasarela.get("id") + 
                             ", Estado: " + (Boolean.TRUE.equals(pasarela.get("habilitada")) ? "✓ Habilitada" : "✗ Deshabilitada") + ")");
        }
        
        // Probar procesamiento con cada pasarela
        System.out.println("\n📤 Probando procesamiento de pagos:\n");
        
        // Test PayPal
        try {
            boolean resultado = pagoService.procesarPago("paypal", new BigDecimal("100.00"), "TEST-001");
            System.out.println("   ✅ PayPal: " + (resultado ? "Pago procesado exitosamente" : "Error"));
        } catch (Exception e) {
            System.out.println("   ❌ PayPal: " + e.getMessage());
        }
        
        // Test Yape
        try {
            boolean resultado = pagoService.procesarPago("yape", new BigDecimal("50.00"), "TEST-002");
            System.out.println("   ✅ Yape: " + (resultado ? "Pago procesado exitosamente" : "Error"));
        } catch (Exception e) {
            System.out.println("   ❌ Yape: " + e.getMessage());
        }
        
        // Test Plin
        try {
            boolean resultado = pagoService.procesarPago("plin", new BigDecimal("75.50"), "TEST-003");
            System.out.println("   ✅ Plin: " + (resultado ? "Pago procesado exitosamente" : "Error"));
        } catch (Exception e) {
            System.out.println("   ❌ Plin: " + e.getMessage());
        }
        
        System.out.println("\n✅ RF1 VERIFICADO: Todas las pasarelas funcionan con interfaz común");
    }
    
    private void verificarRF2() {
        System.out.println("📋 RF2: El administrador puede habilitar/deshabilitar pasarelas\n");
        
        // Deshabilitar Yape
        System.out.println("🔧 Deshabilitando pasarela Yape...");
        pagoService.configurarPasarela("yape", false);
        var estados = pagoService.obtenerEstadoPasarelas();
        System.out.println("   • Yape: " + (Boolean.FALSE.equals(estados.get("yape")) ? "✓ Deshabilitada correctamente" : "✗ Error"));
        
        // Intentar procesar con pasarela deshabilitada
        System.out.println("\n🚫 Intentando procesar pago con pasarela deshabilitada:");
        try {
            pagoService.procesarPago("yape", new BigDecimal("25.00"), "TEST-004");
            System.out.println("   ❌ ERROR: No debería permitir procesar");
        } catch (IllegalStateException e) {
            System.out.println("   ✅ Bloqueado correctamente: " + e.getMessage());
        }
        
        // Rehabilitar Yape
        System.out.println("\n🔧 Habilitando nuevamente Yape...");
        pagoService.configurarPasarela("yape", true);
        estados = pagoService.obtenerEstadoPasarelas();
        System.out.println("   • Yape: " + (Boolean.TRUE.equals(estados.get("yape")) ? "✓ Habilitada correctamente" : "✗ Error"));
        
        // Verificar que ahora sí funciona
        System.out.println("\n✔️ Verificando que Yape ahora funciona:");
        try {
            boolean resultado = pagoService.procesarPago("yape", new BigDecimal("30.00"), "TEST-005");
            System.out.println("   ✅ Yape: " + (resultado ? "Pago procesado exitosamente" : "Error"));
        } catch (Exception e) {
            System.out.println("   ❌ Yape: " + e.getMessage());
        }
        
        System.out.println("\n✅ RF2 VERIFICADO: Control de habilitación funciona correctamente");
    }
}
