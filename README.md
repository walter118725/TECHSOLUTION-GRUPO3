# 🛒 TechSolutions - Sistema de Gestión de Ventas

Sistema web para tienda de productos tecnológicos con carrito de compras, pasarelas de pago y reportes financieros.

---

## 🚀 Cómo Ejecutar

```bash
# 1. Ir a la carpeta del proyecto
cd backend

# 2. Ejecutar la aplicación
mvn spring-boot:run
```

**Abrir en el navegador:** http://localhost:8080

---

## 🔑 Usuarios

| Usuario | Contraseña | Acceso |
|---------|------------|--------|
| `admin` | `admin123` | Panel de administración |
| `gerente` | `gerente123` | Reportes financieros |
| `cliente` | `cliente123` | Compras |

---

## 📱 Páginas

| URL | Descripción |
|-----|-------------|
| http://localhost:8080 | Página de inicio |
| http://localhost:8080/productos | Catálogo de productos |
| http://localhost:8080/carrito | Carrito de compras |
| http://localhost:8080/swagger-ui.html | Documentación API |

---

## 🛠️ Tecnologías

- **Java 21** + **Spring Boot 3.5.8**
- **MySQL 8** (base de datos)
- **Thymeleaf** (vistas HTML)
- **Swagger** (documentación API)
- **JUnit 5** (tests)

---

## 🎨 Patrones de Diseño Implementados

| Patrón | Uso | Archivos |
|--------|-----|----------|
| **Adapter** | Pasarelas de pago (PayPal, Yape, Plin) | `pattern/adapter/` |
| **Observer** | Alertas de stock bajo | `pattern/observer/` |
| **Proxy** | Control de acceso a reportes | `pattern/proxy/` |

---

## 🧪 Tests Unitarios

```bash
# Ejecutar todos los tests
cd backend
mvn test
```

**42 tests** cubriendo:
- ✅ Modelo Usuario (11 tests)
- ✅ Patrón Proxy - Control de acceso (10 tests)
- ✅ Patrón Adapter - Pasarelas de pago (21 tests)

---

## 📁 Estructura del Proyecto

```
backend/
├── src/main/java/com/techsolutions/
│   ├── controller/     ← Controladores REST
│   ├── model/          ← Entidades (Usuario, Producto, Venta)
│   ├── service/        ← Lógica de negocio
│   ├── repository/     ← Acceso a datos
│   ├── pattern/        ← Patrones de diseño
│   │   ├── adapter/    ← PayPal, Yape, Plin
│   │   ├── observer/   ← Notificaciones stock
│   │   └── proxy/      ← Control de acceso
│   └── dto/            ← Objetos de transferencia
├── src/main/resources/
│   ├── templates/      ← Vistas HTML
│   └── static/         ← CSS, JavaScript
└── src/test/           ← Tests unitarios
```

---

## 📊 API REST

Documentación disponible en: http://localhost:8080/swagger-ui.html

**Endpoints principales:**
- `GET /api/productos` - Listar productos
- `POST /api/pagos/procesar` - Procesar pago
- `GET /api/reportes/ventas` - Reporte de ventas (solo GERENTE)

---

## 👥 Equipo

**TechSolutions - Grupo 3**

---

**Versión:** 1.0.0 | **Diciembre 2025**
