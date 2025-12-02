package com.techsolutions.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para documentación de APIs
 * Sistema de Gestión de Ventas TechSolutions
 * 
 * @author TechSolutions - Grupo 3
 * @version 1.0.0
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI techSolutionsOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .externalDocs(externalDocs())
                .servers(servers())
                .components(components())
                .tags(tags());
    }

    /**
     * Información general de la API
     */
    private Info apiInfo() {
        return new Info()
                .title("🛒 TechSolutions API - Sistema de Gestión de Ventas")
                .description("""
                        ## 📋 Descripción
                        API REST completa para el sistema de gestión de ventas **TechSolutions**.
                        Desarrollado con Spring Boot 3.5.8 y Java 21 LTS.
                        
                        ---
                        
                        ## 🎯 Patrones de Diseño Implementados
                        
                        | Patrón | Descripción | Endpoint | RF |
                        |--------|-------------|----------|-----|
                        | **Adapter** | Integración de múltiples pasarelas de pago (PayPal, Yape, Plin) | `/api/pagos` | RF1, RF2 |
                        | **Proxy** | Control de acceso a reportes financieros validando roles | `/api/reportes` | RF3, RF4 |
                        | **Observer** | Notificaciones automáticas cuando el stock es bajo | `/api/inventario` | RF5 |
                        
                        ---
                        
                        ## 📋 Requerimientos Funcionales
                        
                        | RF | Descripción | Estado |
                        |----|-------------|--------|
                        | **RF1** | Integración de múltiples pasarelas de pago mediante adaptador común | ✅ Implementado |
                        | **RF2** | Habilitar/deshabilitar pasarelas desde panel de configuración | ✅ Implementado |
                        | **RF3** | Protección de acceso validando credenciales y roles | ✅ Implementado |
                        | **RF4** | Solo usuarios GERENTE acceden a reportes financieros | ✅ Implementado |
                        | **RF5** | Notificaciones cuando el stock cae por debajo del mínimo | ✅ Implementado |
                        
                        ---
                        
                        ## 👥 Usuarios de Prueba
                        
                        | Usuario | Contraseña | Rol | Acceso a Reportes |
                        |---------|------------|-----|-------------------|
                        | `admin` | `admin123` | ADMIN | ❌ |
                        | `gerente` | `gerente123` | GERENTE | ✅ |
                        | `cliente` | `cliente123` | CLIENTE | ❌ |
                        
                        ---
                        
                        ## 💳 Pasarelas de Pago Disponibles
                        
                        | Pasarela | ID | Descripción |
                        |----------|-----|-------------|
                        | **PayPal** | `paypal` | Pagos internacionales con tarjeta |
                        | **Yape** | `yape` | Billetera móvil BCP (Perú) |
                        | **Plin** | `plin` | Billetera móvil interbancaria (Perú) |
                        
                        ---
                        
                        ## 🗄️ Base de Datos
                        
                        - **Motor**: MySQL 8.0
                        - **Host**: localhost:3306
                        - **Base de datos**: techsolutions
                        - **Configuración**: JPA/Hibernate con DDL auto-update
                        
                        ---
                        
                        ## 🔗 Enlaces Útiles
                        
                        - [📂 Repositorio GitHub](https://github.com/walter118725/TECHSOLUTION-GRUPO3)
                        - [🏠 Página Principal](http://localhost:8080)
                        - [📊 Swagger UI](http://localhost:8080/swagger-ui.html)
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("TechSolutions - Grupo 3")
                        .email("soporte@techsolutions.pe")
                        .url("https://github.com/walter118725/TECHSOLUTION-GRUPO3"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    /**
     * Documentación externa
     */
    private ExternalDocumentation externalDocs() {
        return new ExternalDocumentation()
                .description("📚 Documentación Técnica y Código Fuente en GitHub")
                .url("https://github.com/walter118725/TECHSOLUTION-GRUPO3");
    }

    /**
     * Servidores disponibles
     */
    private List<Server> servers() {
        return List.of(
                new Server()
                        .url("http://localhost:8080")
                        .description("🖥️ Servidor de Desarrollo Local")
        );
    }

    /**
     * Componentes: esquemas de seguridad y modelos de datos
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private Components components() {
        return new Components()
                // Esquemas de Seguridad
                .addSecuritySchemes("basicAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("basic")
                        .description("Autenticación HTTP Basic con usuario y contraseña"))
                
                // Esquemas de Datos (DTOs)
                .addSchemas("ProductoDTO", new Schema()
                        .type("object")
                        .description("Datos de un producto del inventario")
                        .addProperty("id", new Schema().type("integer").format("int64").description("ID único del producto").example(1))
                        .addProperty("codigo", new Schema().type("string").description("Código único del producto").example("TECH-001"))
                        .addProperty("nombre", new Schema().type("string").description("Nombre del producto").example("Laptop HP ProBook 450"))
                        .addProperty("descripcion", new Schema().type("string").description("Descripción detallada").example("Laptop empresarial con procesador Intel Core i5"))
                        .addProperty("precio", new Schema().type("number").format("decimal").description("Precio en soles").example(2599.99))
                        .addProperty("stock", new Schema().type("integer").description("Cantidad en stock").example(25))
                        .addProperty("stockMinimo", new Schema().type("integer").description("Stock mínimo para alertas").example(10))
                        .addProperty("categoria", new Schema().type("string").description("Categoría del producto").example("Electrónicos"))
                        .addProperty("imagen", new Schema().type("string").description("URL de la imagen").example("/images/laptop.jpg"))
                        .addProperty("activo", new Schema().type("boolean").description("Estado del producto").example(true)))
                
                .addSchemas("PagoRequestDTO", new Schema()
                        .type("object")
                        .description("Solicitud para procesar un pago")
                        .addProperty("pasarela", new Schema().type("string").description("ID de la pasarela").example("yape")._enum(List.of("paypal", "yape", "plin")))
                        .addProperty("monto", new Schema().type("number").format("decimal").description("Monto a pagar").example(150.50))
                        .addProperty("referencia", new Schema().type("string").description("Referencia de la transacción").example("ORD-2024-001")))
                
                .addSchemas("PagoResponseDTO", new Schema()
                        .type("object")
                        .description("Respuesta del procesamiento de pago")
                        .addProperty("exitoso", new Schema().type("boolean").description("Indica si el pago fue exitoso").example(true))
                        .addProperty("mensaje", new Schema().type("string").description("Mensaje del resultado").example("Pago procesado correctamente"))
                        .addProperty("pasarela", new Schema().type("string").description("Pasarela utilizada").example("yape"))
                        .addProperty("monto", new Schema().type("number").format("decimal").description("Monto procesado").example(150.50))
                        .addProperty("referencia", new Schema().type("string").description("Referencia").example("ORD-2024-001"))
                        .addProperty("transaccionId", new Schema().type("string").description("ID de transacción").example("TXN-YAPE-123456")))
                
                .addSchemas("ReporteRequestDTO", new Schema()
                        .type("object")
                        .description("Solicitud para generar un reporte financiero")
                        .addProperty("username", new Schema().type("string").description("Nombre de usuario").example("gerente"))
                        .addProperty("activo", new Schema().type("boolean").description("Estado del usuario").example(true))
                        .addProperty("roles", new Schema().type("array").description("Roles del usuario").example(List.of("GERENTE")))
                        .addProperty("fechaInicio", new Schema().type("string").format("date").description("Fecha inicio").example("2024-01-01"))
                        .addProperty("fechaFin", new Schema().type("string").format("date").description("Fecha fin").example("2024-12-31"))
                        .addProperty("mes", new Schema().type("integer").description("Mes (1-12)").example(11))
                        .addProperty("anio", new Schema().type("integer").description("Año").example(2024)))
                
                .addSchemas("StockRequestDTO", new Schema()
                        .type("object")
                        .description("Solicitud para modificar stock")
                        .addProperty("cantidad", new Schema().type("integer").description("Cantidad a modificar").example(10))
                        .addProperty("stockMinimo", new Schema().type("integer").description("Nuevo stock mínimo").example(15)))
                
                .addSchemas("PasarelaDTO", new Schema()
                        .type("object")
                        .description("Información de una pasarela de pago")
                        .addProperty("id", new Schema().type("string").description("Identificador único").example("yape"))
                        .addProperty("nombre", new Schema().type("string").description("Nombre de la pasarela").example("Yape"))
                        .addProperty("habilitada", new Schema().type("boolean").description("Estado de la pasarela").example(true))
                        .addProperty("descripcion", new Schema().type("string").description("Descripción").example("Billetera móvil BCP")))
                
                .addSchemas("ApiResponseDTO", new Schema()
                        .type("object")
                        .description("Respuesta genérica de la API")
                        .addProperty("exitoso", new Schema().type("boolean").description("Indica si la operación fue exitosa").example(true))
                        .addProperty("mensaje", new Schema().type("string").description("Mensaje descriptivo").example("Operación completada"))
                        .addProperty("datos", new Schema().type("object").description("Datos adicionales de la respuesta")))
                
                .addSchemas("NotificacionStockBajo", new Schema()
                        .type("object")
                        .description("Notificación de stock bajo (Patrón Observer)")
                        .addProperty("productoId", new Schema().type("integer").format("int64").example(1))
                        .addProperty("codigo", new Schema().type("string").example("TECH-001"))
                        .addProperty("nombre", new Schema().type("string").example("Laptop HP"))
                        .addProperty("stockActual", new Schema().type("integer").example(5))
                        .addProperty("stockMinimo", new Schema().type("integer").example(10))
                        .addProperty("mensaje", new Schema().type("string").example("⚠️ Stock bajo: Producto necesita reposición"))
                        .addProperty("notificadoA", new Schema().type("array").example(List.of("GERENTE", "COMPRAS"))));
    }

    /**
     * Tags para agrupar endpoints
     */
    private List<Tag> tags() {
        return List.of(
                new Tag()
                        .name("Inventario")
                        .description("""
                                📦 **Gestión de Productos e Inventario**
                                
                                Endpoints para CRUD completo de productos y control de stock.
                                
                                ### 🔔 Patrón Observer (RF5)
                                El sistema notifica automáticamente cuando el stock de un producto
                                cae por debajo del mínimo configurado. Los usuarios con rol GERENTE
                                y COMPRAS reciben estas notificaciones.
                                
                                ### Endpoints disponibles:
                                - `GET /api/inventario/productos` - Listar todos los productos
                                - `POST /api/inventario/productos` - Crear nuevo producto
                                - `GET /api/inventario/{id}` - Obtener producto por ID
                                - `PUT /api/inventario/{id}` - Actualizar producto
                                - `DELETE /api/inventario/{id}` - Eliminar producto
                                - `POST /api/inventario/{id}/reducir` - Reducir stock
                                - `POST /api/inventario/{id}/aumentar` - Aumentar stock
                                - `PUT /api/inventario/{id}/stock-minimo` - Configurar stock mínimo
                                - `GET /api/inventario/stock-bajo` - Productos con stock bajo
                                """),
                
                new Tag()
                        .name("Pagos")
                        .description("""
                                💳 **Procesamiento de Pagos**
                                
                                Endpoints para procesar pagos con múltiples pasarelas.
                                
                                ### 🔌 Patrón Adapter (RF1, RF2)
                                Todas las pasarelas implementan la misma interfaz `PasarelaPago`,
                                permitiendo procesamiento uniforme independientemente del proveedor.
                                
                                ### Pasarelas Disponibles:
                                | ID | Nombre | Descripción |
                                |----|--------|-------------|
                                | `paypal` | PayPal | Pagos internacionales |
                                | `yape` | Yape | Billetera BCP |
                                | `plin` | Plin | Billetera interbancaria |
                                
                                ### Endpoints disponibles:
                                - `GET /api/pagos/pasarelas` - Listar pasarelas disponibles
                                - `POST /api/pagos/procesar` - Procesar un pago
                                - `PUT /api/pagos/pasarelas/{id}/estado` - Habilitar/deshabilitar pasarela
                                - `GET /api/pagos/pasarelas/{id}` - Obtener info de pasarela
                                """),
                
                new Tag()
                        .name("Reportes")
                        .description("""
                                📊 **Reportes Financieros**
                                
                                Endpoints para generación de reportes con control de acceso.
                                
                                ### 🔐 Patrón Proxy (RF3, RF4)
                                El Proxy valida credenciales y roles antes de permitir
                                el acceso a los reportes financieros.
                                
                                ### Control de Acceso:
                                | Rol | Acceso |
                                |-----|--------|
                                | GERENTE | ✅ Permitido |
                                | ADMIN | ❌ Denegado |
                                | CLIENTE | ❌ Denegado |
                                
                                ### Endpoints disponibles:
                                - `POST /api/reportes/ventas` - Reporte de ventas
                                - `POST /api/reportes/ingresos-gastos` - Reporte de ingresos y gastos
                                - `POST /api/reportes/utilidades` - Reporte de utilidades
                                - `POST /api/reportes/generar-pdf` - Generar PDF del reporte
                                """)
        );
    }
}

