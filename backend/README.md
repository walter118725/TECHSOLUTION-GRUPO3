# Backend - TechSolutions

Plataforma Spring Boot 3.5.0 de gestión de ventas, inventario y reportes financieros.

## 📋 Tecnologías

- **Java 21 LTS** - Runtime moderno y estable
- **Spring Boot 3.5.0** - Framework web
- **Spring Data JPA** - Persistencia
- **Spring Security 6.5.0** - Autenticación y autorización
- **Hibernate 6.6.15** - ORM
- **H2 Database 2.3.232** - Base de datos en memoria
- **Maven 3.9.11** - Gestor de dependencias

## 🏗️ Estructura

```
backend/src/main/java/com/techsolutions/
├── TechSolutionsApplication.java       # Punto de entrada Spring Boot
├── config/
│   └── SecurityConfig.java             # Configuración de seguridad
├── controller/
│   ├── WebController.java
│   ├── InventarioController.java
│   ├── PagoController.java
│   └── ReporteController.java
├── model/                              # Entidades JPA
├── repository/                         # Acceso a datos
├── service/                            # Lógica de negocio
├── pattern/                            # Patrones de diseño
│   ├── adapter/                        # Pasarelas de pago
│   ├── observer/                       # Notificaciones inventario
│   └── proxy/                          # Control accesos reportes
└── test/                               # Verificadores de patrón
```

## 🚀 Desarrollo Local

### Build

```bash
# Desde raíz del proyecto
./scripts/build.sh

# O directamente
cd backend
mvn clean install
```

### Ejecución

```bash
# Desde backend/
mvn spring-boot:run

# O desde raíz
./scripts/run-local.sh

# La app estará en http://localhost:8080
# H2 Console: http://localhost:8080/h2-console
```

### Credenciales de prueba

- **Usuario**: `admin`
- **Contraseña**: `admin123`

## 🐳 Docker

### Build imagen backend

```bash
docker build -t techsolutions-backend:latest -f backend/Dockerfile .
```

### Ejecutar contenedor

```bash
docker run -p 8080:8080 techsolutions-backend:latest
```

### Con docker-compose (incluye frontend)

```bash
docker-compose up -d
```

## 📝 Configuración

Archivo: `backend/src/main/resources/application.properties`

```properties
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:testdb
```

## 🔐 Seguridad

Se implementó **Spring Security 6.5.0** con:
- Autenticación por usuario/contraseña (BCrypt)
- Tres roles: `ADMIN`, `GERENTE`, `CLIENTE`
- Control de acceso a endpoints (venta, reportes, inventario)

## ✅ Tests

Ejecutar tests:

```bash
mvn test
```

Verificadores de patrones disponibles:

```bash
cd backend/scripts
./test-observer-inventario.sh
./test-proxy-reportes.sh
```

## 📚 Documentación

- `backend/docs/DOCUMENTACION_TECNICA.md` - Arquitectura completa
- `backend/docs/VERIFICACION_*.md` - Análisis de patrones
- Código comentado con Javadoc

## 🤝 Contribución

1. Crea rama desde `main`
2. Realiza cambios y commits descriptivos
3. Ejecuta `mvn clean install` para verificar
4. Haz push y abre PR

---

**Última actualización**: Noviembre 2025  
**Versión**: 1.0.0
