package com.techsolutions;

import com.techsolutions.pattern.adapter.PasarelaPago;
import com.techsolutions.pattern.adapter.PayPalAdapter;
import com.techsolutions.pattern.adapter.YapeAdapter;
import com.techsolutions.pattern.adapter.PlinAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para el Patrón Adapter - Pasarelas de Pago
 * RF1: El sistema debe integrar múltiples pasarelas de pago mediante un adaptador común
 * RF2: El administrador puede habilitar/deshabilitar pasarelas desde el panel de configuración
 */
@SuppressWarnings("all")
@DisplayName("Tests de Patrón Adapter - Pasarelas de Pago (RF1, RF2)")
class PasarelaPagoAdapterTest {

    private PayPalAdapter paypalAdapter;
    private YapeAdapter yapeAdapter;
    private PlinAdapter plinAdapter;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        paypalAdapter = new PayPalAdapter();
        yapeAdapter = new YapeAdapter();
        plinAdapter = new PlinAdapter();
    }

    @SuppressWarnings("unused")
    @Nested
    @DisplayName("RF1: Tests de PayPal Adapter")
    class PayPalAdapterTests {

        @Test
        @DisplayName("PayPal procesa pago correctamente cuando está habilitada")
        void paypal_procesaPagoExitosamente() {
            // Arrange
            BigDecimal monto = new BigDecimal("100.00");
            String referencia = "PAY-001";

            // Act
            boolean resultado = paypalAdapter.procesarPago(monto, referencia);

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("PayPal retorna nombre correcto")
        void paypal_retornaNombreCorrecto() {
            // Act & Assert
            assertEquals("PayPal", paypalAdapter.getNombre());
        }

        @Test
        @DisplayName("PayPal está habilitada por defecto")
        void paypal_habilitadaPorDefecto() {
            // Act & Assert
            assertTrue(paypalAdapter.estaHabilitada());
        }

        @Test
        @DisplayName("PayPal verifica estado de transacción")
        void paypal_verificaEstadoTransaccion() {
            // Act
            String estado = paypalAdapter.verificarEstado("PAY-001");

            // Assert
            assertNotNull(estado);
            assertTrue(estado.contains("PayPal"));
        }
    }

    @SuppressWarnings("unused")
    @Nested
    @DisplayName("RF1: Tests de Yape Adapter")
    class YapeAdapterTests {

        @Test
        @DisplayName("Yape procesa pago correctamente")
        void yape_procesaPagoExitosamente() {
            // Arrange
            BigDecimal monto = new BigDecimal("50.00");
            String referencia = "YAPE-001";

            // Act
            boolean resultado = yapeAdapter.procesarPago(monto, referencia);

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("Yape retorna nombre correcto")
        void yape_retornaNombreCorrecto() {
            // Act & Assert
            assertEquals("Yape", yapeAdapter.getNombre());
        }

        @Test
        @DisplayName("Yape está habilitada por defecto")
        void yape_habilitadaPorDefecto() {
            // Act & Assert
            assertTrue(yapeAdapter.estaHabilitada());
        }
    }

    @SuppressWarnings("unused")
    @Nested
    @DisplayName("RF1: Tests de Plin Adapter")
    class PlinAdapterTests {

        @Test
        @DisplayName("Plin procesa pago correctamente")
        void plin_procesaPagoExitosamente() {
            // Arrange
            BigDecimal monto = new BigDecimal("75.50");
            String referencia = "PLIN-001";

            // Act
            boolean resultado = plinAdapter.procesarPago(monto, referencia);

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("Plin retorna nombre correcto")
        void plin_retornaNombreCorrecto() {
            // Act & Assert
            assertEquals("Plin", plinAdapter.getNombre());
        }

        @Test
        @DisplayName("Plin está habilitada por defecto")
        void plin_habilitadaPorDefecto() {
            // Act & Assert
            assertTrue(plinAdapter.estaHabilitada());
        }
    }

    @SuppressWarnings("unused")
    @Nested
    @DisplayName("RF2: Tests de Habilitar/Deshabilitar Pasarelas")
    class HabilitarDeshabilitarTests {

        @Test
        @DisplayName("PayPal puede ser deshabilitada")
        void paypal_puedeSerDeshabilitada() {
            // Arrange & Act
            paypalAdapter.setHabilitada(false);

            // Assert
            assertFalse(paypalAdapter.estaHabilitada());
        }

        @Test
        @DisplayName("PayPal deshabilitada no procesa pagos")
        void paypalDeshabilitada_noProcesaPagos() {
            // Arrange
            paypalAdapter.setHabilitada(false);
            BigDecimal monto = new BigDecimal("100.00");

            // Act
            boolean resultado = paypalAdapter.procesarPago(monto, "TEST-001");

            // Assert
            assertFalse(resultado);
        }

        @Test
        @DisplayName("Yape puede ser deshabilitada y habilitada nuevamente")
        void yape_puedeSerHabilitadaYDeshabilitada() {
            // Arrange & Act - Deshabilitar
            yapeAdapter.setHabilitada(false);
            assertFalse(yapeAdapter.estaHabilitada());

            // Act - Habilitar
            yapeAdapter.setHabilitada(true);

            // Assert
            assertTrue(yapeAdapter.estaHabilitada());
        }

        @Test
        @DisplayName("Yape deshabilitada no procesa pagos")
        void yapeDeshabilitada_noProcesaPagos() {
            // Arrange
            yapeAdapter.setHabilitada(false);
            BigDecimal monto = new BigDecimal("50.00");

            // Act
            boolean resultado = yapeAdapter.procesarPago(monto, "TEST-002");

            // Assert
            assertFalse(resultado);
        }

        @Test
        @DisplayName("Plin deshabilitada no procesa pagos")
        void plinDeshabilitada_noProcesaPagos() {
            // Arrange
            plinAdapter.setHabilitada(false);
            BigDecimal monto = new BigDecimal("30.00");

            // Act
            boolean resultado = plinAdapter.procesarPago(monto, "TEST-003");

            // Assert
            assertFalse(resultado);
        }
    }

    @SuppressWarnings("unused")
    @Nested
    @DisplayName("RF1: Tests de Interfaz Común")
    class InterfazComunTests {

        @Test
        @DisplayName("Todas las pasarelas implementan PasarelaPago")
        void todasImplementanInterfazComun() {
            // Assert
            assertInstanceOf(PasarelaPago.class, paypalAdapter);
            assertInstanceOf(PasarelaPago.class, yapeAdapter);
            assertInstanceOf(PasarelaPago.class, plinAdapter);
        }

        @Test
        @DisplayName("Se pueden usar polimórficamente")
        void pasarelas_usoPolimorfico() {
            // Arrange
            PasarelaPago[] pasarelas = {paypalAdapter, yapeAdapter, plinAdapter};
            BigDecimal monto = new BigDecimal("100.00");

            // Act & Assert
            for (PasarelaPago pasarela : pasarelas) {
                assertTrue(pasarela.estaHabilitada());
                assertNotNull(pasarela.getNombre());
                assertTrue(pasarela.procesarPago(monto, "TEST-POLY"));
            }
        }

        @Test
        @DisplayName("Todas las pasarelas verifican estado")
        void todasVerificanEstado() {
            // Act & Assert
            assertNotNull(paypalAdapter.verificarEstado("REF-001"));
            assertNotNull(yapeAdapter.verificarEstado("REF-002"));
            assertNotNull(plinAdapter.verificarEstado("REF-003"));
        }
    }

    @SuppressWarnings("unused")
    @Nested
    @DisplayName("Tests de Montos")
    class MontosTests {

        @Test
        @DisplayName("Procesa montos pequeños")
        void procesaMontoPequeno() {
            // Arrange
            BigDecimal monto = new BigDecimal("0.01");

            // Act & Assert
            assertTrue(paypalAdapter.procesarPago(monto, "SMALL-001"));
        }

        @Test
        @DisplayName("Procesa montos grandes")
        void procesaMontoGrande() {
            // Arrange
            BigDecimal monto = new BigDecimal("999999.99");

            // Act & Assert
            assertTrue(paypalAdapter.procesarPago(monto, "LARGE-001"));
        }

        @Test
        @DisplayName("Procesa montos con decimales")
        void procesaMontoConDecimales() {
            // Arrange
            BigDecimal monto = new BigDecimal("123.45");

            // Act & Assert
            assertTrue(yapeAdapter.procesarPago(monto, "DEC-001"));
        }
    }
}
