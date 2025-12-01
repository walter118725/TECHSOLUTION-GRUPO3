# TechSolutions - Sistema de Gestión de Ventas

## 📋 Descripción

Tienda online de productos tecnológicos con gestión de inventario, ventas y reportes. Desarrollado con Spring Boot y Thymeleaf.

---

## 🛠️ Tecnologías

| Tecnología | Versión |
|------------|---------|
| Java | 21 LTS |
| Spring Boot | 3.5.8 |
| Maven | 3.9+ |
| H2 Database | En memoria |
| Thymeleaf | Motor de plantillas |

---

## 🚀 Ejecutar el Proyecto

```bash
cd backend
mvn spring-boot:run
```

📍 **Acceder a:** http://localhost:8080

---

## 🔑 Usuarios de Prueba

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| `admin` | `admin123` | Administrador |
| `gerente` | `gerente123` | Gerente |

---

## 📁 Estructura

```
TechSolutions-Proyecto/
├── backend/                  # API Spring Boot
│   └── src/main/
│       ├── java/            # Código Java
│       └── resources/
│           ├── templates/   # Vistas HTML
│           └── static/      # CSS, JS
└── frontend/                # Assets adicionales
```

---

## 🎨 Patrones de Diseño

- **Adapter** - Pasarelas de pago (PayPal, Yape, Plin)
- **Observer** - Notificaciones de stock bajo
- **Proxy** - Control de acceso a reportes

---

## 📱 Páginas Principales

| Ruta | Descripción |
|------|-------------|
| `/` | Inicio con promociones |
| `/productos` | Catálogo de productos |
| `/carrito` | Carrito de compras |
| `/login` | Iniciar sesión |
| `/admin` | Panel de administrador |

---

## 🧪 Base de Datos

Consola H2: http://localhost:8080/h2-console
- **JDBC URL:** `jdbc:h2:mem:techsolutions`
- **Usuario:** `sa`
- **Contraseña:** *(vacía)*

---

**Versión:** 1.0.0 | **Última actualización:** Diciembre 2025
