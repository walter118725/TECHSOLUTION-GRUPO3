# TechSolutions - Plataforma de Gestión de Ventas
## Documentación Técnica de Sustentación - Evaluación Final

### Información del Proyecto
- **Nombre**: TechSolutions - Gestión de Ventas
- **Versión**: 1.0.0
- **Tecnologías**: Java 21 LTS, Spring Boot 3.2.0, H2 Database, Maven
- **Fecha**: Noviembre 2025

---

## 1. CONTEXTO Y SITUACIÓN PROBLEMÁTICA

TechSolutions S.A. es una empresa peruana que desarrolla soluciones tecnológicas para PyMEs. Este proyecto implementa una plataforma integral que resuelve los siguientes problemas:

### Problemas Identificados y Soluciones
1. **Pagos en Línea Incompatibles** → Patrón Adapter
2. **Seguridad de Información** → Patrón Proxy
3. **Gestión de Inventario** → Patrón Observer
4. **Procesamiento de Pedidos** → Patrones Command y Memento
5. **Políticas de Precios Flexibles** → Patrón Strategy
6. **Catálogo de Productos** → Patrón Iterator

---

## 2. PATRONES DE DISEÑO IMPLEMENTADOS

### 2.1. PATRÓN ADAPTER (Procesamiento de Pagos)

**Problema**: Cada cliente utiliza diferentes pasarelas de pago (PayPal, Yape, Plin) con APIs incompatibles.

**Solución**: Implementación del patrón Adapter para crear una interfaz común.

#### Estructura:
```
pattern/adapter/
├── PasarelaPago.java (Interfaz Target)
├── PayPalAdapter.java
├── YapeAdapter.java
└── PlinAdapter.java
```

#### Justificación Técnica:
- **GRASP - Pure Fabrication**: La interfaz `PasarelaPago` es una fabricación pura que no representa un concepto del dominio pero resuelve un problema técnico
- **GRASP - Low Coupling**: Desacopla el sistema de las implementaciones específicas de cada pasarela
- **RF1**: Integra múltiples pasarelas mediante un adaptador común
- **RF2**: Permite habilitar/deshabilitar pasarelas desde configuración

#### Código Clave:
```java
public interface PasarelaPago {
    boolean procesarPago(BigDecimal monto, String referencia);
    String verificarEstado(String referencia);
    String getNombre();
    boolean estaHabilitada();
}

public class PayPalAdapter implements PasarelaPago {
    private boolean habilitada;
    
    @Override
    public boolean procesarPago(BigDecimal monto, String referencia) {
        if (!habilitada) return false;
        // Adaptación de API de PayPal
        return true;
    }
}
```

### 2.2. PATRÓN PROXY (Control de Accesos)

**Problema**: Los reportes financieros contienen información sensible y requieren control robusto de accesos.

**Solución**: Implementación del patrón Proxy para validar credenciales y roles.

#### Estructura:
```
pattern/proxy/
├── ServicioReporte.java (Subject)
├── ServicioReporteReal.java (RealSubject)
└── ProxyReporteFinanciero.java (Proxy)
```

#### Justificación Técnica:
- **GRASP - Protected Variations**: Protege el acceso a reportes contra cambios en políticas de seguridad
- **GRASP - Controller**: El Proxy actúa como controlador de acceso
- **RF3**: Valida credenciales y roles antes de acceder a reportes
- **RF4**: Solo usuarios con rol GERENTE o CONTADOR pueden acceder

#### Diagrama:
```
Cliente → ProxyReporteFinanciero → ServicioReporteReal
              ↓ valida roles
           Usuario
```

### 2.3. PATRÓN OBSERVER (Gestión de Inventario)

**Problema**: Las empresas pierden ventas por no reponer productos a tiempo.

**Solución**: Patrón Observer para notificar automáticamente cuando el stock es bajo.

#### Estructura:
```
pattern/observer/
├── ObservadorInventario.java (Observer Interface)
├── ObservadorUsuario.java (ConcreteObserver)
└── GestorInventarioObservable.java (Subject)
```

#### Justificación Técnica:
- **GRASP - Low Coupling**: Los observadores están desacoplados del sujeto observable
- **GRASP - Information Expert**: El Producto conoce su propio stock y sabe cuándo necesita reposición
- **RF5**: Notifica automáticamente cuando stock < stockMinimo
- **RF6**: Nivel mínimo configurable por producto
- **Notifica solo a**: GERENTE y COMPRAS

#### Código Clave:
```java
@Component
public class GestorInventarioObservable {
    private final List<ObservadorInventario> observadores = new ArrayList<>();
    
    public void verificarYNotificarStock(Producto producto) {
        if (producto.necesitaReposicion()) {
            notificarStockBajo(producto);
        }
    }
    
    private void notificarStockBajo(Producto producto) {
        for (ObservadorInventario observador : observadores) {
            if ("GERENTE".equals(observador.getRol()) || 
                "COMPRAS".equals(observador.getRol())) {
                observador.notificarStockBajo(producto);
            }
        }
    }
}
```

### 2.4. PATRÓN COMMAND (Procesamiento de Pedidos)

**Problema**: No existe un sistema que registre acciones de manera ordenada ni permita revertir operaciones erróneas.

**Solución**: Patrón Command para encapsular operaciones como objetos.

#### Estructura:
```
pattern/command/
├── ComandoPedido.java (Command Interface)
├── CrearPedidoCommand.java
├── ProcesarPedidoCommand.java
├── AplicarDescuentoCommand.java
├── CancelarPedidoCommand.java
└── InvocadorComandos.java (Invoker)
```

#### Justificación Técnica:
- **GRASP - High Cohesion**: Cada comando tiene una responsabilidad específica
- **GRASP - Low Coupling**: Los comandos están desacoplados del receptor
- **RF7**: Encapsula acciones (crear, procesar, aplicar descuento, cancelar)
- **RF7**: Mantiene historial de comandos ejecutados

#### Diagrama de Secuencia:
```
Cliente → InvocadorComandos → CrearPedidoCommand → Venta
              ↓ almacena
          historialComandos[]
```

### 2.5. PATRÓN MEMENTO (Restauración de Estados)

**Problema**: Los usuarios requieren la opción de "deshacer" modificaciones críticas.

**Solución**: Patrón Memento para guardar y restaurar estados anteriores.

#### Estructura:
```
pattern/memento/
├── MementoVenta.java (Memento)
├── Venta.java (Originator - actualizada)
└── CareTaker.java (Caretaker)
```

#### Justificación Técnica:
- **GRASP - Information Expert**: La Venta sabe cómo crear y restaurar su propio estado
- **Encapsulación**: El Memento protege el estado interno de la Venta
- **RF8**: Permite revertir un pedido a un estado anterior
- **Integración con Command**: Los comandos crean mementos antes de modificar

#### Código Clave:
```java
public class MementoVenta {
    private final EstadoVenta estado;
    private final BigDecimal total;
    private final LocalDateTime timestamp;
    
    // Constructor con copia profunda del estado
}

public class CareTaker {
    private Stack<MementoVenta> mementos = new Stack<>();
    
    public void guardarMemento(MementoVenta memento) {
        mementos.push(memento);
    }
    
    public MementoVenta deshacer() {
        return mementos.isEmpty() ? null : mementos.pop();
    }
}
```

### 2.6. PATRÓN STRATEGY (Políticas de Precios)

**Problema**: Las PyMEs necesitan aplicar estrategias flexibles de precios.

**Solución**: Patrón Strategy para intercambiar algoritmos de cálculo de precios.

#### Estructura:
```
pattern/strategy/
├── EstrategiaPrecio.java (Strategy Interface)
├── PrecioEstandar.java
├── PrecioConDescuento.java
└── PrecioDinamico.java
```

#### Justificación Técnica:
- **GRASP - Polymorphism**: Diferentes estrategias con comportamiento polimórfico
- **Open/Closed Principle**: Abierto a extensión (nuevas estrategias), cerrado a modificación
- **RF9**: Soporta precio estándar, con descuento, y dinámico
- **RF10**: El administrador puede cambiar la estrategia en configuración

#### Código Clave:
```java
public interface EstrategiaPrecio {
    BigDecimal calcularPrecio(BigDecimal precioBase, Producto producto);
    String getNombre();
}

public class PrecioConDescuento implements EstrategiaPrecio {
    private BigDecimal porcentajeDescuento;
    
    @Override
    public BigDecimal calcularPrecio(BigDecimal precioBase, Producto producto) {
        BigDecimal descuento = precioBase
            .multiply(porcentajeDescuento)
            .divide(BigDecimal.valueOf(100));
        return precioBase.subtract(descuento);
    }
}

public class PrecioDinamico implements EstrategiaPrecio {
    @Override
    public BigDecimal calcularPrecio(BigDecimal precioBase, Producto producto) {
        // Ajusta precio según demanda, temporada, stock
        if (producto.getStock() < producto.getStockMinimo()) {
            return precioBase.multiply(BigDecimal.valueOf(1.15)); // +15%
        }
        return precioBase;
    }
}
```

### 2.7. PATRÓN ITERATOR (Catálogo de Productos)

**Problema**: Los catálogos tienen muchos productos y la navegación es lenta.

**Solución**: Patrón Iterator para recorrer el catálogo eficientemente.

#### Estructura:
```
pattern/iterator/
├── IteradorProducto.java (Iterator Interface)
├── IteradorProductoConcreto.java
└── ColeccionProductos.java (Aggregate)
```

#### Justificación Técnica:
- **GRASP - Pure Fabrication**: El Iterator es una fabricación que no existe en el dominio
- **Encapsulación**: No expone la estructura interna de la colección
- **RF11**: Recorre catálogo con soporte para paginación y filtros
- **RF12**: Muestra productos ordenados sin exponer estructura interna

#### Código Clave:
```java
public interface IteradorProducto {
    boolean hasNext();
    Producto next();
    void reset();
}

public class ColeccionProductos {
    private List<Producto> productos = new ArrayList<>();
    
    public IteradorProducto crearIteradorPaginado(int tamanioPagina) {
        return new IteradorProductoPaginado(productos, tamanioPagina);
    }
    
    public IteradorProducto crearIteradorFiltrado(Predicate<Producto> filtro) {
        return new IteradorProductoFiltrado(productos, filtro);
    }
}
```

---

## 3. APLICACIÓN DE PATRONES GRASP

### 3.1. Information Expert
- **Producto**: Conoce su propio stock y determina si necesita reposición
- **Usuario**: Conoce sus roles y permisos
- **Venta**: Calcula sus propios totales
- **DetalleVenta**: Calcula su propio subtotal

### 3.2. Creator
- **Venta** crea y gestiona sus **DetalleVenta**
- **GestorInventarioObservable** crea notificaciones

### 3.3. Controller
- **InvocadorComandos**: Coordina la ejecución de comandos
- **GestorInventarioObservable**: Coordina las notificaciones
- **ProxyReporteFinanciero**: Controla el acceso a reportes

### 3.4. Low Coupling
- Los patrones Adapter, Strategy y Command mantienen bajo acoplamiento
- Las interfaces definen contratos claros entre componentes

### 3.5. High Cohesion
- Cada comando tiene una única responsabilidad
- Cada estrategia de precio se enfoca en un algoritmo específico

### 3.6. Polymorphism
- Estrategias de precio polimórficas
- Adaptadores de pago polimórficos

### 3.7. Pure Fabrication
- **PasarelaPago** (interface para adaptar APIs externas)
- **IteradorProducto** (no existe en el dominio real)

### 3.8. Protected Variations
- El Proxy protege contra cambios en políticas de seguridad
- Strategy protege contra cambios en cálculos de precios

---

## 4. ARQUITECTURA DEL SISTEMA

### 4.1. Estructura de Capas
```
┌─────────────────────────────────────┐
│         Capa de Presentación        │
│  (Controllers REST + Thymeleaf)     │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│        Capa de Patrones             │
│  (Adapter, Proxy, Command, etc.)    │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│         Capa de Servicio            │
│  (Lógica de Negocio)                │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│       Capa de Repositorio           │
│  (Spring Data JPA)                  │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│         Base de Datos H2            │
└─────────────────────────────────────┘
```

### 4.2. Modelo de Dominio
```
Usuario ──┐
          ├──> Venta ──> DetalleVenta ──> Producto ──> Categoría
Cliente ──┘

Producto ──> Observer (GestorInventario)
            ↓
      ObservadorUsuario (GERENTE, COMPRAS)
```

---

## 5. REQUISITOS FUNCIONALES IMPLEMENTADOS

| RF | Descripción | Patrón | Estado |
|----|-------------|--------|--------|
| RF1 | Integrar múltiples pasarelas de pago | Adapter | ✅ Implementado |
| RF2 | Habilitar/deshabilitar pasarelas | Adapter | ✅ Implementado |
| RF3 | Proteger acceso a reportes financieros | Proxy | ✅ Implementado |
| RF4 | Solo GERENTE/CONTADOR acceden a reportes | Proxy | ✅ Implementado |
| RF5 | Notificar stock bajo | Observer | ✅ Implementado |
| RF6 | Stock mínimo configurable | Observer | ✅ Implementado |
| RF7 | Comandos con historial | Command | ✅ Implementado |
| RF8 | Revertir pedido a estado anterior | Memento | ✅ Implementado |
| RF9 | Estrategias de precios | Strategy | ✅ Implementado |
| RF10 | Cambiar estrategia desde config | Strategy | ✅ Implementado |
| RF11 | Recorrer catálogo con iterador | Iterator | ✅ Implementado |
| RF12 | Mostrar productos sin exponer estructura | Iterator | ✅ Implementado |

---

## 6. TECNOLOGÍAS Y HERRAMIENTAS

### 6.1. Backend
- **Java 21 LTS**: Versión actual de soporte a largo plazo
- **Spring Boot 3.2.0**: Framework principal
- **Spring Data JPA**: Persistencia de datos
- **Spring Security**: Autenticación y autorización
- **H2 Database**: Base de datos en memoria para desarrollo

### 6.2. Frontend
- **Thymeleaf**: Motor de plantillas
- **Bootstrap 5**: Framework CSS responsive
- **JavaScript**: Interacciones dinámicas

### 6.3. Build & Testing
- **Maven 3.9.11**: Gestión de dependencias
- **JUnit 5**: Testing unitario
- **Mockito**: Mocking para pruebas

---

## 7. MEJORAS Y ESCALABILIDAD

### 7.1. Mejoras Implementadas
1. **Separación de Responsabilidades**: Cada patrón en su propio paquete
2. **Código Documentado**: Javadoc explicando el propósito de cada clase
3. **GRASP Explicito**: Comentarios indicando qué patrón GRASP se aplica
4. **Configuración Externalizada**: Properties para fácil configuración

### 7.2. Escalabilidad Futura
1. **Nuevas Pasarelas**: Solo agregar nuevos Adapters
2. **Nuevas Estrategias**: Implementar interface EstrategiaPrecio
3. **Nuevos Comandos**: Implementar interface ComandoPedido
4. **Microservicios**: Arquitectura preparada para separación

---

## 8. CONCLUSIONES

### 8.1. Logros Alcanzados
✅ **Patrones Estructurales**: Adapter y Proxy implementados para control de acceso y eficiencia
✅ **Patrones de Comportamiento**: Observer, Strategy, Command, Memento, Iterator implementados
✅ **GRASP Aplicados**: Information Expert, Creator, Controller, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Protected Variations
✅ **Código Organizado**: Estructura clara y mantenible
✅ **Documentación Completa**: Cada componente documentado con justificación técnica

### 8.2. Beneficios para las PyMEs
1. **Flexibilidad**: Fácil agregar nuevas pasarelas de pago y estrategias
2. **Seguridad**: Control robusto de accesos a información sensible
3. **Eficiencia**: Notificaciones automáticas previenen pérdidas de ventas
4. **Confiabilidad**: Sistema de deshacer previene errores críticos
5. **Escalabilidad**: Arquitectura preparada para crecimiento

---

## 9. REFERENCIAS

- Gamma, E., et al. (1994). *Design Patterns: Elements of Reusable Object-Oriented Software*
- Larman, C. (2004). *Applying UML and Patterns*
- Spring Framework Documentation
- Clean Code Principles
- SOLID Principles

---

**Elaborado por**: TechSolutions Development Team  
**Fecha**: Noviembre 2025  
**Versión**: 1.0
