# 🌟 Gestión Energética ENEL - Documentación

## 🚀 Inicio Rápido

### Estado Actual: ✅ Modo Desarrollo (Mock)

La aplicación está lista para usar **sin necesidad de backend**:

```typescript
// /services/api.ts
USE_MOCK_MODE = true  // ✅ Activo
```

### 🔐 Iniciar Sesión

```
Email: test@enel.com
Password: test123
```

O cualquier otra combinación (el modo mock acepta todo).

---

## 📚 Documentación Disponible

| Archivo | Descripción |
|---------|-------------|
| `/MODO_DESARROLLO.md` | **👈 Leer primero** - Guía del modo mock |
| `/BACKEND_INTEGRATION.md` | Integración con Spring Boot |
| `/GESTION_CONTADORES.md` | CRUD de contadores |
| `/METER_SERVICE.md` | Servicio completo de API |
| `/INTEGRACION_EJEMPLO.md` | Ejemplos de uso |
| `/API_CONFIG.md` | Configuración de endpoints |

---

## 🎯 Características Principales

### ✅ Completamente Funcional en Modo Mock

- ✅ **Autenticación** - Login/Signup simulado
- ✅ **Dashboard** - Vista principal con estadísticas
- ✅ **Contadores** - Gestión CRUD completa
- ✅ **Consumo** - Gráficos por día/semana/mes
- ✅ **Datos Técnicos** - Voltaje, corriente, potencia
- ✅ **Alertas** - Notificaciones de consumo
- ✅ **Configuración** - Preferencias de usuario
- ✅ **Responsive** - Mobile y Desktop

---

## 🏗️ Estructura del Proyecto

```
/
├── components/          # Componentes React
│   ├── Dashboard.tsx   # Vista principal
│   ├── GestionInmuebles.tsx  # CRUD de contadores
│   ├── MiConsumo.tsx   # Gráficos de consumo
│   ├── Alertas.tsx     # Sistema de alertas
│   └── ...
├── services/           # Servicios de API
│   ├── api.ts         # Cliente HTTP base
│   ├── authService.ts # Autenticación
│   └── meterService.ts # Servicio de contadores
└── styles/
    └── globals.css    # Estilos globales Tailwind
```

---

## 🔄 Cambiar entre Modos

### Modo Mock (Actual) ✅
```typescript
// /services/api.ts
export const USE_MOCK_MODE = true
```

**Ventajas:**
- ✅ No necesita backend
- ✅ Sin errores de conexión
- ✅ Desarrollo rápido

**Limitaciones:**
- ❌ Datos no persisten (solo en sesión)
- ❌ Solo 2 contadores iniciales

### Modo Backend
```typescript
// /services/api.ts
export const USE_MOCK_MODE = false
```

**Requisitos:**
- ✅ Backend Spring Boot en `http://localhost:8081`
- ✅ Endpoints implementados:
  - `GET /api/my/meters`
  - `POST /api/meters`
  - `PUT /api/meters/{id}`
  - `DELETE /api/meters/{id}`
- ✅ CORS configurado

---

## 📊 Datos Mock Incluidos

### 2 Contadores Predefinidos:

#### 1. Apartamento Centro
```json
{
  "id": "a01b2edc-51e9-45d2-8b34-f126c36e9c8d",
  "alias": "Apartamento Centro",
  "serviceNumber": "1234565",
  "serialNumber": "SNR-ENE-00123",
  "provider": "ENEL",
  "status": "ACTIVE",
  "type": "SMART"
}
```

#### 2. Casa de Playa
```json
{
  "id": "b12c3fde-62fa-56e3-9c45-g237d47f0d9e",
  "alias": "Casa de Playa",
  "serviceNumber": "7891234",
  "serialNumber": "SNR-ENE-00456",
  "provider": "ENEL",
  "status": "ACTIVE",
  "type": "SMART"
}
```

---

## 🎨 Paleta de Colores

### Color Principal: Azul ENEL
```css
--color-primary: #0089CF  /* Azul ENEL */
--color-secondary: #E6F4FB
--color-accent: #D1ECFA
```

---

## 🛠️ Tecnologías

- **React 18** - Framework UI
- **TypeScript** - Tipado estático
- **Tailwind CSS 4.0** - Estilos
- **Recharts** - Gráficos
- **Motion (Framer Motion)** - Animaciones
- **Lucide React** - Iconos
- **ShadCN UI** - Componentes

---

## 🔧 Scripts Disponibles

```bash
npm run dev        # Iniciar desarrollo
npm run build      # Compilar para producción
npm run preview    # Previsualizar build
```

---

## 📱 Vistas Principales

### 1. Dashboard (Home)
- Selector de contador
- Consumo actual
- Proyección de gasto
- Gráficos interactivos

### 2. Mi Consumo
- Gráficos por día/semana/mes
- Costos detallados
- Comparativas

### 3. Datos Técnicos
- Voltaje, corriente, potencia
- Frecuencia, factor de potencia
- Última lectura

### 4. Proyección
- Pronóstico de consumo
- Estimación de costos
- Tendencias

### 5. Mi Contador
- Estado del contador
- Ubicación
- Tipo de medidor

### 6. Alertas
- Notificaciones de consumo alto
- Recomendaciones
- Marcar como leída

### 7. Configuración
- Notificaciones
- Modo oscuro
- Gestión de contadores
- Privacidad

### 8. Gestión de Contadores
- Crear nuevo contador
- Editar contador
- Eliminar contador
- Ver lista completa

---

## 🔐 Sistema de Autenticación

### En Modo Mock:
- Cualquier email/password funciona
- Genera token JWT simulado
- Almacena en localStorage

### En Modo Backend:
- Validación real de credenciales
- JWT real del backend
- Refresh token support

---

## 📋 Endpoints de Backend Esperados

### Autenticación
```
POST /api/auth/login
POST /api/auth/signup
POST /api/auth/logout
```

### Contadores
```
GET    /api/my/meters          # Listar contadores del usuario
POST   /api/meters             # Crear contador
PUT    /api/meters/{id}        # Actualizar contador
DELETE /api/meters/{id}        # Eliminar contador
GET    /api/meters/{id}        # Obtener un contador
```

### Consumo
```
GET /api/meters/{id}/consumo?periodo=dia|semana|mes
GET /api/meters/{id}/datos-tecnicos
GET /api/meters/{id}/alertas
PUT /api/meters/{id}/alertas/{alertaId}/leida
```

---

## 🎯 Flujo de Usuario

```
1. Login (test@enel.com / test123)
   ↓
2. Dashboard - Ver contadores disponibles
   ↓
3. Selector - Cambiar entre "Apartamento" y "Casa"
   ↓
4. Ver consumo, alertas, datos técnicos
   ↓
5. Configuración → Gestionar → CRUD de contadores
```

---

## 🐛 Solución de Problemas

### No aparecen los contadores
**Solución:** Verifica que `USE_MOCK_MODE = true`

### Error "Failed to fetch"
**Solución:** Cambia `USE_MOCK_MODE = true` en `/services/api.ts`

### Los datos no se guardan
**Esperado en modo mock.** Para persistencia, usar backend real.

### El indicador amarillo no aparece
**Solución:** Recarga la página. El indicador aparece solo si `USE_MOCK_MODE = true`

---

## ✨ Próximos Pasos

### Para Desarrollo Frontend:
1. ✅ Mantener `USE_MOCK_MODE = true`
2. ✅ Desarrollar componentes
3. ✅ Mejorar UI/UX
4. ✅ Agregar funcionalidades

### Para Integración Backend:
1. ⏳ Implementar backend Spring Boot
2. ⏳ Configurar base de datos
3. ⏳ Implementar endpoints
4. ⏳ Cambiar `USE_MOCK_MODE = false`
5. ⏳ Probar integración completa

---

## 📞 Contacto

Para más información, consulta los archivos de documentación en la raíz del proyecto.

---

## 📄 Licencia

© 2025 ENEL - Gestión Energética. Todos los derechos reservados.
