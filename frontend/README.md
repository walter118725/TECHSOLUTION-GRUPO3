# Frontend - TechSolutions

Carpeta destinada al desarrollo de la interfaz de usuario.

## Estructura

- `frontend/src/static/` → CSS, JS, imágenes.
- `frontend/src/templates/` → Plantillas HTML (versión estática para el frontend).
- `frontend/package.json` → Scripts de build y deploy.

## Comandos útiles

- Instalar dependencias (si usas npm):

```bash
cd frontend
npm install
```

- Compilar frontend (copiar assets a dist):

```bash
npm run build
```

- Deploy (copiar dist al backend):

```bash
npm run deploy
```

> Nota: `npm run deploy` copia los archivos desde `frontend/dist` a `src/main/resources/static` y `src/main/resources/templates` para que el backend (Spring Boot) los sirva.

Si necesitas que integre esta etapa dentro del `pom.xml` para que el frontend se construya automáticamente con `mvn install`, puedo hacerlo en la próxima iteración.
