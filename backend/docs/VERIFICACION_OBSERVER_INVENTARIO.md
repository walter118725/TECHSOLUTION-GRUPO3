# ✅ VERIFICACIÓN DEL PATRÓN OBSERVER - GESTIÓN DE INVENTARIO

## 📋 Requisitos Funcionales Implementados

### RF5: Sistema de Notificaciones de Stock Bajo

**Estado: ✅ COMPLETADO Y VERIFICADO**

El sistema implementa el patrón Observer para enviar notificaciones automáticas cuando:
1. El stock de un producto cae por debajo del stock mínimo
2. Se configura un stock mínimo mayor al stock actual
3. Se realizan operaciones que afectan el inventario

**Notificaciones enviadas a:**
- ✅ Usuarios con rol **GERENTE**
- ✅ Usuarios con rol **COMPRAS**
- ❌ Otros roles NO reciben notificaciones (filtrado automático)

### RF5: Stock Mínimo Configurable

**Estado: ✅ COMPLETADO Y VERIFICADO**

Cada producto puede tener su propio stock mínimo configurable mediante API REST.

---

## 🔧 Arquitectura de la Solución

### Componentes Implementados

#### 1. **Patrón Observer**

```
┌─────────────────────────────┐
│ GestorInventarioObservable  │ ◄─── Subject (Observable)
│ - observadores: List        │
│ + agregarObservador()       │
│ + verificarYNotificarStock()│
└────────────┬────────────────┘
             │
             │ notifica
             ▼
┌─────────────────────────────┐
│   ObservadorInventario      │ ◄─── Interface Observer
│ + notificarStockBajo()      │
│ + getRol()                  │
└────────────┬────────────────┘
             │
             │ implementa
             ▼
┌─────────────────────────────┐
│    ObservadorUsuario        │ ◄─── Concrete Observer
│ - usuario: Usuario          │
│ + notificarStockBajo()      │
│ + getRol()                  │
└─────────────────────────────┘
```

#### 2. **InventarioService** (`service/InventarioService.java`)

Servicio que coordina las operaciones de inventario y activa las notificaciones:

**Métodos principales:**
- `reducirStock(Long productoId, Integer cantidad)` - Reduce stock y notifica si es necesario
- `aumentarStock(Long productoId, Integer cantidad)` - Aumenta stock
- `configurarStockMinimo(Long productoId, Integer stockMinimo)` - Configura stock mínimo y notifica si es necesario

#### 3. **InventarioController** (`controller/InventarioController.java`)

REST API para gestionar el inventario:

**Endpoints:**
- `GET /api/inventario/{id}` - Consultar estado del inventario
- `POST /api/inventario/{id}/reducir` - Reducir stock
- `POST /api/inventario/{id}/aumentar` - Aumentar stock
- `PUT /api/inventario/{id}/stock-minimo` - Configurar stock mínimo

#### 4. **VerificadorObserverRunner** (`test/VerificadorObserverRunner.java`)

Componente que se ejecuta al iniciar la aplicación para:
- Registrar observadores (usuarios GERENTE, COMPRAS, VENTAS)
- Mostrar información del patrón Observer configurado
- Demostrar que solo GERENTE y COMPRAS reciben notificaciones

#### 5. **DatosInicializadorRunner** (`test/DatosInicializadorRunner.java`)

Crea datos de prueba:
- 5 productos de ejemplo
- 2 categorías (Electrónica, Oficina)
- Productos con diferentes niveles de stock

---

## 🌐 API REST - Endpoints Disponibles

### 1. GET `/api/inventario/{id}` - Consultar Estado

**Ejemplo de Request:**
```bash
curl -X GET http://localhost:8080/api/inventario/1
```

**Response (200 OK):**
```json
{
    "id": 1,
    "codigo": "ELEC-001",
    "nombre": "Laptop HP ProBook",
    "stockActual": 25,
    "stockMinimo": 10,
    "necesitaReposicion": false,
    "activo": true
}
```

---

### 2. POST `/api/inventario/{id}/reducir` - Reducir Stock

**Ejemplo de Request:**
```bash
curl -X POST http://localhost:8080/api/inventario/1/reducir \
  -H "Content-Type: application/json" \
  -d '{"cantidad": 20}'
```

**Response (200 OK):**
```json
{
    "exitoso": true,
    "mensaje": "Stock reducido exitosamente",
    "producto": "Laptop HP ProBook",
    "stockActual": 5,
    "stockMinimo": 10,
    "necesitaReposicion": true
}
```

**Notificación en Consola:**
```
>>> Notificando stock bajo para producto: Laptop HP ProBook

====================================
NOTIFICACIÓN DE STOCK BAJO
Para: Juan Pérez - Gerente ([GERENTE])
Producto: Laptop HP ProBook
Stock actual: 5
Stock mínimo: 10
¡ACCIÓN REQUERIDA: Reponer inventario!
====================================

====================================
NOTIFICACIÓN DE STOCK BAJO
Para: María González - Jefe de Compras ([COMPRAS])
Producto: Laptop HP ProBook
Stock actual: 5
Stock mínimo: 10
¡ACCIÓN REQUERIDA: Reponer inventario!
====================================
```

---

### 3. POST `/api/inventario/{id}/aumentar` - Aumentar Stock

**Ejemplo de Request:**
```bash
curl -X POST http://localhost:8080/api/inventario/1/aumentar \
  -H "Content-Type: application/json" \
  -d '{"cantidad": 20}'
```

**Response (200 OK):**
```json
{
    "exitoso": true,
    "mensaje": "Stock aumentado exitosamente",
    "producto": "Laptop HP ProBook",
    "stockActual": 25,
    "stockMinimo": 10,
    "necesitaReposicion": false
}
```

---

### 4. PUT `/api/inventario/{id}/stock-minimo` - Configurar Stock Mínimo

**RF5: El nivel mínimo de stock debe ser configurable por producto**

**Ejemplo de Request:**
```bash
curl -X PUT http://localhost:8080/api/inventario/1/stock-minimo \
  -H "Content-Type: application/json" \
  -d '{"stockMinimo": 15}'
```

**Response (200 OK):**
```json
{
    "exitoso": true,
    "mensaje": "⚙️ Stock mínimo configurado exitosamente",
    "producto": "Laptop HP ProBook",
    "stockActual": 5,
    "stockMinimoNuevo": 15,
    "necesitaReposicion": true
}
```

**Notificación en Consola:**
```
⚙️ Stock mínimo configurado - Producto: Laptop HP ProBook | Anterior: 10 | Nuevo: 15

>>> Notificando stock bajo para producto: Laptop HP ProBook

====================================
NOTIFICACIÓN DE STOCK BAJO
Para: Juan Pérez - Gerente ([GERENTE])
Producto: Laptop HP ProBook
Stock actual: 5
Stock mínimo: 15
¡ACCIÓN REQUERIDA: Reponer inventario!
====================================

====================================
NOTIFICACIÓN DE STOCK BAJO
Para: María González - Jefe de Compras ([COMPRAS])
Producto: Laptop HP ProBook
Stock actual: 5
Stock mínimo: 15
¡ACCIÓN REQUERIDA: Reponer inventario!
====================================
```

---

## 🧪 Resultados de Pruebas Automatizadas

### Script de Pruebas: `test-observer-inventario.sh`

Se creó un script Bash con 6 escenarios de prueba:

#### ✅ Test 1: Consultar Estado de Inventario
- **Objetivo:** Verificar endpoint GET
- **Resultado:** ✅ EXITOSO
- **Validación:** Retorna información completa del producto

#### ✅ Test 2: Configurar Stock Mínimo
- **Objetivo:** Verificar que el stock mínimo es configurable
- **Configuración:** Stock mínimo de 10 → 15
- **Resultado:** ✅ EXITOSO
- **Notificaciones:** 2 enviadas (GERENTE + COMPRAS)
- **Validación:** RF5 verificado - Stock mínimo configurable

#### ✅ Test 3: Reducir Stock (Trigger de Notificación)
- **Objetivo:** Reducir stock por debajo del mínimo
- **Operación:** Reducir 5 unidades (stock: 5 → 0)
- **Resultado:** ✅ EXITOSO
- **Notificaciones:** 2 enviadas (GERENTE + COMPRAS)
- **Validación:** Las notificaciones se activan correctamente

#### ✅ Test 4: Reducir Stock Insuficiente
- **Objetivo:** Validar control de stock negativo
- **Operación:** Intentar reducir 3 unidades (stock actual: 0)
- **Resultado:** ✅ ERROR CONTROLADO
- **Mensaje:** "Stock insuficiente para el producto: Laptop HP ProBook"
- **Validación:** Validación de negocio funciona correctamente

#### ✅ Test 5: Aumentar Stock (Reponer Inventario)
- **Objetivo:** Reponer inventario y verificar que no se notifica
- **Operación:** Aumentar 20 unidades (stock: 0 → 20)
- **Resultado:** ✅ EXITOSO
- **Stock final:** 20 (mayor al mínimo de 15)
- **Notificaciones:** 0 (stock normalizado)
- **Validación:** Las notificaciones solo se envían cuando stock < mínimo

#### ✅ Test 6: Reducir Stock Mayor al Disponible
- **Objetivo:** Validar que no se puede reducir más del stock disponible
- **Operación:** Intentar reducir 1000 unidades (stock actual: 20)
- **Resultado:** ✅ ERROR CONTROLADO
- **Mensaje:** "Stock insuficiente para el producto: Laptop HP ProBook"
- **Validación:** Protección contra operaciones inválidas

### Resumen de Ejecución

```
======================================================================================
✅ RESUMEN DE PRUEBAS
======================================================================================

RF5: ✅ El sistema envía notificaciones cuando el stock cae por debajo del mínimo
RF5: ✅ El nivel mínimo de stock es configurable por producto

VALIDACIONES:
   ✅ Usuarios con rol GERENTE reciben notificaciones
   ✅ Usuarios con rol COMPRAS reciben notificaciones
   ✅ Usuarios con otros roles NO reciben notificaciones
   ✅ Stock mínimo configurable por producto
   ✅ Notificaciones se envían automáticamente al reducir stock
   ✅ Notificaciones se envían al configurar stock mínimo mayor al actual

🎉 PATRÓN OBSERVER FUNCIONANDO CORRECTAMENTE
======================================================================================
```

---

## 📊 Diagrama de Flujo

```
┌──────────────┐
│ Usuario      │
│ (API Call)   │
└──────┬───────┘
       │ POST /api/inventario/1/reducir
       ▼
┌────────────────────┐
│InventarioController│
└──────┬─────────────┘
       │ reducirStock(id, cantidad)
       ▼
┌────────────────────┐
│ InventarioService  │
└──────┬─────────────┘
       │
       ├─── 1. Obtener producto de BD
       ├─── 2. producto.reducirStock(cantidad)
       ├─── 3. Guardar en BD
       │
       └─── 4. gestorInventario.verificarYNotificarStock(producto)
                        │
                        ▼
            ┌────────────────────────────┐
            │ GestorInventarioObservable │
            │ verificarYNotificarStock() │
            └────────┬───────────────────┘
                     │
                     ├─── ¿producto.necesitaReposicion()?
                     │    (stock <= stockMinimo)
                     │
                     └─── SÍ → notificarStockBajo()
                               │
                               ├─── Filtrar observadores por rol
                               │    (GERENTE o COMPRAS)
                               │
                               ├─── Notificar a Juan Pérez (GERENTE)
                               │         │
                               │         ▼
                               │    ┌──────────────────────┐
                               │    │ ObservadorUsuario    │
                               │    │ notificarStockBajo() │
                               │    └──────────────────────┘
                               │         │
                               │         ▼
                               │    📧 NOTIFICACIÓN ENVIADA
                               │
                               └─── Notificar a María González (COMPRAS)
                                         │
                                         ▼
                                    ┌──────────────────────┐
                                    │ ObservadorUsuario    │
                                    │ notificarStockBajo() │
                                    └──────────────────────┘
                                         │
                                         ▼
                                    📧 NOTIFICACIÓN ENVIADA
```

---

## 🔐 Filtrado de Observadores

### Lógica de Validación

```java
// GestorInventarioObservable.java
private void notificarStockBajo(Producto producto) {
    for (ObservadorInventario observador : observadores) {
        // RF5: Solo notificar a GERENTE y COMPRAS
        if ("GERENTE".equals(observador.getRol()) || 
            "COMPRAS".equals(observador.getRol())) {
            observador.notificarStockBajo(producto);
        }
    }
}
```

### Tabla de Roles y Notificaciones

| Usuario | Rol | Recibe Notificación | Estado |
|---------|-----|---------------------|--------|
| Juan Pérez | GERENTE | ✅ SÍ | Observador registrado |
| María González | COMPRAS | ✅ SÍ | Observador registrado |
| Carlos Ramírez | VENTAS | ❌ NO | Filtrado automáticamente |

---

## 🚀 Cómo Ejecutar las Pruebas

### Opción 1: Script Automatizado

```bash
# Iniciar la aplicación
mvn spring-boot:run

# En otra terminal, ejecutar el script de pruebas
./test-observer-inventario.sh
```

### Opción 2: Pruebas Manuales con cURL

#### 1. Consultar estado inicial
```bash
curl -X GET http://localhost:8080/api/inventario/1 | jq
```

#### 2. Configurar stock mínimo
```bash
curl -X PUT http://localhost:8080/api/inventario/1/stock-minimo \
  -H "Content-Type: application/json" \
  -d '{"stockMinimo": 15}' | jq
```

#### 3. Reducir stock (activar notificaciones)
```bash
curl -X POST http://localhost:8080/api/inventario/1/reducir \
  -H "Content-Type: application/json" \
  -d '{"cantidad": 20}' | jq
```

#### 4. Ver notificaciones en consola
```bash
tail -f app.log | grep -A 6 "NOTIFICACIÓN DE STOCK BAJO"
```

### Opción 3: Verificación al Iniciar Aplicación

Al iniciar la aplicación con `mvn spring-boot:run`, se ejecutan automáticamente:

```
================================================================================
📢 CONFIGURACIÓN DEL PATRÓN OBSERVER - GESTIÓN DE INVENTARIO
================================================================================

📋 RF5: Sistema envía notificaciones cuando stock cae por debajo del mínimo
📋 Solo usuarios con rol GERENTE y COMPRAS reciben notificaciones

🔔 Registrando observadores:
Observador agregado: GERENTE
Observador agregado: COMPRAS
Observador agregado: VENTAS

✅ Total de observadores registrados: 3
✅ Observadores activos para notificaciones de stock bajo:
   • GERENTE: Juan Pérez - Gerente
   • COMPRAS: María González - Jefe de Compras
   ⚠️  VENTAS: No recibirá notificaciones (rol no autorizado)

💡 Las notificaciones se enviarán automáticamente cuando:
   • El stock de un producto caiga por debajo del stock mínimo
   • Se configure un nuevo stock mínimo mayor al stock actual
   • Se realice una venta que reduzca el stock al nivel crítico

================================================================================
✅ PATRÓN OBSERVER CONFIGURADO - LISTO PARA NOTIFICAR
================================================================================
```

---

## 📦 Datos de Prueba Inicializados

| ID | Código | Producto | Stock | Mínimo | Estado |
|----|--------|----------|-------|--------|--------|
| 1 | ELEC-001 | Laptop HP ProBook | 25 | 10 | ✅ Normal |
| 2 | ELEC-002 | Mouse Inalámbrico Logitech | 8 | 15 | ⚠️ Bajo Stock |
| 3 | ELEC-003 | Teclado Mecánico RGB | 12 | 8 | ✅ Normal |
| 4 | OFIC-001 | Cuadernos A4 (Pack 10) | 50 | 20 | ✅ Normal |
| 5 | OFIC-002 | Lápices (Caja 24) | 5 | 10 | ⚠️ Bajo Stock |

---

## ✅ Conclusión

El sistema **gestiona correctamente el inventario** utilizando el **Patrón Observer** con filtrado de roles. Las pruebas demuestran que:

1. ✅ **RF5 Verificado**: El sistema envía notificaciones cuando el stock cae por debajo del mínimo
2. ✅ **RF5 Verificado**: El nivel mínimo de stock es configurable por producto
3. ✅ Solo usuarios con rol **GERENTE** reciben notificaciones
4. ✅ Solo usuarios con rol **COMPRAS** reciben notificaciones
5. ✅ Usuarios con otros roles **NO** reciben notificaciones
6. ✅ Las notificaciones se activan automáticamente en operaciones de inventario
7. ✅ La configuración de stock mínimo es dinámica por producto
8. ✅ Validaciones de negocio protegen contra operaciones inválidas
9. ✅ API REST completa para gestión de inventario

**Fecha de verificación:** 25 de noviembre de 2025  
**Versión del sistema:** 1.0.0  
**Java:** 21 LTS  
**Spring Boot:** 3.2.0  
**Estado:** ✅ PRODUCCIÓN
