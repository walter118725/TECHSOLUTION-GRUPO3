package com.techsolutions.test;

import com.techsolutions.model.Usuario;
import com.techsolutions.pattern.observer.GestorInventarioObservable;
import com.techsolutions.pattern.observer.ObservadorUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Verificador del Patrón Observer para gestión de inventario
 * RF5: Notificaciones de stock bajo a usuarios con rol GERENTE y COMPRAS
 * Se ejecuta al iniciar la aplicación para registrar observadores
 */
@Component
@Order(3)
public class VerificadorObserverRunner implements CommandLineRunner {
    
    @Autowired
    private GestorInventarioObservable gestorInventario;
    
    @Override
    public void run(String... args) {
        System.out.println("\n================================================================================");
        System.out.println("📢 CONFIGURACIÓN DEL PATRÓN OBSERVER - GESTIÓN DE INVENTARIO");
        System.out.println("================================================================================\n");
        
        System.out.println("📋 RF5: Sistema envía notificaciones cuando stock cae por debajo del mínimo");
        System.out.println("📋 Solo usuarios con rol GERENTE y COMPRAS reciben notificaciones\n");
        
        // Crear usuarios observadores
        Usuario gerente = new Usuario();
        gerente.setUsername("gerente_inventario");
        gerente.setNombreCompleto("Juan Pérez - Gerente");
        gerente.setRoles(Set.of("GERENTE"));
        gerente.setActivo(true);
        
        Usuario compras = new Usuario();
        compras.setUsername("jefe_compras");
        compras.setNombreCompleto("María González - Jefe de Compras");
        compras.setRoles(Set.of("COMPRAS"));
        compras.setActivo(true);
        
        Usuario vendedor = new Usuario();
        vendedor.setUsername("vendedor01");
        vendedor.setNombreCompleto("Carlos Ramírez - Vendedor");
        vendedor.setRoles(Set.of("VENTAS"));
        vendedor.setActivo(true);
        
        // Registrar observadores
        System.out.println("🔔 Registrando observadores:");
        gestorInventario.agregarObservador(new ObservadorUsuario(gerente));
        gestorInventario.agregarObservador(new ObservadorUsuario(compras));
        gestorInventario.agregarObservador(new ObservadorUsuario(vendedor)); // Este NO recibirá notificaciones
        
        System.out.println("\n✅ Total de observadores registrados: " + gestorInventario.getObservadores().size());
        System.out.println("✅ Observadores activos para notificaciones de stock bajo:");
        System.out.println("   • GERENTE: " + gerente.getNombreCompleto());
        System.out.println("   • COMPRAS: " + compras.getNombreCompleto());
        System.out.println("   ⚠️  VENTAS: No recibirá notificaciones (rol no autorizado)\n");
        
        System.out.println("💡 Las notificaciones se enviarán automáticamente cuando:");
        System.out.println("   • El stock de un producto caiga por debajo del stock mínimo");
        System.out.println("   • Se configure un nuevo stock mínimo mayor al stock actual");
        System.out.println("   • Se realice una venta que reduzca el stock al nivel crítico\n");
        
        System.out.println("🌐 Endpoints disponibles:");
        System.out.println("   • POST /api/inventario/{id}/reducir - Reduce stock");
        System.out.println("   • POST /api/inventario/{id}/aumentar - Aumenta stock");
        System.out.println("   • PUT /api/inventario/{id}/stock-minimo - Configura stock mínimo");
        System.out.println("   • GET /api/inventario/{id} - Consulta estado\n");
        
        System.out.println("================================================================================");
        System.out.println("✅ PATRÓN OBSERVER CONFIGURADO - LISTO PARA NOTIFICAR");
        System.out.println("================================================================================\n");
    }
}
