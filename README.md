diff --git a/README.md b/README.md
new file mode 100644
index 0000000000000000000000000000000000000000..042651965f4f3c659bbf07229d7cb6ac0436b715
--- /dev/null
+++ b/README.md
@@ -0,0 +1,226 @@
+# 📘 Plataforma de gestión energética (Trabajo de Grado 2)
+
+Este repositorio contiene una solución full‑stack para la gestión de consumo eléctrico de usuarios residenciales. El backend está construido con **Spring Boot 3** y sigue principios de arquitectura hexagonal / DDD, mientras que el frontend es una **SPA en React + Vite + Tailwind** pensada para dashboards y control de medidores. Todo el entorno se ejecuta de forma local contra una **base de datos Microsoft SQL Server**.
+
+> ℹ️ **Dato clave:** la aplicación backend se conecta por defecto a un SQL Server local (`jdbc:sqlserver://localhost:1433`) y usa la base de datos `TG2` con el usuario `TDG2`. Asegúrate de tener esa instancia disponible antes de iniciar los servicios.
+
+## Tabla de contenidos
+- [Arquitectura general](#arquitectura-general)
+- [Estructura del repositorio](#estructura-del-repositorio)
+- [Backend (Spring Boot)](#backend-spring-boot)
+  - [Propiedades relevantes](#propiedades-relevantes)
+  - [Capas y componentes](#capas-y-componentes)
+  - [Casos de uso expuestos](#casos-de-uso-expuestos)
+  - [Seguridad y autenticación](#seguridad-y-autenticación)
+  - [Arranque local con SQL Server](#arranque-local-con-sql-server)
+  - [Herramientas para pruebas manuales](#herramientas-para-pruebas-manuales)
+- [Frontend (React + Vite)](#frontend-react--vite)
+  - [Estructura y componentes principales](#estructura-y-componentes-principales)
+  - [Servicios de datos y consumo de API](#servicios-de-datos-y-consumo-de-api)
+  - [Ejecución en desarrollo](#ejecución-en-desarrollo)
+- [Integración extremo a extremo](#integración-extremo-a-extremo)
+  - [Usuarios y datos iniciales](#usuarios-y-datos-iniciales)
+  - [Notas de despliegue y CORS](#notas-de-despliegue-y-cors)
+- [Resolución de problemas frecuentes](#resolución-de-problemas-frecuentes)
+
+---
+
+## Arquitectura general
+
+```
+TRABAJO_DE_GRADO_2/
+├── backend/   → API REST en Spring Boot 3 (Java 21)
+└── frontend/  → Aplicación React 18 + Vite + Tailwind
+```
+
+- **Dominio funcional:** registro y autenticación de usuarios, administración de medidores, cuotas de consumo, lecturas, alertas y recomendaciones.
+- **Comunicación:** el frontend consume los endpoints REST del backend usando JWT. El cliente apunta a `http://localhost:8081/api` y maneja tokens en `localStorage`.
+- **Persistencia:** SQL Server con scripts de datos (`data.sql`) que inicializan usuarios, medidores, consumos, cuotas y alertas para pruebas rápidas.
+
+## Estructura del repositorio
+
+| Ruta | Descripción |
+|------|-------------|
+| `backend/pom.xml` | Definición Maven: Spring Boot, Spring Data JPA, Seguridad, SQL Server JDBC, MapStruct y OpenAPI. |
+| `backend/src/main/java/com/ucdc/backend` | Código fuente organizado por capas (`application`, `domain`, `infrastructure`). |
+| `backend/src/main/resources/application.properties` | Configuración del servicio (puerto, conexión SQL Server, propiedades JWT, reglas de alertas). |
+| `backend/src/main/resources/data.sql` | Semilla de datos con usuarios, credenciales bcrypt, medidores, consumos, cuotas y alertas. |
+| `frontend/src` | Código de la SPA: componentes, contextos, servicios API, estilos y documentación auxiliar. |
+| `frontend/vite.config.ts` | Configuración de Vite (alias, puerto 3000, build a `build/`). |
+| `frontend/package.json` | Dependencias (React, Radix UI, Recharts, Tailwind 4, etc.) y scripts de npm. |
+
+---
+
+## Backend (Spring Boot)
+
+El backend es una API REST en **Java 21** que arranca con `Spring Boot 3.5`. Se apoya en Spring Data JPA para la persistencia, Spring Security + JWT para autenticación y MapStruct para mapear entidades/dominios.
+
+### Propiedades relevantes
+
+El archivo `application.properties` fija todas las conexiones necesarias para trabajar localmente con SQL Server:
+
+```properties
+spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=TG2;encrypt=true;trustServerCertificate=true
+spring.datasource.username=TDG2
+spring.datasource.password=TDG2025*
+spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
+server.port=8081
+spring.jpa.hibernate.ddl-auto=create-drop
+spring.sql.init.mode=always
+spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect
+security.jwt.secret=${JWT_SECRET:change-me-256bits-key}
+```
+
+- **Motor**: MS SQL Server en el puerto estándar 1433.
+- **Credenciales**: usuario `TDG2` con la contraseña `TDG2025*` y base `TG2`.
+- **Esquema**: `ddl-auto=create-drop` recrea las tablas en cada arranque y `data.sql` se ejecuta siempre (`spring.sql.init.mode=always`).
+- **JWT**: se puede sobreescribir la clave con la variable `JWT_SECRET` en el entorno.
+
+### Capas y componentes
+
+La organización sigue un enfoque hexagonal:
+
+- **`domain`**: entidades (por ejemplo `User`, `Meter`, `Consumption`, `Alert`), repositorios y servicios de dominio (detectores estadísticos, políticas de cuota, excepciones y enums para estados/roles).【F:backend/src/main/java/com/ucdc/backend/domain/model/User.java†L1-L120】【F:backend/src/main/java/com/ucdc/backend/domain/services/detectors/SpikeDetector.java†L1-L120】
+- **`application`**: DTOs, mapeadores y casos de uso (`RegisterUserUseCase`, `ListMyMetersUseCase`, `GetConsumptionChartUseCase`, etc.) que orquestan la lógica entre dominio e infraestructura.【F:backend/src/main/java/com/ucdc/backend/application/usecase/consumption/GetConsumptionChartUseCase.java†L1-L120】
+- **`infrastructure`**: controladores REST, configuración de seguridad, adaptadores JPA (repositorios Spring Data, entidades persistentes), configuración CORS y beans auxiliares.【F:backend/src/main/java/com/ucdc/backend/infrastructure/web/controller/AuthController.java†L1-L64】【F:backend/src/main/java/com/ucdc/backend/infrastructure/security/SecurityConfig.java†L1-L63】【F:backend/src/main/java/com/ucdc/backend/infrastructure/config/CorsConfig.java†L1-L41】
+
+### Casos de uso expuestos
+
+Los controladores en `infrastructure/web/controller` agrupan los principales endpoints:
+
+| Módulo | Endpoints | Funcionalidad |
+|--------|-----------|----------------|
+| Autenticación (`AuthController`) | `POST /api/auth/register`, `POST /api/auth/login`, `POST /api/auth/refresh`, `POST /api/auth/logout` | Registro de usuarios, emisión y refresco de tokens, cierre de sesión. | 
+| Medidores (`MeterController`, `MyMetersController`) | `POST /api/meters`, `GET /api/my/meters`, `PUT /api/my/meters/{meterId}` | Alta de medidores, listado filtrado por usuario autenticado y actualización de alias/datos. |
+| Consumos (`ConsumptionController`) | `GET /api/meters/{id}/consumption/monthly`, `.../annual`, `.../chart`, `.../compare` | Consultas agregadas en granularidades diaria/mensual y comparativas vs proyecciones. |
+| Cuotas (`QuotaController`) | `GET /api/meters/{id}/quota/active`, `PUT /api/meters/{id}/quota` | Gestión del límite de kWh por medidor. |
+| Lecturas (`ReadingController`) | Ingesta de lecturas y consulta histórica. |
+| Alertas (`AlertController`) | Listado de alertas, generación temprana y resolución. |
+| Recomendaciones (`RecommendationController`) | Sugerencias de ahorro derivadas del análisis de consumo. |
+| Usuarios (`UserController`) | Perfil, activación/desactivación y consulta administrativa. |
+
+Cada caso de uso delega en adaptadores de persistencia (`infrastructure/persistence/adapter`) que implementan los puertos de repositorio definidos en el dominio. Los mapeadores MapStruct transforman entre DTOs, modelos de dominio y entidades.
+
+### Seguridad y autenticación
+
+- **JWT**: `JwtAuthFilter` valida los tokens en cada petición y se registra en el `SecurityFilterChain`. El proveedor (`JwtProviderAdapter`) firma tokens con el secreto configurado y emite access/refresh tokens.【F:backend/src/main/java/com/ucdc/backend/infrastructure/security/JwtAuthFilter.java†L1-L120】【F:backend/src/main/java/com/ucdc/backend/infrastructure/security/JwtProviderAdapter.java†L1-L160】
+- **Password hashing**: los hashes se generan con `BCryptPasswordEncoder` y se almacenan en `password_credentials`. Al autenticarse se compara contra los hashes cargados desde `data.sql`.【F:backend/src/main/java/com/ucdc/backend/infrastructure/security/BCryptPasswordEncoderAdapter.java†L1-L34】【F:backend/src/main/resources/data.sql†L18-L39】
+- **Política de acceso**: el `SecurityConfig` habilita CORS, desactiva sesiones (stateless), permite libremente `/api/auth/**` y la documentación (`/swagger-ui/**`), protegiendo el resto de endpoints.【F:backend/src/main/java/com/ucdc/backend/infrastructure/security/SecurityConfig.java†L24-L62】
+
+Swagger/OpenAPI está disponible en `http://localhost:8081/swagger-ui/index.html` gracias al starter `springdoc-openapi-starter-webmvc-ui`.
+
+### Arranque local con SQL Server
+
+1. **Preparar la base de datos** (una sola vez):
+   ```sql
+   CREATE DATABASE TG2;
+   CREATE LOGIN TDG2 WITH PASSWORD = 'TDG2025*';
+   CREATE USER TDG2 FOR LOGIN TDG2;
+   EXEC sp_addrolemember 'db_owner', 'TDG2';
+   ```
+   *Si ya existe, verifica que el usuario tenga permisos de lectura/escritura.*
+
+2. **Variables opcionales**: define `JWT_SECRET` si deseas una clave distinta; de lo contrario se usa `change-me-256bits-key`.
+
+3. **Ejecutar el servicio**:
+   ```bash
+   cd backend
+   ./mvnw spring-boot:run
+   ```
+   El proceso levanta en `http://localhost:8081` y recrea el esquema con la semilla de `data.sql`. Los logs de SQL aparecerán porque `spring.jpa.show-sql=true`.
+
+4. **Compilar pruebas** (opcional):
+   ```bash
+   ./mvnw test
+   ```
+   *Las pruebas unitarias usan Mockito y JUnit 5; asegúrate de que la base esté accesible para los tests que necesiten datos.*
+
+### Herramientas para pruebas manuales
+
+- **`src/main/resources/requests.http`**: colección de peticiones REST (formato HTTP Client de IntelliJ/VS Code) para autenticar, registrar medidores, consultar consumos y gestionar alertas.
+- **`data.sql`**: referencia rápida de IDs, correos y tokens de ejemplo cuando pruebes consultas específicas (por ejemplo medidor `a01b2edc-51e9-45d2-8b34-f126c36e9c8d`).【F:backend/src/main/resources/data.sql†L1-L120】
+
+---
+
+## Frontend (React + Vite)
+
+La SPA está construida con **React 18**, **TypeScript**, **Vite 6** y el stack de componentes de Radix/ShadCN. Emplea Tailwind CSS 4 para estilos utilitarios y Recharts para gráficas.
+
+### Estructura y componentes principales
+
+- **Estado global**:
+  - `AuthProvider`: gestiona login/registro/logout, persiste tokens en `localStorage` y expone el usuario autenticado a toda la app.【F:frontend/src/components/AuthContext.tsx†L1-L83】
+  - `InmuebleProvider`: carga medidores del usuario mediante `/api/my/meters`, guarda el medidor activo y refresca la lista cuando se crean/actualizan/eliminan contadores.【F:frontend/src/components/InmuebleContext.tsx†L1-L92】
+- **Layout**: `App.tsx` controla el flujo de autenticación, modo oscuro, navegación lateral y vistas principales (Dashboard, Mi Consumo, Mi Contador, Proyección, Alertas, Configuración, Gestión de Inmuebles).【F:frontend/src/App.tsx†L1-L145】
+- **Componentes de UI**: en `components/ui` se agrupan wrappers reutilizables basados en Radix UI (modales, formularios, tablas, etc.).
+- **Documentación auxiliar**: archivos como `API_CONFIG.md` o `README_ES.md` explican cómo integrar el frontend con el backend o ejecutar en modo mock para pruebas rápidas.
+
+### Servicios de datos y consumo de API
+
+- **Cliente HTTP (`services/api.ts`)**: encapsula `fetch`, agrega el header `Authorization: Bearer <token>`, normaliza errores del backend y define la base `http://localhost:8081/api`.【F:frontend/src/services/api.ts†L1-L86】
+- **Autenticación (`authService.ts`)**: llama a `/auth/login` y `/auth/register`, guarda `accessToken`/`refreshToken` y realiza logout limpiando `localStorage`.【F:frontend/src/services/authService.ts†L1-L47】
+- **Medidores (`meterService.ts`)**: implementa `listMyMeters`, `createMeter`, `updateMeter` y `deleteMeter` contra los endpoints protegidos. Los tipos reflejan lo que entrega el backend. 【F:frontend/src/services/meterService.ts†L1-L64】
+- **Consumo (`consumoService.ts`)**: obtiene consumo mensual/anual, series para gráficos y comparaciones real vs proyección, reutilizando parámetros como granularidad y periodo. 【F:frontend/src/services/consumoService.ts†L1-L99】
+- **Cuotas (`quotaService.ts`)**: consulta y actualiza el límite mensual en kWh de un medidor (`/quota/active`, `/quota`).【F:frontend/src/services/quotaService.ts†L1-L40】
+
+### Ejecución en desarrollo
+
+1. **Requisitos**: Node.js 18+ (idealmente 20 LTS) y npm.
+2. **Instalación**:
+   ```bash
+   cd frontend
+   npm install
+   ```
+3. **Arranque**:
+   ```bash
+   npm run dev
+   ```
+   Vite abrirá la app en `http://localhost:3000` (ver `server.port` en `vite.config.ts`). Si deseas otra URL, ajusta el puerto o añade más orígenes permitidos en el backend (`CorsConfig`).
+4. **Build de producción**:
+   ```bash
+   npm run build
+   ```
+   El artefacto queda en `frontend/build` listo para ser servido por un web server estático.
+
+---
+
+## Integración extremo a extremo
+
+1. **Inicia SQL Server** y verifica que el login `TDG2` tenga acceso a la base `TG2`.
+2. **Levanta el backend** (`./mvnw spring-boot:run`). Confirma en la consola que el banner de Spring Boot aparece y que Hibernate ejecuta el `create-drop`.
+3. **Levanta el frontend** (`npm run dev`). Al autenticarse, el flujo de `AuthProvider` almacenará el `accessToken` y todas las solicitudes subsecuentes enviarán el header Bearer automáticamente.
+4. **Swagger y pruebas**: visita `http://localhost:8081/swagger-ui/index.html` para probar endpoints antes de conectarlos desde la SPA.
+
+### Usuarios y datos iniciales
+
+`data.sql` carga tres usuarios y sus contraseñas (en comentarios claros):
+
+| Rol | Email | Contraseña en texto claro |
+|-----|-------|---------------------------|
+| Cliente | `carlos.perez@example.com` | `Password123!` |
+| Administrador | `admin@example.com` | `AdminPass!2025` |
+| Cliente | `laura.gomez@example.com` | `TestPass!2025` |
+
+También se crean medidores asociados, lecturas (`meter_readings`), consumos diarios/mensuales, cuotas (`meter_quotas`) y sesiones de refresh token, por lo que podrás ver datos reales en los dashboards inmediatamente.【F:backend/src/main/resources/data.sql†L5-L120】【F:backend/src/main/resources/data.sql†L121-L240】
+
+### Notas de despliegue y CORS
+
+- El backend permite el origen `http://localhost:3000` y métodos `GET/POST/PUT/PATCH/DELETE/OPTIONS`. Si ejecutas Vite en otro puerto (ej. 5173) o expones la app en producción, actualiza `CorsConfig` y las variables de entorno correspondientes.【F:backend/src/main/java/com/ucdc/backend/infrastructure/config/CorsConfig.java†L16-L37】
+- Para publicar el backend fuera de tu máquina local, ajusta `spring.datasource.url` para apuntar al host/puerto público del SQL Server y habilita TLS según sea necesario (`encrypt=true;trustServerCertificate=true` ya está activo para entornos locales).
+- Considera cambiar `spring.jpa.hibernate.ddl-auto` a `update` o gestionar migraciones con Flyway/Liquibase cuando pases a ambientes productivos.
+
+---
+
+## Resolución de problemas frecuentes
+
+| Problema | Posible causa / solución |
+|----------|-------------------------|
+| `Login failed for user 'TDG2'` al arrancar el backend | Verifica que el login exista en SQL Server y que la contraseña coincida (`TDG2025*`). Ajusta `application.properties` si usas credenciales distintas. |
+| Error `Failed to fetch` en el frontend | El backend no está disponible en `localhost:8081` o hay un problema de CORS. Confirma que Spring Boot esté en ejecución y que `CorsConfig` incluya el origen correcto. |
+| `HTTP 401 Unauthorized` en peticiones protegidas | Asegúrate de iniciar sesión desde la SPA para obtener un `accessToken` válido o incluye el header Bearer en herramientas externas. |
+| Datos se reinician al reiniciar el backend | Es el comportamiento esperado con `spring.jpa.hibernate.ddl-auto=create-drop`. Cambia a `update` o desactiva la inicialización automática si necesitas persistencia estable. |
+| Diferencia entre puertos 3000 y 5173 | Vite está configurado explícitamente para usar `3000`. Si cambias este valor, recuerda actualizar `CorsConfig` y, si aplica, el `API_BASE_URL` en `services/api.ts`. |
+
+---
+
+¡Con esto deberías poder ejecutar y entender el proyecto completo, desde la capa de presentación hasta la base de datos SQL Server local!
