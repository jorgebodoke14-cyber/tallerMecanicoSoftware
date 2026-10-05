# Fase 02 - Registro de Clientes

## Resumen

Se implemento el caso de uso de registro de clientes para una sucursal, con validaciones en Vue.js y Spring Boot, control de acceso por JWT y roles, persistencia MySQL, auditoria HTTP existente y almacenamiento desacoplado de fotografias. La sucursal se resuelve a partir del usuario autenticado; nunca se acepta desde el formulario como dato de confianza.

## Arquitectura implementada

La vista `frontend/src/components/ClientRegistration.vue` recibe la sesion ya autenticada y muestra el formulario solo a `ADMINISTRATOR` y `RECEPTIONIST`. `ClientFacade` en Vue valida el contexto, los campos del navegador y la fotografia antes de delegar el `FormData` a `ClientRepository`, que encapsula la llamada REST.

`ClientController` recibe `POST /api/clients` y delega al `ClientFacade` de Spring. Esa fachada vuelve a leer al actor desde la sesion, valida rol, sucursal, formato, duplicados y fotografia; despues usa `ClientRepository` de JPA y el puerto `PhotoStorage`. `LocalPhotoStorage` es el adaptador actual. El contrato permite sustituirlo por un adaptador S3, Cloudinary u otro proveedor sin cambiar el controlador ni la vista.

## Login auditado y refactorizado

El login ya aportaba JWT stateless, BCrypt con costo 12, usuarios activos, recuperacion de contrasena y `AuditFilter` para las rutas `/api`. Se conservaron esos elementos y se corrigieron los factores que faltaban para esta fase:

- Se agregaron `ADMINISTRATOR` y `RECEPTIONIST` como roles operativos de autorizacion.
- Se agrego `Branch` y `branch_id` a `UserAccount`; el JWT incluye ese identificador y el DTO de sesion lo entrega al frontend.
- La cuenta semilla se migra a `ADMINISTRATOR` y a la sucursal `MAIN` al arrancar.
- El alta de usuarios internos ya no es una operacion anonima: `/api/auth/register` requiere `ADMINISTRATOR` o `RECEPTIONIST`. Una recepcionista puede crear cuentas operativas, pero el backend impide que eleve privilegios creando una cuenta `ADMINISTRATOR`.
- El registro de clientes exige, tanto en frontend como backend, `ADMINISTRATOR` o `RECEPTIONIST`; el backend es la fuente de verdad.

Para la base existente se aplico una migracion de datos no destructiva: se creo la sucursal `MAIN`, se asociaron los usuarios previos a ella y se amplio el enum de roles conservando los valores antiguos.

## Modelo de datos y reglas

- `branches`: catalogo de talleres/sucursales. El registro actual contiene `MAIN`.
- `users.branch_id`: delimita la sucursal de la sesion.
- `clients.branch_id`: relaciona cada cliente a una sucursal desde el primer despliegue.
- `clients`: nombre completo, contacto alternativo, edad, fecha de nacimiento, telefonos, emails, direccion desglosada y referencia a foto.
- Las restricciones unicas son `(branch_id, email)` y `(branch_id, personal_phone)`. No se puede duplicar un cliente por correo o telefono personal dentro de una sucursal. La regla se verifica antes de persistir y se respalda con restricciones de MySQL.

Los campos validan longitud, formato de email, telefonos de 10 digitos, codigo postal de 5 digitos, rango de edad de 18 a 120 y coherencia entre edad y fecha de nacimiento.

## Fotografias

La fotografia es obligatoria y admite JPG/JPEG, PNG y WEBP. Estos tres formatos se eligieron por compatibilidad web y soporte de compresion; no se admitieron formatos adicionales para reducir superficie de ataque. Frontend y backend revisan extension, MIME declarado, firma binaria y limite estricto de 15 MB. La interfaz muestra previsualizacion local y mensajes de error antes del envio. El backend sanitiza el nombre al no reutilizarlo, genera una llave UUID y evita sobrescrituras.

No se implemento redimensionamiento automatico porque no es necesario para cumplir el limite y puede degradar una evidencia visual sin una politica de calidad aprobada. La validacion de 15 MB se conserva aunque se agregue un compresor en una fase posterior.

## Diagrama de componentes

El diagrama se encuentra en [component-diagram.html](docs/diagrams/component-diagram.html) y su especificacion fuente en [component-diagram.architecture.json](docs/diagrams/component-diagram.architecture.json). Refleja la vista Vue, `ClienteFacade`/`ClientFacade`, repositorios, JWT/roles, sucursal, almacenamiento de foto y MySQL.

Archify se instalo mediante:

```bash
npx skills add tt-a1i/archify -g
```

La instalacion publica de `skills` expone Archify como skill y no como binario npm llamado `archify`; por ello los comandos funcionales ejecutados fueron:

```bash
node /home/jorch/.agents/skills/archify/bin/archify.mjs validate architecture docs/diagrams/component-diagram.architecture.json --repo-root "$(pwd)" --quality showcase --json
node /home/jorch/.agents/skills/archify/bin/archify.mjs deliver architecture docs/diagrams/component-diagram.architecture.json docs/diagrams/component-diagram.html --repo-root "$(pwd)" --quality showcase --json
```

Resultado: validacion `showcase` aprobada, 9 controles tecnicos aprobados, cero errores y 13 referencias de codigo verificadas.

## Verificacion realizada

- `./gradlew test --console=plain`: compilacion Spring Boot correcta; el proyecto aun no contiene clases de prueba automatizadas.
- `npm run build`: compilacion Vue/Vite correcta.
- Prueba de integracion contra MySQL: alta de un cliente con PNG valido devolvio `201 Created`.
- Segundo envio con el mismo email y telefono dentro de `MAIN`: rechazado con `400` y mensaje de duplicado.

## Correccion posterior de flujos

Se corrigio la pantalla de registro de clientes para utilizar etiquetas HTML nativas; los 13 campos obligatorios y opcionales ahora son visibles y editables en la interfaz. Tambien se movio el registro de usuarios al area autenticada: el administrador ve la opcion `Usuarios`, crea cuentas con rol operativo y estas heredan el `branch_id` de su sesion. La pantalla anonima conserva solo inicio de sesion y recuperacion de contrasena, evitando un intento de alta sin permisos.

La prueba de integracion posterior confirmo: creacion de un usuario `RECEPTIONIST` por un administrador con respuesta `200`, inicio de sesion de ese usuario, alta de cliente con respuesta `201` y bloqueo del duplicado con respuesta `400`.

## Actualizacion de control operativo

Se agrego el boton **Ver clientes guardados** en el modulo de clientes. Abre una ventana modal que consulta `GET /api/clients` y presenta nombre, correo, telefono personal y fecha de registro. La consulta pasa por `ClientFacade` y `ClientRepository` del frontend; en el backend, `ClientFacade.list` valida de nuevo la sesion, el rol y la sucursal antes de recuperar solo los clientes asociados al `branch_id` del actor. No se entrega una lista global entre sucursales.

El alta de empleados desde `Usuarios` persiste en la tabla `users` mediante `POST /api/auth/register`: la contrasena se almacena como hash BCrypt, la cuenta hereda la sucursal de quien la crea y queda disponible para iniciar sesion. Administrador y Recepcionista pueden abrir los modulos de Clientes y Usuarios. Los demas roles autenticados reciben una unica pantalla de aviso de interfaz proxima y el boton de cerrar sesion, sin acceso a esas operaciones. Esta regla existe tanto en la interfaz como en Spring Security y en las fachadas de negocio.

El login incorpora un control accesible para mostrar u ocultar la contrasena mientras se captura. No revela hashes ni contrasenas almacenadas y el formulario inicia sin credenciales precargadas.

### Verificacion de esta actualizacion

- `./gradlew test --console=plain`: compilacion del backend correcta; aun no hay clases de prueba automatizadas en el proyecto.
- `npm run build`: compilacion Vue/Vite correcta.
- Prueba contra MySQL: alta de una Recepcionista por Administrador (`200`), alta de un Mecanico por Recepcionista (`200`) e inicio de sesion de la cuenta creada confirmado.
- Prueba de autorizacion: la Recepcionista obtuvo `200` al consultar clientes de su sucursal; la cuenta Mecanico obtuvo `403` en la misma ruta.
- Revision visual local: el login contiene el boton de mostrar contrasena y el modulo autorizado muestra la ventana **Clientes registrados**.

## Despliegue

No se publico una URL externa en esta iteracion: el entorno no tiene una sesion o token autorizado para Vercel, Render, Railway, AWS u otro proveedor. No se debe declarar una URL publica sin que el frontend, la API y MySQL sean realmente accesibles y tengan secretos configurados.

La configuracion recomendada es frontend en Vercel y backend Docker/Spring Boot en Render o Railway, con MySQL administrado. Configurar `VITE_API_URL` en frontend y `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `CORS_ALLOWED_ORIGINS` y `CLIENT_PHOTOS_DIRECTORY` en backend. Usar almacenamiento de objetos (S3/Cloudinary) antes de un despliegue multi-instancia, mediante un nuevo adaptador de `PhotoStorage`.

Para obtener una URL publica, se requiere autorizar una cuenta del proveedor escogido y configurar esas variables como secretos. Las credenciales de desarrollo no se incluyen en este documento ni deben copiarse a un proveedor.

## Actualizacion SEPOMEX - Catalogo postal local

Se incorporo la base tecnica para que el registro de clientes seleccione una direccion autentica desde el catalogo nacional SEPOMEX, almacenado localmente en MySQL. La aplicacion no consulta servicios publicos durante el registro; asi se evita depender de disponibilidad, limites o cambios de terceros.

| Estado | Elemento | Detalle |
| --- | --- | --- |
| ✅ | Modelo local | Se agregaron las tablas `postal_states`, `postal_municipalities` y `postal_settlements`, con llaves de texto para conservar codigos con ceros iniciales. |
| ✅ | Importador transaccional | El importador lee el archivo oficial `CPdescarga.txt`, valida encabezados, acepta UTF-8 o Windows-1252, elimina duplicados y reemplaza el catalogo dentro de una transaccion. |
| ✅ | API protegida | Se implementaron los endpoints JWT `GET /api/postal/states`, municipios por estado y asentamientos por municipio. |
| ✅ | Registro coherente | El frontend usa selects dependientes y busqueda local de asentamientos; el backend vuelve a validar estado, municipio, asentamiento y codigo postal antes de guardar al cliente. |
| ✅ | Pruebas | Se agregaron pruebas para el parser SEPOMEX y la fachada postal, incluyendo ceros iniciales, duplicados, catalogo vacio y manipulacion de codigo postal. |
| ✅ | Documentacion | La guia operativa esta en [docs/sepomex-catalog.md](docs/sepomex-catalog.md). |
| ⏳ | Carga del catalogo nacional oficial | Pendiente hasta disponer del archivo oficial descargado. No se deja una carga parcial ni datos ficticios en la base de datos. |
| ⏳ | URL publica | Sigue pendiente una cuenta autorizada de proveedor y configuracion de secretos; no se declara una URL externa inexistente. |

Para cargar el archivo oficial cuando este disponible, se debe ubicar en `backend/data/sepomex/CPdescarga.txt` (ruta ignorada por Git) y ejecutar:

```bash
cd backend
./gradlew bootRun --args='--app.postal.import.enabled=true --app.postal.import-file=./data/sepomex/CPdescarga.txt'
```

Mientras el catalogo este vacio, el formulario informa claramente que SEPOMEX debe importarse antes de permitir un registro con direccion. Esta proteccion evita almacenar direcciones arbitrarias o inconsistentes.

## Estado de la iteracion

| Lo que se hizo | Lo que esta completo | Lo que falta |
| --- | --- | --- |
| Registro con vista Vue, fachada y repositorio REST | Formulario, previsualizacion, `FormData` y mensajes de error | Pruebas E2E automatizadas del navegador |
| API Spring Boot y persistencia | `ClientController`, `ClientFacade`, entidades, repositorio y restricciones unicas | Migraciones versionadas con Flyway/Liquibase antes de produccion |
| Roles, sesion y sucursal | JWT con `branchId`, roles autorizados y `branch_id` en usuarios/clientes; Administrador y Recepcionista gestionan clientes y usuarios | Administracion de multiples sucursales en interfaz, fuera del alcance actual |
| Consulta de clientes guardados | Modal con listado REST filtrado por sucursal y control de rol en frontend y backend | Busqueda, paginacion o edicion de clientes, no solicitadas |
| Alta e inicio de sesion de empleados | Persistencia BCrypt en `users`, sucursal heredada y validacion de acceso de la nueva cuenta | Gestion de ciclo de vida de cuentas, fuera del alcance actual |
| Fotografias desacopladas | Validacion doble, UUID y adaptador local `PhotoStorage` | Adaptador S3/Cloudinary para instancias multiples |
| Auditoria de login | Refactor de roles, sucursal y alta interna protegida | Auditoria de negocio enriquecida con identificador de cliente, si se requiere |
| Diagrama Archify | JSON y HTML interactivo validados con calidad `showcase` | Ninguno para el diagrama actual |
| Despliegue | Guia y variables de entorno definidas | Cuenta o token del proveedor y URL publica real |
