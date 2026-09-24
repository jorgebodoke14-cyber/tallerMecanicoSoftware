# Reporte de Estado y Planeación - Fase Inicial

## Resumen Ejecutivo

Durante la fase inicial se construyo una base full-stack para el portal de gestion de ordenes de recepcion de un taller mecanico. El alcance funcional validado se concentra en el modulo de identidad: pantalla de acceso en Vue, comunicacion real con API REST, autenticacion mediante JWT, cifrado de contrasenas con BCrypt, roles operativos, persistencia en MySQL y registro de auditoria para las interacciones HTTP de la API.

La arquitectura actual esta dividida en dos aplicaciones independientes:

- **Frontend:** aplicacion Vue 3 compilada con Vite y estilizada con Tailwind CSS. En esta etapa la interfaz activa esta acotada al panel de acceso con login, registro y recuperacion.
- **Backend:** API REST con Spring Boot, Spring Security, Spring Data JPA, JWT, validaciones Bean Validation y conector MySQL.
- **Base de datos:** MySQL en contenedor Docker, con creacion/actualizacion automatica de tablas mediante Hibernate `ddl-auto: update`.

El login fue probado exitosamente contra el backend y la base de datos local. Tambien se validaron las compilaciones de frontend y backend con `npm run build` y `./gradlew test`.

## Tabla de Modulos Terminados

| Modulo | Estado | Descripcion Tecnica | Archivos Principales |
|---|---|---|---|
| Frontend de acceso | 100% fase inicial | Pantalla unica de login para el portal del taller. Incluye layout responsivo, seccion institucional, panel con pestanas de Login, Registro y Recuperar, estados de carga, mensajes de exito/error y persistencia local de sesion recibida desde la API. | `frontend/src/App.vue`, `frontend/src/components/AuthPanel.vue`, `frontend/src/styles/tailwind.css` |
| Cliente HTTP frontend | 100% fase inicial | Capa centralizada para consumir endpoints `/api`, agregar `Authorization: Bearer <token>` cuando existe sesion y normalizar errores JSON del backend. | `frontend/src/services/api.js`, `frontend/vite.config.js` |
| Autenticacion REST | 100% fase inicial | Endpoints para registrar usuario, iniciar sesion, consultar usuario autenticado, solicitar recuperacion y restablecer contrasena. Devuelve JWT y DTO seguro del usuario, sin exponer hash de contrasena. | `backend/src/main/java/com/taller/recepcion/auth/AuthController.java`, `backend/src/main/java/com/taller/recepcion/auth/AuthService.java`, `backend/src/main/java/com/taller/recepcion/auth/AuthDtos.java` |
| Seguridad, sesiones y autorizacion | 100% fase inicial | Seguridad stateless con Spring Security, JWT, filtro de autenticacion, CORS para frontend local, autorizacion por rutas y soporte de `@PreAuthorize`. Las contrasenas se cifran con BCrypt. | `backend/src/main/java/com/taller/recepcion/security/SecurityConfig.java`, `backend/src/main/java/com/taller/recepcion/security/JwtAuthenticationFilter.java`, `backend/src/main/java/com/taller/recepcion/security/JwtService.java`, `backend/src/main/java/com/taller/recepcion/security/CustomUserDetailsService.java` |
| Usuarios y roles | 100% fase inicial | Modelo persistente de usuario con nombre, correo unico, hash de contrasena, rol, estado activo y fechas de creacion/actualizacion. Roles definidos para dueno, gerente, secretaria, mecanico, contador y cliente. | `backend/src/main/java/com/taller/recepcion/users/UserAccount.java`, `backend/src/main/java/com/taller/recepcion/users/Role.java`, `backend/src/main/java/com/taller/recepcion/users/UserAccountRepository.java`, `backend/src/main/java/com/taller/recepcion/users/UserController.java` |
| Usuario semilla de desarrollo | 100% fase inicial | Inicializador que crea automaticamente un usuario propietario si no existe el correo configurado. Permite validar login sin carga manual de datos. | `backend/src/main/java/com/taller/recepcion/config/SeedDataConfig.java`, `backend/src/main/resources/application.yml` |
| Recuperacion de contrasena | 100% fase inicial tecnica | Generacion de token unico de recuperacion, expiracion temporal, marca de uso y actualizacion de hash de contrasena al restablecer. El envio por correo/SMS queda como integracion externa pendiente para fase posterior. | `backend/src/main/java/com/taller/recepcion/auth/PasswordResetToken.java`, `backend/src/main/java/com/taller/recepcion/auth/PasswordResetTokenRepository.java`, `backend/src/main/java/com/taller/recepcion/auth/AuthService.java` |
| Auditoria de API | 100% fase inicial | Filtro transversal para registrar actor, metodo HTTP, ruta, codigo de respuesta e IP de cada solicitud bajo `/api/`. Incluye endpoint de consulta restringido al rol propietario. | `backend/src/main/java/com/taller/recepcion/audit/AuditFilter.java`, `backend/src/main/java/com/taller/recepcion/audit/AuditLog.java`, `backend/src/main/java/com/taller/recepcion/audit/AuditLogRepository.java`, `backend/src/main/java/com/taller/recepcion/audit/AuditController.java` |
| Ordenes de recepcion - API base | 100% fase inicial API | Modelo y endpoints REST base para listar, crear, actualizar estado y consultar orden por folio publico. Aunque la UI de esta fase se concentro en login, el backend ya contiene la base funcional de ordenes. | `backend/src/main/java/com/taller/recepcion/orders/ReceptionOrder.java`, `backend/src/main/java/com/taller/recepcion/orders/ReceptionOrderController.java`, `backend/src/main/java/com/taller/recepcion/orders/ReceptionOrderService.java`, `backend/src/main/java/com/taller/recepcion/orders/OrderDtos.java` |
| Manejo global de errores | 100% fase inicial | Manejador central para credenciales invalidas, argumentos invalidos y errores de validacion, devolviendo mensajes JSON consistentes para consumo del frontend. | `backend/src/main/java/com/taller/recepcion/config/ApiExceptionHandler.java` |
| Build y ejecucion local | 100% fase inicial | Configuracion Gradle con Spring Boot `0.2.2` a nivel de proyecto, wrapper de Gradle, Java toolchain 26 y compilacion con `--release 25` para compatibilidad con el escaneo de clases de Spring Framework. | `backend/build.gradle`, `backend/settings.gradle`, `backend/gradlew`, `frontend/package.json`, `frontend/tailwind.config.js`, `frontend/postcss.config.js` |

## Estructura de Datos y Credenciales

### Modelo de datos implementado

La base de datos se construye mediante entidades JPA. Las tablas principales generadas por el backend son:

- **`users`**: almacena usuarios internos y clientes potenciales. Campos principales: identificador, nombre, correo unico, hash de contrasena, rol, bandera `active`, fecha de creacion y fecha de actualizacion.
- **`password_reset_tokens`**: almacena tokens de recuperacion asociados a usuarios, con fecha de expiracion y bandera de uso.
- **`audit_logs`**: registra eventos de API con actor, metodo HTTP, ruta, codigo de respuesta, direccion IP y fecha del evento.
- **`reception_orders`**: almacena ordenes de recepcion con folio unico, datos del cliente, telefono, vehiculo, placas, falla reportada, mecanico asignado, estado y porcentaje de avance.

### Ubicacion de configuracion y credenciales

La configuracion de conexion a base de datos, JWT, CORS y usuario semilla se encuentra en:

```text
backend/src/main/resources/application.yml
```

El archivo usa variables de entorno para resolver valores sensibles en tiempo de ejecucion:

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION_MINUTES`
- `CORS_ALLOWED_ORIGINS`
- `DEFAULT_OWNER_NAME`
- `DEFAULT_OWNER_EMAIL`
- `DEFAULT_OWNER_PASSWORD`

Por seguridad, las contrasenas, secretos JWT y credenciales reales no deben publicarse en el repositorio ni documentarse en texto plano. Para despliegues productivos se recomienda inyectar estos valores desde el panel de secretos del proveedor cloud o desde un archivo `.env` excluido por `.gitignore`.

### Configuracion local actual

- El backend escucha por defecto en `SERVER_PORT=8082`.
- El frontend de desarrollo usa Vite en `5173`.
- El proxy de desarrollo del frontend redirige `/api` hacia `http://localhost:8082`.
- La base de datos esperada para desarrollo es MySQL accesible desde el host local en el puerto `3306`.

## Guia de Despliegue (Deployment)

## Despliegue de Backend

### Software requerido

1. Java 26 instalado en el entorno de build/runtime.
2. Gradle Wrapper incluido en el proyecto (`backend/gradlew`).
3. Base de datos MySQL administrada o contenedor MySQL.
4. Proveedor recomendado: **Railway**, **Render** o **AWS Elastic Beanstalk/ECS**.
5. Repositorio GitHub conectado al proveedor de despliegue.

### Opcion recomendada para fase inicial: Railway

1. Crear un nuevo proyecto en Railway.
2. Agregar un servicio MySQL administrado o conectar una base MySQL externa.
3. Crear un servicio backend desde el repositorio de GitHub.
4. Configurar el directorio raiz del servicio como:

   ```text
   backend
   ```

5. Configurar comando de build:

   ```bash
   ./gradlew clean bootJar
   ```

6. Configurar comando de start:

   ```bash
   java -jar build/libs/taller-recepcion-backend-0.2.2.jar
   ```

7. Definir variables de entorno en Railway, sin escribir secretos en el codigo:

   ```text
   SERVER_PORT=<puerto asignado por proveedor o 8082>
   DB_HOST=<host mysql>
   DB_PORT=<puerto mysql>
   DB_NAME=<nombre base de datos>
   DB_USER=<usuario base de datos>
   DB_PASSWORD=<password base de datos>
   JWT_SECRET=<secreto fuerte de al menos 64 caracteres>
   JWT_EXPIRATION_MINUTES=480
   CORS_ALLOWED_ORIGINS=<url publica del frontend>
   DEFAULT_OWNER_NAME=<nombre propietario inicial>
   DEFAULT_OWNER_EMAIL=<correo propietario inicial>
   DEFAULT_OWNER_PASSWORD=<password temporal fuerte>
   ```

8. Verificar logs de arranque y confirmar que Hibernate inicializa tablas.
9. Probar endpoint de login:

   ```bash
   curl -X POST https://<backend-url>/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"<correo>","password":"<password>"}'
   ```

### Opcion Docker

Para una estrategia portable, se recomienda agregar en la siguiente fase:

- `backend/Dockerfile`
- `docker-compose.yml` para backend + MySQL
- healthcheck de API
- perfil de variables `.env.example` sin secretos reales

Ejemplo conceptual de imagen:

```dockerfile
FROM eclipse-temurin:26-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew clean bootJar

FROM eclipse-temurin:26-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Despliegue de Frontend

### Software requerido

1. Node.js 24 o compatible con Vite 6.
2. npm.
3. Proveedor recomendado: **Vercel** o **Netlify** para sitio estatico.
4. URL publica del backend para configurar consumo de API.

### Opcion recomendada: Vercel

1. Crear un nuevo proyecto en Vercel desde el repositorio de GitHub.
2. Configurar el directorio raiz del proyecto como:

   ```text
   frontend
   ```

3. Configurar framework:

   ```text
   Vite
   ```

4. Configurar comando de instalacion:

   ```bash
   npm install
   ```

5. Configurar comando de build:

   ```bash
   npm run build
   ```

6. Configurar directorio de salida:

   ```text
   dist
   ```

7. Para produccion, sustituir el proxy local de `frontend/vite.config.js` por una variable de entorno de API o crear una configuracion `VITE_API_BASE_URL`. La URL debera apuntar al backend desplegado.
8. En el backend, actualizar `CORS_ALLOWED_ORIGINS` con la URL publica de Vercel.
9. Ejecutar prueba manual de login desde la URL publica del frontend.

### Consideraciones DevOps para la siguiente fase

- Agregar `.gitignore` formal para excluir `node_modules`, `dist`, `build`, `bin`, archivos `.env` y logs.
- Agregar `.env.example` con nombres de variables sin valores sensibles.
- Agregar Dockerfile de backend y frontend.
- Agregar pipeline GitHub Actions para ejecutar `npm run build` y `./gradlew test` en cada push.
- Agregar migraciones versionadas con Flyway o Liquibase antes de pasar a produccion.
- Sustituir `ddl-auto: update` por migraciones controladas en ambientes productivos.
- Integrar envio real de recuperacion de contrasena mediante email transaccional.
