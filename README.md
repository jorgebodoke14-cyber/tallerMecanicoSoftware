# Portal de gestion de ordenes de recepcion

Aplicacion full stack para un taller mecanico:

- `frontend/`: Vue + Vite + Tailwind CSS.
- `backend/`: Spring Boot REST API con seguridad JWT, BCrypt, roles, auditoria y MySQL.

## Frontend

```bash
cd frontend
npm install
npm run dev
```

## Backend

Requiere Java 26 y Gradle o Gradle Wrapper.

```bash
cd backend
SERVER_PORT=8082 ./gradlew bootRun
```

Variables de entorno utiles:

```bash
DB_HOST=localhost
DB_PORT=3306
DB_NAME=springdb
DB_USER=spring
DB_PASSWORD=nerv_spring_2026
JWT_SECRET=change-this-secret-with-at-least-64-characters-for-production
```

La API expone autenticacion, registro, recuperacion de contrasena, usuarios y ordenes de recepcion.

Usuario inicial de desarrollo:

- correo: `dueno@taller.com`
- contrasena: `Nerv_owner_2026!`
