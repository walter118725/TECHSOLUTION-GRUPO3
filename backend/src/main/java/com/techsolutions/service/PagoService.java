package com.techsolutions.service;

import com.techsolutions.pattern.adapter.PasarelaPago;
import com.techsolutions.pattern.adapter.PayPalAdapter;
import com.techsolutions.pattern.adapter.YapeAdapter;
import com.techsolutions.pattern.adapter.PlinAdapter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio que gestiona las pasarelas de pago
 * GRASP: Controller - Coordina las operaciones de pago
 * RF1: Integra múltiples pasarelas mediante un adaptador común
 * RF2: Permite habilitar/deshabilitar pasarelas
 */
@Service
public class PagoService {
    
    private final Map<String, PasarelaPago> pasarelas;
    
    public PagoService() {
        this.pasarelas = new HashMap<>();
        // Inicializar las pasarelas disponibles
        pasarelas.put("paypal", new PayPalAdapter());
        pasarelas.put("yape", new YapeAdapter());
        pasarelas.put("plin", new PlinAdapter());
    }
    
    /**
     * RF1: Procesa un pago utilizando la pasarela especificada
     * Gracias al patrón Adapter, todas las pasarelas tienen la misma interfaz
     */
    public boolean procesarPago(String nombrePasarela, BigDecimal monto, String referencia) {
        PasarelaPago pasarela = pasarelas.get(nombrePasarela.toLowerCase());
        
        if (pasarela == null) {
            throw new IllegalArgumentException("Pasarela no encontrada: " + nombrePasarela);
        }
        
        if (!pasarela.estaHabilitada()) {
            throw new IllegalStateException("La pasarela " + nombrePasarela + " no está habilitada");
        }
        
        return pasarela.procesarPago(monto, referencia);
    }
    
    /**
     * RF2: Habilita o deshabilita una pasarela de pago
     */
    public void configurarPasarela(String nombrePasarela, boolean habilitar) {
        PasarelaPago pasarela = pasarelas.get(nombrePasarela.toLowerCase());
        
        if (pasarela == null) {
            throw new IllegalArgumentException("Pasarela no encontrada: " + nombrePasarela);
        }
        
        // Usar reflexión o métodos específicos para cambiar el estado
        if (pasarela instanceof PayPalAdapter) {
            ((PayPalAdapter) pasarela).setHabilitada(habilitar);
        } else if (pasarela instanceof YapeAdapter) {
            ((YapeAdapter) pasarela).setHabilitada(habilitar);
        } else if (pasarela instanceof PlinAdapter) {
            ((PlinAdapter) pasarela).setHabilitada(habilitar);
        }
    }
    
    /**
     * Obtiene el estado de todas las pasarelas
     */
    public Map<String, Boolean> obtenerEstadoPasarelas() {
        Map<String, Boolean> estados = new HashMap<>();
        pasarelas.forEach((nombre, pasarela) -> 
            estados.put(nombre, pasarela.estaHabilitada())
        );
        return estados;
    }
    
    /**
     * Obtiene información de todas las pasarelas disponibles
     */
    public List<Map<String, Object>> listarPasarelas() {
        return pasarelas.entrySet().stream()
            .map(entry -> {
                Map<String, Object> info = new HashMap<>();
                info.put("id", entry.getKey());
                info.put("nombre", entry.getValue().getNombre());
                info.put("habilitada", entry.getValue().estaHabilitada());
                return info;
            })
            .toList();
    }
    
    /**
     * Verifica el estado de una transacción
     */
    public String verificarEstadoTransaccion(String nombrePasarela, String referencia) {
        PasarelaPago pasarela = pasarelas.get(nombrePasarela.toLowerCase());
        
        if (pasarela == null) {
            throw new IllegalArgumentException("Pasarela no encontrada: " + nombrePasarela);
        }
        
        return pasarela.verificarEstado(referencia);
    }
}
