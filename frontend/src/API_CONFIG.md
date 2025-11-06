# 🔌 Configuración de Integración con API Spring Boot

## 🟡 MODO ACTUAL: MOCK (Datos de Prueba)

La aplicación está configurada en **MODO MOCK** por defecto. Esto significa que funciona sin necesidad de backend.

### 🎯 Para usar la aplicación AHORA:
```
Email: test@enel.com
Password: test123
```

### 🔧 Para cambiar a modo PRODUCCIÓN:
Edita `/services/api.ts` línea 6:
```typescript
export const USE_MOCK_MODE = false  // Cambiar a false
```

---

## ✅ Archivos Creados

### 1. `/services/api.ts`
Configuración base para comunicación con tu API REST.

**URL Base configurada:** `http://localhost:8081/api`
**Modo Mock:** Activo (cambiar `USE_MOCK_MODE = false` para usar backend real)

### 2. `/services/authService.ts`
Servicio de autenticación que maneja login, register y gestión del accessToken.

### 3. `/components/AuthContext.tsx`
Contexto React para manejar el estado de autenticación globalmente.

---

## 🔧 Ajustes Necesarios en tu Backend Spring Boot

### 1. **Configurar CORS**
Agrega esta configuración en tu Spring Boot para permitir peticiones desde el frontend:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:3000") // Puertos de desarrollo
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

### 2. **Endpoints Esperados**

#### **Login**
- **URL:** `POST http://localhost:8081/api/auth/login`
- **Body:**
```json
{
  "email": "correo@ejemplo.com",
  "password": "contraseña123"
}
```
- **Respuesta esperada:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "tokenType": "Bearer",
  "user": {
    "id": "1",
    "email": "correo@ejemplo.com",
    "nombre": "Juan Pérez"
  }
}
```

#### **Register**
- **URL:** `POST http://localhost:8081/api/auth/register`
- **Body:**
```json
{
  "nombreCompleto": "Juan Pérez",
  "cedula": "1234567890",
  "correo": "correo@ejemplo.com",
  "numeroServicio": "000000000000",
  "telefono": "0987654321",
  "password": "contraseña123"
}
```
- **Respuesta esperada:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "tokenType": "Bearer",
  "user": {
    "id": "1",
    "email": "correo@ejemplo.com",
    "nombre": "Juan Pérez"
  }
}
```

---

## 🔐 Gestión del AccessToken

El frontend guarda el `accessToken` en `localStorage` y automáticamente lo incluye en todas las peticiones:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

### Verificar Token en Spring Boot
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) {
        String token = extractToken(request);
        if (token != null && validateToken(token)) {
            // Token válido
        }
    }
    
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

---

## 🎯 Personalizar Rutas de la API

Si tus endpoints son diferentes, modifica `/services/authService.ts`:

```typescript
// Login
const response = await api.post<LoginResponse>('/auth/login', {...})

// Si tu ruta es /api/usuario/login en lugar de /api/auth/login:
const response = await api.post<LoginResponse>('/usuario/login', {...})

// Si tu ruta es /api/v1/auth/login:
const response = await api.post<LoginResponse>('/v1/auth/login', {...})
```

---

## 🌐 Cambiar la URL Base del API

Para cambiar el puerto o dominio, edita `/services/api.ts`:

```typescript
// Desarrollo local
const API_BASE_URL = 'http://localhost:8081/api'

// Producción
const API_BASE_URL = 'https://tu-dominio.com/api'

// Usar variable de entorno
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8081/api'
```

---

## 📝 Ejemplo de Uso en Otros Componentes

Para hacer peticiones autenticadas desde otros componentes:

```typescript
import api from '../services/api'

// GET
const consumo = await api.get('/consumo/usuario/123')

// POST
const nuevoConsumo = await api.post('/consumo', {
  kwh: 150,
  fecha: '2025-11-02'
})

// PUT
const actualizado = await api.put('/usuario/123', {
  nombre: 'Nuevo Nombre'
})

// DELETE
await api.delete('/consumo/456')
```

---

## 🧪 Probar la Conexión

1. Asegúrate de que tu Spring Boot esté corriendo en `http://localhost:8081`
2. Abre la consola del navegador (F12)
3. Intenta hacer login
4. Verás las peticiones en la pestaña "Network"

---

## ⚠️ Manejo de Errores

El frontend muestra mensajes de error automáticamente:
- ❌ Credenciales incorrectas
- ❌ Usuario ya existe
- ❌ Error de conexión
- ❌ Campos inválidos

Asegúrate de que tu Spring Boot devuelva mensajes descriptivos en las respuestas de error:

```json
{
  "message": "El correo ya está registrado",
  "error": "DUPLICATE_EMAIL"
}
```

---

## 🔄 Flujo de Autenticación

1. Usuario ingresa credenciales en Login/SignUp
2. Frontend envía petición a Spring Boot
3. Spring Boot valida y devuelve `accessToken`
4. Frontend guarda token en `localStorage`
5. Todas las peticiones subsecuentes incluyen el token
6. Al cerrar sesión, el token se elimina

---

## 📦 Dependencias Adicionales

No se requieren dependencias adicionales. Todo usa `fetch` nativo del navegador.

---

## 🆘 Troubleshooting

### Error: "CORS policy"
- Configura CORS en Spring Boot (ver sección 1)

### Error: "Network request failed"
- Verifica que Spring Boot esté corriendo
- Verifica la URL y puerto en `/services/api.ts`

### Error: "401 Unauthorized"
- El token expiró o es inválido
- Verifica la validación del token en Spring Boot

### El login no funciona
- Abre la consola (F12) → Network
- Verifica la petición y respuesta
- Compara con los formatos esperados arriba
