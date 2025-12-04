# 📚 APUNTES DETALLADOS - Sistema de Gestión de Congresos Académicos

## 📋 ÍNDICE
1. [Visión General del Sistema](#visión-general)
2. [Arquitectura de Microservicios](#arquitectura)
3. [Tecnologías Utilizadas](#tecnologías)
4. [Backend Principal - Estructura Detallada](#backend-principal)
5. [Modelo de Datos (Entidades)](#modelo-de-datos)
6. [Seguridad y Autenticación](#seguridad)
7. [Microservicios Auxiliares](#microservicios)
8. [Frontend Vue.js](#frontend)
9. [Flujo de Datos Completo](#flujo-de-datos)
10. [Casos de Uso Implementados](#casos-de-uso)

---

## 🎯 VISIÓN GENERAL

### ¿Qué es este sistema?
Es una **aplicación web completa** para gestionar congresos académicos que permite:
- A los **asistentes**: registrarse, inscribirse en congresos y sesiones, pagar, obtener tickets QR
- A los **organizadores**: crear y gestionar congresos, ver estadísticas
- Al **staff**: escanear códigos QR para controlar accesos

### Arquitectura General
```
┌─────────────┐      ┌──────────────────┐      ┌─────────────┐
│  Frontend   │ ───> │ Backend Principal│ <──> │  PostgreSQL │
│  (Vue.js)   │      │  (Spring Boot)   │      │  (Base de   │
└─────────────┘      └──────────────────┘      │   Datos)    │
                              │                 └─────────────┘
                              │
                    ┌─────────┴──────────┐
                    ▼                    ▼
          ┌──────────────────┐  ┌──────────────────┐
          │ MS Verificador   │  │ MS Ticket        │
          │   Adapter        │─>│   Authority      │
          │ (Spring Boot)    │  │ (Spring Boot)    │
          └──────────────────┘  └──────────────────┘
```

---

## 🏗️ ARQUITECTURA

### Servicios del Sistema

#### 1. **Frontend** (Puerto 5173)
- **Tecnología**: Vue.js 3 + Vite
- **Función**: Interfaz de usuario
- **Usuarios**: Asistentes, Organizadores, Staff

#### 2. **Backend Principal** (Puerto 8080)
- **Tecnología**: Spring Boot 3.5.0 + Java 21
- **Función**: Lógica de negocio principal
- **Gestiona**: Usuarios, Congresos, Inscripciones, Pagos, Sesiones

#### 3. **MS Ticket Authority** (Puerto 8081)
- **Tecnología**: Spring Boot 3.5.0
- **Función**: Servicio externo para generar y validar tickets QR con firma criptográfica
- **Es independiente**: Simula un servicio de terceros

#### 4. **MS Verificador Adapter** (Puerto 8082)
- **Tecnología**: Spring Boot 3.5.0
- **Función**: Adaptador entre Backend Principal y Ticket Authority
- **Patrón**: Adapter Pattern (encapsula la comunicación)

#### 5. **Base de Datos** (Puerto 5432)
- **Tecnología**: PostgreSQL 15
- **Función**: Persistencia de datos
- **Nombre**: `congresos_db`

---

## 💻 TECNOLOGÍAS

### Backend
- **Java 21**: Lenguaje de programación
- **Spring Boot 3.5.0**: Framework principal
  - Spring Web: APIs REST
  - Spring Data JPA: Acceso a datos
  - Spring Security: Autenticación y autorización
- **Hibernate**: ORM (mapeo objeto-relacional)
- **PostgreSQL**: Base de datos relacional
- **JWT (jjwt 0.12.3)**: Tokens de autenticación
- **Lombok**: Reduce código boilerplate
- **Maven**: Gestión de dependencias

### Frontend
- **Vue.js 3**: Framework JavaScript reactivo
- **Vite**: Build tool rápido
- **Vue Router**: Navegación entre páginas
- **Pinia**: State management (gestión de estado)
- **Axios**: Cliente HTTP para llamadas al backend

### DevOps
- **Docker**: Contenedores
- **Docker Compose**: Orquestación multi-contenedor

---

## 🔧 BACKEND PRINCIPAL

### Estructura del Proyecto

```
backend-principal/
├── src/main/java/com/congresos/backend/
│   ├── BackendPrincipalApplication.java  [PUNTO DE ENTRADA]
│   ├── config/
│   │   └── SecurityConfig.java           [CONFIGURACIÓN SEGURIDAD]
│   ├── security/
│   │   ├── JwtService.java               [SERVICIO JWT]
│   │   ├── JwtAuthenticationFilter.java  [FILTRO JWT]
│   │   └── UserDetailsServiceImpl.java   [CARGA USUARIOS]
│   ├── model/domain/
│   │   ├── Usuario.java                  [ENTIDAD USUARIO]
│   │   ├── Congreso.java                 [ENTIDAD CONGRESO]
│   │   ├── Sesion.java                   [ENTIDAD SESIÓN]
│   │   ├── InscripcionCongreso.java      [ENTIDAD INSCRIPCIÓN CONGRESO]
│   │   ├── InscripcionSesion.java        [ENTIDAD INSCRIPCIÓN SESIÓN]
│   │   ├── Pago.java                     [ENTIDAD PAGO]
│   │   └── RegistroAcceso.java           [ENTIDAD REGISTRO ACCESO]
│   ├── repository/
│   │   ├── UsuarioRepository.java        [REPOSITORIO USUARIO]
│   │   ├── CongresoRepository.java       [REPOSITORIO CONGRESO]
│   │   ├── SesionRepository.java         [REPOSITORIO SESIÓN]
│   │   ├── InscripcionCongresoRepository.java
│   │   ├── InscripcionSesionRepository.java
│   │   ├── PagoRepository.java
│   │   └── RegistroAccesoRepository.java
│   └── exception/
│       ├── GlobalExceptionHandler.java   [MANEJO ERRORES GLOBAL]
│       ├── BusinessException.java        [EXCEPCIÓN NEGOCIO]
│       └── NotFoundException.java        [EXCEPCIÓN NO ENCONTRADO]
└── src/main/resources/
    └── application.properties            [CONFIGURACIÓN]
```

---

## 📊 MODELO DE DATOS

### 1. **ENTIDAD `Usuario`** 
**Archivo**: `Usuario.java`

**¿Qué es?** Representa a cualquier usuario del sistema (asistente, organizador o staff)

**Atributos principales**:
```java
- idUsuario (Long): ID único
- nombreCompleto (String): Nombre del usuario
- email (String): Email único (usado para login)
- password (String): Contraseña encriptada
- tipoUsuario (Enum): ASISTENTE, ORGANIZADOR o STAFF
- organizacion (String): Organización del usuario
- fotoPerfil (String): URL de la foto
- activo (Boolean): Si está activo
- fechaRegistro (LocalDateTime): Cuándo se registró
```

**Anotaciones importantes**:
- `@Entity`: Marca la clase como una entidad JPA (tabla en BD)
- `@Table(name = "usuarios")`: Nombre de la tabla en PostgreSQL
- `@Id`: Marca el campo como clave primaria
- `@GeneratedValue`: El ID se genera automáticamente
- `@Column`: Define propiedades de la columna (nullable, unique, length)
- `@Enumerated`: Para campos enum (tipoUsuario)
- `@CreationTimestamp`: Se rellena automáticamente al crear
- `@UpdateTimestamp`: Se actualiza automáticamente al modificar

**Relaciones**:
- Un Usuario puede ser organizador de muchos Congresos
- Un Usuario (asistente) puede tener muchas InscripcionCongreso
- Un Usuario (asistente) puede tener muchas InscripcionSesion
- Un Usuario (staff) puede realizar muchos RegistroAcceso

---

### 2. **ENTIDAD `Congreso`**
**Archivo**: `Congreso.java`

**¿Qué es?** Representa un congreso académico (evento principal)

**Atributos principales**:
```java
- idCongreso (Long): ID único
- nombre (String): Nombre del congreso
- descripcion (String): Descripción larga
- fechaInicio (LocalDate): Fecha de inicio
- fechaFin (LocalDate): Fecha de fin
- lugar (String): Ubicación física
- ciudad (String): Ciudad
- precio (BigDecimal): Precio de inscripción
- esPago (Boolean): Si requiere pago
- imagenPortada (String): URL imagen
- tematica (String): Temática del congreso
- activo (Boolean): Si está activo
```

**Relaciones**:
```java
@ManyToOne
- organizador (Usuario): El organizador del congreso

@OneToMany
- sesiones (List<Sesion>): Las sesiones del congreso
- inscripciones (List<InscripcionCongreso>): Inscripciones al congreso
```

**Métodos útiles**:
- `haFinalizado()`: Verifica si ya terminó
- `estaEnCurso()`: Verifica si está en curso ahora

**Cascadas**: `CascadeType.ALL` significa que si eliminas un Congreso, se eliminan todas sus Sesiones e Inscripciones

---

### 3. **ENTIDAD `Sesion`**
**Archivo**: `Sesion.java`

**¿Qué es?** Una sesión dentro de un congreso (charla, taller, conferencia)

**Atributos principales**:
```java
- idSesion (Long): ID único
- titulo (String): Título de la sesión
- descripcion (String): Descripción
- ponente (String): Nombre del ponente
- sala (String): Sala donde se realiza
- fechaHoraInicio (LocalDateTime): Inicio
- fechaHoraFin (LocalDateTime): Fin
- aforoMaximo (Integer): Plazas totales
- aforoActual (Integer): Plazas ocupadas
- activa (Boolean): Si está activa
```

**Relaciones**:
```java
@ManyToOne
- congreso (Congreso): El congreso al que pertenece

@OneToMany
- inscripciones (List<InscripcionSesion>): Inscripciones a esta sesión
```

**Métodos de negocio**:
- `hayPlazasDisponibles()`: Comprueba si hay aforo
- `incrementarAforo()`: Suma 1 al aforo actual
- `decrementarAforo()`: Resta 1 al aforo actual
- `tieneConflictoHorarioCon(Sesion)`: Detecta solapamiento horario

---

### 4. **ENTIDAD `InscripcionCongreso`**
**Archivo**: `InscripcionCongreso.java`

**¿Qué es?** Representa la inscripción de un asistente a un congreso

**Atributos principales**:
```java
- idInscripcion (Long): ID único
- estado (Enum): PENDIENTE, CONFIRMADA, CANCELADA
- ticketQR (String): Código QR en formato texto
- ticketId (String): ID del ticket generado por MS Ticket Authority
- activa (Boolean): Si está activa
- fechaInscripcion (LocalDateTime): Cuándo se inscribió
- fechaCancelacion (LocalDateTime): Si se canceló
```

**Relaciones**:
```java
@ManyToOne
- asistente (Usuario): El usuario que se inscribió
- congreso (Congreso): El congreso

@OneToOne
- pago (Pago): El pago asociado (si es de pago)
```

**Restricción única**:
```java
@UniqueConstraint(columnNames = {"id_asistente", "id_congreso"})
```
Esto significa que **un usuario solo puede inscribirse UNA VEZ al mismo congreso**

**Métodos**:
- `confirmar()`: Cambia estado a CONFIRMADA
- `cancelar()`: Cancela la inscripción

---

### 5. **ENTIDAD `InscripcionSesion`**
**Archivo**: `InscripcionSesion.java`

**¿Qué es?** Inscripción de un asistente a una sesión específica

**Similar a InscripcionCongreso pero más simple** (no tiene pago, solo ticket QR)

**Relaciones**:
```java
@ManyToOne
- asistente (Usuario): El usuario inscrito
- sesion (Sesion): La sesión
```

**Restricción única**: Un usuario solo puede inscribirse una vez a la misma sesión

---

### 6. **ENTIDAD `Pago`**
**Archivo**: `Pago.java`

**¿Qué es?** Representa el pago de una inscripción a un congreso de pago

**Atributos principales**:
```java
- idPago (Long): ID único
- monto (BigDecimal): Cantidad pagada
- estado (Enum): PENDIENTE, PROCESANDO, COMPLETADO, FALLIDO, REEMBOLSADO
- metodoPago (Enum): TARJETA_CREDITO, PAYPAL, BIZUM, etc.
- transaccionId (String): ID de la pasarela de pago
- fechaPago (LocalDateTime): Cuándo se creó
- fechaConfirmacion (LocalDateTime): Cuándo se confirmó
```

**Relación**:
```java
@OneToOne
- inscripcionCongreso (InscripcionCongreso): La inscripción pagada
```

**Métodos**:
- `confirmar()`: Marca como COMPLETADO
- `marcarFallido()`: Marca como FALLIDO

---

### 7. **ENTIDAD `RegistroAcceso`**
**Archivo**: `RegistroAcceso.java`

**¿Qué es?** Registra cada escaneo de QR (para control de acceso)

**Atributos principales**:
```java
- idRegistro (Long): ID único
- tipoAcceso (Enum): CONGRESO o SESION
- resultado (Enum): VALIDO, YA_USADO, FALSIFICADO, CADUCADO, etc.
- gateId (String): ID de la puerta/sala donde se escaneó
- qrEscaneado (String): El código QR escaneado
- observaciones (String): Notas adicionales
- fechaHoraEscaneo (LocalDateTime): Cuándo se escaneó
```

**Relaciones**:
```java
@ManyToOne
- usuario (Usuario): El asistente escaneado
- congreso (Congreso): Si es acceso a congreso
- sesion (Sesion): Si es acceso a sesión
- staff (Usuario): El staff que escaneó
```

---

## 🔐 SEGURIDAD Y AUTENTICACIÓN

### Cómo funciona JWT (JSON Web Token)

**1. Login del Usuario**:
```
Usuario → Frontend → Backend → Verifica credenciales
                                ↓
                         Genera JWT Token
                                ↓
                         Devuelve token al Frontend
                                ↓
                    Frontend guarda en localStorage
```

**2. Peticiones Autenticadas**:
```
Frontend → Añade header "Authorization: Bearer <token>"
           ↓
Backend → JwtAuthenticationFilter intercepta
           ↓
       Valida el token
           ↓
       Extrae usuario del token
           ↓
       Autentica en SecurityContext
           ↓
       Permite acceso al endpoint
```

### Componentes de Seguridad

#### **JwtService.java** - Servicio de Tokens
**¿Qué hace?**
- **Genera tokens JWT** con información del usuario
- **Valida tokens** (firma, expiración)
- **Extrae información** del token (username, claims)

**Métodos principales**:
```java
generateToken(UserDetails user): Crea un token nuevo
extractUsername(String token): Extrae el email del token
isTokenValid(String token, UserDetails user): Valida el token
```

**Configuración**:
- `jwt.secret`: Clave secreta para firmar tokens (en application.properties)
- `jwt.expiration`: 86400000 ms = 24 horas

#### **JwtAuthenticationFilter.java** - Filtro de Autenticación
**¿Qué hace?**
Es un **filtro que intercepta TODAS las peticiones HTTP** antes de llegar a los controladores

**Flujo**:
1. Lee el header `Authorization`
2. Si existe y empieza con "Bearer ", extrae el token
3. Valida el token con JwtService
4. Si es válido, autentica al usuario en SecurityContext
5. Continúa con la petición

**Anotación**: `@Component` lo registra como bean de Spring

#### **SecurityConfig.java** - Configuración de Seguridad
**¿Qué hace?**
Configura toda la seguridad de la aplicación

**Configuraciones importantes**:

1. **Rutas públicas** (no requieren login):
```java
.requestMatchers("/api/auth/**").permitAll()  // Login/Registro
.requestMatchers("/api/congresos/buscar").permitAll()  // Búsqueda pública
```

2. **Rutas protegidas por ROL**:
```java
.requestMatchers("/api/inscripciones/**").hasAnyRole("ASISTENTE", "ORGANIZADOR")
.requestMatchers("/api/congresos/crear").hasRole("ORGANIZADOR")
.requestMatchers("/api/escaneo/**").hasRole("STAFF")
```

3. **CORS** (Cross-Origin Resource Sharing):
Permite que el frontend (puerto 5173) haga peticiones al backend (puerto 8080)

4. **CSRF deshabilitado**: 
Porque usamos JWT (no cookies de sesión)

5. **Session Management**:
`STATELESS` → No mantiene sesión en servidor, todo vía tokens

---

## 🔄 REPOSITORIOS (Acceso a Datos)

### ¿Qué es un Repository?

Un **Repository** es una interfaz que **extiende JpaRepository** y proporciona métodos para acceder a la base de datos **SIN ESCRIBIR SQL**.

### Ejemplo: **UsuarioRepository.java**

```java
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

**¿Qué hace?**
- `extends JpaRepository<Usuario, Long>`: 
  - `Usuario`: La entidad que gestiona
  - `Long`: El tipo del ID

**Métodos automáticos** (heredados de JpaRepository):
- `save(Usuario)`: Guarda o actualiza
- `findById(Long id)`: Busca por ID
- `findAll()`: Devuelve todos
- `deleteById(Long id)`: Elimina por ID
- `count()`: Cuenta registros

**Métodos personalizados**:
- `findByEmail(String email)`: Spring genera automáticamente el SQL
  - SQL generado: `SELECT * FROM usuarios WHERE email = ?`
- `existsByEmail(String email)`: Verifica si existe
  - SQL generado: `SELECT COUNT(*) > 0 FROM usuarios WHERE email = ?`

**Spring Data JPA** es "mágico" porque:
- Genera automáticamente la implementación
- Solo defines el método, no el código
- Sigue convenciones de nombres: `findBy`, `existsBy`, `countBy`, etc.

---

## 🎨 FRONTEND VUE.JS

### Estructura del Frontend

```
frontend/
├── src/
│   ├── main.js                    [PUNTO DE ENTRADA]
│   ├── App.vue                    [COMPONENTE RAÍZ]
│   ├── router/
│   │   └── index.js               [CONFIGURACIÓN RUTAS]
│   ├── stores/
│   │   └── auth.js                [ESTADO AUTENTICACIÓN (Pinia)]
│   ├── services/
│   │   └── http.js                [CLIENTE HTTP (Axios)]
│   └── views/
│       ├── LoginView.vue          [VISTA LOGIN]
│       ├── RegistroView.vue       [VISTA REGISTRO]
│       ├── CatalogoView.vue       [CATÁLOGO CONGRESOS]
│       ├── CongresoDetalleView.vue [DETALLE CONGRESO]
│       ├── MisCongresosView.vue   [MIS CONGRESOS]
│       ├── MisSesionesView.vue    [MIS SESIONES]
│       └── PerfilView.vue         [PERFIL USUARIO]
```

### **http.js** - Cliente HTTP

**¿Qué hace?**
Configura Axios para hacer peticiones al backend

**Configuración base**:
```javascript
const http = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' }
})
```

**Interceptor de Request** (antes de enviar):
```javascript
http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`  // Añade token JWT
  }
  return config
})
```

**Interceptor de Response** (cuando llega respuesta):
```javascript
http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token inválido → redirige a login
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)
```

### **auth.js** - Store de Autenticación (Pinia)

**¿Qué es Pinia?**
Sistema de gestión de estado global para Vue.js (como Redux en React)

**Estado reactivo**:
```javascript
const user = ref(null)      // Usuario actual
const token = ref(null)     // Token JWT
```

**Computed (valores calculados)**:
```javascript
const isAuthenticated = computed(() => !!token.value)
const isAsistente = computed(() => user.value?.tipoUsuario === 'ASISTENTE')
```

**Actions (acciones)**:
```javascript
login(email, password): Inicia sesión
registro(userData): Registra nuevo usuario
logout(): Cierra sesión
```

**Persistencia**:
```javascript
localStorage.setItem('token', authToken)
localStorage.setItem('user', JSON.stringify(usuario))
```

### **router/index.js** - Navegación

**Define las rutas**:
```javascript
{ path: '/', name: 'catalogo', component: CatalogoView }
{ path: '/login', name: 'login', component: LoginView }
{ path: '/mis-congresos', name: 'mis-congresos', component: MisCongresosView,
  meta: { requiresAuth: true, role: 'ASISTENTE' } }
```

**Guard de navegación** (protege rutas):
```javascript
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')  // Redirige a login si no está autenticado
  } else {
    next()
  }
})
```

---

## 🔄 FLUJO DE DATOS COMPLETO

### Ejemplo: Usuario se inscribe a un congreso

**1. FRONTEND (CatalogoView.vue)**
```javascript
async inscribirse(congresoId) {
  const response = await http.post(`/inscripciones/congreso/${congresoId}`)
  // Muestra mensaje de éxito
}
```

**2. HTTP.JS**
```javascript
// Interceptor añade token:
config.headers.Authorization = `Bearer eyJhbGc...`
// Envía: POST http://localhost:8080/api/inscripciones/congreso/123
```

**3. BACKEND - JwtAuthenticationFilter**
```java
// Intercepta la petición
// Extrae y valida el token JWT
// Autentica al usuario en SecurityContext
```

**4. BACKEND - SecurityConfig**
```java
// Verifica que la ruta /api/inscripciones/** requiere rol ASISTENTE
// Permite el acceso (si el usuario es ASISTENTE)
```

**5. BACKEND - Controller (hipotético InscripcionController)**
```java
@PostMapping("/congreso/{id}")
public ResponseEntity<?> inscribirse(@PathVariable Long id) {
    // Obtiene usuario autenticado de SecurityContext
    // Llama al Service
}
```

**6. BACKEND - Service (hipotético InscripcionService)**
```java
public InscripcionCongreso inscribir(Long congresoId, Long usuarioId) {
    // 1. Busca el congreso
    Congreso congreso = congresoRepository.findById(congresoId);
    
    // 2. Busca el usuario
    Usuario usuario = usuarioRepository.findById(usuarioId);
    
    // 3. Verifica que no esté ya inscrito
    if (inscripcionRepository.existsByAsistenteAndCongreso(...)) {
        throw new BusinessException("Ya inscrito");
    }
    
    // 4. Crea la inscripción
    InscripcionCongreso inscripcion = new InscripcionCongreso();
    inscripcion.setAsistente(usuario);
    inscripcion.setCongreso(congreso);
    inscripcion.setEstado(EstadoInscripcion.PENDIENTE);
    
    // 5. Si es de pago, crea el pago
    if (congreso.getEsPago()) {
        Pago pago = new Pago();
        pago.setMonto(congreso.getPrecio());
        pago.setEstado(EstadoPago.PENDIENTE);
        inscripcion.setPago(pago);
    } else {
        inscripcion.confirmar();
    }
    
    // 6. Llama al MS Verificador para generar ticket QR
    String ticketQR = verificadorClient.generarTicket(...);
    inscripcion.setTicketQR(ticketQR);
    
    // 7. Guarda en BD
    return inscripcionRepository.save(inscripcion);
}
```

**7. BACKEND - Repository**
```java
inscripcionRepository.save(inscripcion)
// Hibernate genera SQL:
// INSERT INTO inscripciones_congreso (id_asistente, id_congreso, estado, ...)
// VALUES (?, ?, ?, ...)
```

**8. RESPUESTA AL FRONTEND**
```javascript
// Frontend recibe respuesta JSON
{
  "idInscripcion": 123,
  "estado": "CONFIRMADA",
  "ticketQR": "https://...",
  "fechaInscripcion": "2025-12-02T15:30:00"
}
```

---

## 🔌 MICROSERVICIOS AUXILIARES

### **MS Ticket Authority** (Puerto 8081)

**Función**: Es el servicio AUTORIDAD de tickets - el único que puede emitir y validar tickets de forma definitiva

**Responsabilidades principales**:

1. **Emisión de tickets QR** (`POST /api/v1/tickets`)
   - Genera un ID único de ticket (formato: `TCK-XXXXXXXX`)
   - Crea una **firma criptográfica** para evitar falsificaciones
   - Guarda el ticket en su almacén (en memoria actualmente)
   - Devuelve: QR payload (Base64) + signature

2. **Verificación de tickets** (`POST /api/v1/tickets/verify`)
   - Valida la firma criptográfica (detecta tickets falsos/manipulados)
   - Verifica fechas: no caducado, ya válido
   - Verifica que no haya sido usado previamente
   - Marca el ticket como "usado" una vez escaneado
   - Devuelve estados: `VALID`, `TAMPERED`, `EXPIRED`, `NOT_YET_VALID`, `ALREADY_USED`

3. **Consulta de estado** (`GET /api/v1/tickets/{ticketId}/status`)
   - Consulta información del ticket
   - Devuelve: estado, fecha primer uso, número de usos

**Modelo de datos Ticket**:
```java
class Ticket {
    String ticketId;           // TCK-XXXXXXXX
    String eventId;            // ID del evento
    String holderName;         // Nombre del titular
    String holderDoc;          // Documento del titular
    LocalDateTime validFrom;   // Válido desde
    LocalDateTime validTo;     // Válido hasta
    String signature;          // Firma criptográfica
    List<Usage> usages;        // Registro de escaneos
    LocalDateTime createdAt;   // Fecha de creación
}
```

**Seguridad**:
```properties
ticket.signature.secret=CLAVE_SECRETA_PARA_FIRMAR_TICKETS_QR_MUY_SEGURA_2025
```
- Usa esta clave para generar firmas HMAC
- Solo Ticket Authority conoce esta clave
- Imposible falsificar tickets sin la clave

**Tecnología**:
- Spring Boot 3.5.0
- Almacenamiento: `ConcurrentHashMap` (en memoria)
- SignatureService: Genera/valida firmas criptográficas
- No usa base de datos (stateless para alto rendimiento)

---

### **MS Verificador Adapter** (Puerto 8082)

**Función**: Adaptador/Proxy entre el Backend Principal y el Ticket Authority

**¿Por qué existe este microservicio separado?**

1. **Patrón de diseño: Adapter Pattern**
   - El Backend Principal NO llama directamente al Ticket Authority
   - El Verificador actúa como intermediario inteligente
   - Abstrae la complejidad de comunicación con servicios externos

2. **Funcionalidades que añade**:

   **a) Reintentos automáticos**:
   ```java
   return ticketAuthorityClient.verifyTicket(request)
       .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
   ```
   - Si Ticket Authority falla temporalmente, reintenta 3 veces
   - Backoff exponencial: 1s, 2s, 4s
   - Aumenta la resiliencia del sistema

   **b) Logging y auditoría**:
   ```java
   .doOnSuccess(response -> 
       log.info("Verificación completada - Status: {}, TicketId: {}", 
                response.getStatus(), response.getTicketId()))
   .doOnError(error -> 
       log.error("Error en verificación: {}", error.getMessage()))
   ```
   - Registra todas las verificaciones
   - Facilita debugging y auditoría
   - Cumplimiento normativo (trazabilidad)

   **c) Resiliencia**:
   - Usa **WebFlux reactivo** (Mono) para operaciones no bloqueantes
   - Preparado para implementar Circuit Breaker
   - Manejo elegante de fallos

   **d) Caché** (preparado para implementar):
   - Podría cachear respuestas de tickets ya verificados
   - Reduce carga en Ticket Authority
   - Mejora tiempos de respuesta

**Endpoints expuestos**:
```
POST /api/verificador/verifyTicket
  Body: { qrPayload, signature, gateId }
  Response: { status, ticketId, allowed, verificationId, ... }

GET /api/verificador/health
  Response: "MS Verificador Adapter is running"
```

**Configuración**:
```properties
ticket.authority.url=http://localhost:8081
```

**Tecnología**:
- Spring Boot 3.5.0
- WebFlux (programación reactiva con Mono)
- WebClient (cliente HTTP reactivo)
- Retry backoff strategy

---

### 🔄 **Flujo completo de verificación de un ticket QR**

#### Diagrama de secuencia:
```
1. Staff escanea QR en la app móvil/web
        ↓
2. Frontend envía solicitud al Backend Principal
   POST http://localhost:8080/api/acceso/verificar
   Body: { qrPayload: "base64...", signature: "hmac...", gateId: "GATE-001" }
        ↓
3. Backend Principal → MS-Verificador-Adapter
   POST http://localhost:8082/api/verificador/verifyTicket
   Body: { qrPayload, signature, gateId }
        ↓
4. MS-Verificador-Adapter → MS-Ticket-Authority
   POST http://localhost:8081/api/v1/tickets/verify
   Body: { qrPayload, signature, gateId }
   
   [Si falla, reintenta 3 veces con backoff: 1s, 2s, 4s]
        ↓
5. MS-Ticket-Authority valida el ticket:
   ✓ Verifica firma criptográfica (HMAC-SHA256)
   ✓ Extrae ticketId del payload
   ✓ Busca ticket en almacén
   ✓ Verifica fechas: validFrom <= ahora <= validTo
   ✓ Verifica que NO haya sido usado antes
   ✓ Marca como USADO y registra: { gateId, verificationId, timestamp }
        ↓
6. Responde con uno de estos estados:
   - VALID: ✅ Ticket válido, acceso permitido
   - TAMPERED: ❌ Firma inválida o ticket no existe
   - EXPIRED: ⏰ Ticket caducado
   - NOT_YET_VALID: 🕐 Ticket aún no válido
   - ALREADY_USED: ♻️ Ticket ya usado anteriormente
        ↓
7. MS-Verificador-Adapter recibe respuesta
   - Logging: "Verificación completada - Status: VALID, TicketId: TCK-ABC12345"
        ↓
8. Backend Principal recibe respuesta
   - Guarda en BD: RegistroAcceso { usuario, ticket, sesion, gate, timestamp }
        ↓
9. Frontend muestra resultado al Staff:
   - ✅ "Acceso permitido - Bienvenido Juan Pérez"
   - ❌ "Ticket inválido - Contacte con organización"
```

#### ¿Por qué DOS microservicios en lugar de UNO?

**Ventajas de la arquitectura de 2 microservicios:**

1. **Separación de responsabilidades (SRP)**:
   - **Ticket Authority**: SOLO lógica de negocio de tickets (core domain)
   - **Verificador Adapter**: SOLO infraestructura (comunicación, reintentos, caché)

2. **Escalabilidad independiente**:
   - Ticket Authority puede escalar horizontalmente según carga
   - Verificador puede tener múltiples instancias sin duplicar lógica

3. **Seguridad mejorada**:
   - La clave secreta de firma SOLO está en Ticket Authority
   - Verificador NO puede falsificar tickets
   - Backend Principal ni siquiera conoce la clave

4. **Resiliencia del sistema**:
   - Si Ticket Authority falla temporalmente → Verificador reintenta
   - Puedes añadir Circuit Breaker para evitar cascading failures
   - Desacopla fallo de un servicio del otro

5. **Flexibilidad de evolución**:
   - Cambiar Ticket Authority → Solo modificas Verificador
   - Añadir caché → Solo en Verificador
   - Nuevos adaptadores (móvil, kioscos) → Reutilizan Ticket Authority
   - Migrar a servicio real (Ticketmaster) → Solo cambias Verificador

6. **Testing más sencillo**:
   - Puedes mockear Ticket Authority en tests del Verificador
   - Puedes testear Ticket Authority sin HTTP (testing unitario)

#### Configuración de servicios:

**docker-compose.yml**:
```yaml
services:
  backend-principal:
    ports: ["8080:8080"]
    environment:
      - ms.verificador.url=http://ms-verificador-adapter:8082
  
  ms-verificador-adapter:
    ports: ["8082:8082"]
    environment:
      - ticket.authority.url=http://ms-ticket-authority:8081
  
  ms-ticket-authority:
    ports: ["8081:8081"]
    environment:
      - ticket.signature.secret=CLAVE_SECRETA_...
```

#### Estados de respuesta del sistema:

| Estado | Código HTTP | Significado | Acción del Frontend |
|--------|-------------|-------------|---------------------|
| `VALID` | 200 | Ticket válido | ✅ Permitir acceso |
| `TAMPERED` | 200 | Firma inválida | ❌ Denegar + Alertar seguridad |
| `EXPIRED` | 200 | Caducado | ❌ Denegar + Mostrar "Ticket expirado" |
| `NOT_YET_VALID` | 200 | Aún no válido | ❌ Denegar + Mostrar fecha inicio |
| `ALREADY_USED` | 200 | Ya usado | ❌ Denegar + Mostrar fecha/hora uso |
| Error 500 | 500 | Error del servidor | ⚠️ Mostrar "Error temporal, reintente" |

**Nota**: Todos devuelven 200 porque son respuestas válidas del servicio. El estado de negocio va en el campo `status` del JSON.

---

## 📝 CONCEPTOS CLAVE

### DTO (Data Transfer Object)
**¿Qué es?** Objeto para transferir datos entre capas

**Ejemplo**:
```java
public class LoginRequest {
    private String email;
    private String password;
}

public class LoginResponse {
    private String token;
    private UsuarioDTO usuario;
}
```

**¿Por qué usar DTOs?**
- **Seguridad**: No expones entidades completas (evitas enviar password)
- **Flexibilidad**: Puedes combinar datos de varias entidades
- **Versionado**: Diferentes versiones de API pueden tener DTOs diferentes

---

### Controller (Controlador)
**¿Qué es?** Capa que recibe peticiones HTTP y devuelve respuestas

**Ejemplo típico**:
```java
@RestController
@RequestMapping("/api/congresos")
public class CongresoController {
    
    @GetMapping("/buscar")
    public List<CongresoDTO> buscar(@RequestParam String query) {
        return congresoService.buscar(query);
    }
    
    @PostMapping("/crear")
    @PreAuthorize("hasRole('ORGANIZADOR')")
    public CongresoDTO crear(@RequestBody CongresoRequest request) {
        return congresoService.crear(request);
    }
}
```

**Anotaciones**:
- `@RestController`: Marca como controlador REST (devuelve JSON)
- `@RequestMapping`: Prefijo de URL
- `@GetMapping`, `@PostMapping`: Tipos de petición HTTP
- `@RequestParam`: Parámetro de URL (?query=java)
- `@PathVariable`: Parámetro en la ruta (/congresos/123)
- `@RequestBody`: El body de la petición se convierte a objeto Java

---

### Service (Servicio)
**¿Qué es?** Capa de lógica de negocio

**Ejemplo típico**:
```java
@Service
public class CongresoService {
    
    @Autowired
    private CongresoRepository congresoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    public CongresoDTO crear(CongresoRequest request, Long organizadorId) {
        // 1. Validaciones
        Usuario organizador = usuarioRepository.findById(organizadorId)
            .orElseThrow(() -> new NotFoundException("Organizador no encontrado"));
        
        if (organizador.getTipoUsuario() != TipoUsuario.ORGANIZADOR) {
            throw new BusinessException("Solo organizadores pueden crear congresos");
        }
        
        // 2. Lógica de negocio
        Congreso congreso = new Congreso();
        congreso.setNombre(request.getNombre());
        congreso.setFechaInicio(request.getFechaInicio());
        congreso.setFechaFin(request.getFechaFin());
        congreso.setOrganizador(organizador);
        
        // 3. Persistencia
        Congreso saved = congresoRepository.save(congreso);
        
        // 4. Conversión a DTO
        return toDTO(saved);
    }
}
```

**¿Por qué separar Service de Controller?**
- **Reutilización**: Varios controllers pueden usar el mismo service
- **Testeo**: Puedes testear la lógica sin HTTP
- **Organización**: Cada capa tiene una responsabilidad clara

---

### Exception Handling (Manejo de Errores)

**GlobalExceptionHandler.java**:
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getMessage()));
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(ex.getMessage()));
    }
}
```

**¿Qué hace?**
- Captura excepciones lanzadas en cualquier parte del backend
- Las convierte en respuestas HTTP apropiadas
- Evita duplicar código de manejo de errores

---

## 🎯 CASOS DE USO IMPLEMENTADOS

### CU1: Registro de Asistente
**Flujo**:
1. Usuario rellena formulario (RegistroView.vue)
2. Frontend envía POST /api/auth/registro
3. Backend valida datos
4. Encripta password con BCrypt
5. Guarda usuario en BD
6. Devuelve confirmación

### CU2: Inicio de Sesión
**Flujo**:
1. Usuario introduce email/password (LoginView.vue)
2. Frontend envía POST /api/auth/login
3. Backend verifica credenciales
4. Genera token JWT
5. Devuelve token + datos usuario
6. Frontend guarda token en localStorage
7. Redirige según rol (asistente → catálogo, organizador → panel)

### CU4: Inscribirse a Congreso
**Flujo**:
1. Usuario ve detalle de congreso (CongresoDetalleView.vue)
2. Click en "Inscribirse"
3. Frontend envía POST /api/inscripciones/congreso/{id}
4. Backend verifica:
   - Usuario autenticado
   - No inscrito previamente
   - Congreso activo y no finalizado
5. Crea InscripcionCongreso
6. Si es de pago → crea Pago (estado PENDIENTE)
7. Si es gratuito → confirma inscripción + genera ticket QR
8. Devuelve inscripción

### CU5: Pago de Inscripción
**Flujo**:
1. Usuario en MisCongresosView.vue ve inscripción PENDIENTE
2. Click en "Pagar"
3. Frontend redirige a pasarela de pago (simulada)
4. Tras pago exitoso → POST /api/pagos/{id}/confirmar
5. Backend:
   - Marca pago como COMPLETADO
   - Confirma inscripción
   - Genera ticket QR vía MS Verificador
6. Usuario recibe ticket

### CU7: Inscribirse a Sesión
**Flujo**:
1. Usuario ve programa del congreso
2. Selecciona sesión
3. Frontend envía POST /api/inscripciones/sesion/{id}
4. Backend verifica:
   - Inscrito al congreso padre
   - Hay aforo disponible
   - No hay conflicto horario
5. Crea InscripcionSesion
6. Incrementa aforo de sesión
7. Genera ticket QR específico
8. Devuelve inscripción

### CU9: Escaneo y Control de Acceso
**Flujo**:
1. Staff escanea QR en puerta/sala
2. App staff envía POST /api/escaneo/verificar
   Body: { qrCode, gateId, tipoAcceso }
3. Backend:
   - Envía QR a MS Verificador para validar firma
   - Verifica que corresponde al congreso/sesión
   - Verifica que no se usó antes
   - Verifica horario
4. Crea RegistroAcceso con resultado
5. Devuelve resultado (VALIDO / NO_VALIDO / YA_USADO / etc.)
6. Staff permite o deniega acceso

### CU21: Ver Estadísticas (Organizador)
**Flujo**:
1. Organizador en panel de estadísticas
2. Frontend envía GET /api/estadisticas/congreso/{id}
3. Backend calcula:
   - Total inscripciones
   - Tasa de asistencia (accesos vs inscripciones)
   - Ingresos totales
   - Sesiones más populares
   - Evolución temporal
4. Devuelve datos para gráficas

### CU23: Crear Congreso (Organizador)
**Descripción**: Permite al organizador crear un nuevo congreso con toda su información.

**Flujo Completo**:
1. **Frontend**: Organizador accede a `/organizador/crear-congreso`
2. **Frontend**: Rellena formulario con:
   - Nombre del congreso
   - Descripción
   - Fecha inicio y fin
   - Lugar y ciudad
   - Temática
   - Precio (si es de pago)
   - URL imagen portada
3. **Frontend**: Envía POST `/api/congresos/crear` con datos validados
4. **Backend**: `JwtAuthenticationFilter` valida token JWT
5. **Backend**: `@PreAuthorize("hasRole('ORGANIZADOR')")` verifica rol
6. **Backend**: `CongresoController.crearCongreso()` recibe request
7. **Backend**: `CongresoService.crearCongreso()` ejecuta lógica:
   - Busca organizador por email autenticado
   - Valida que usuario sea ORGANIZADOR
   - Valida fechas (inicio < fin, inicio >= hoy)
   - Valida precio (> 0 si es de pago)
   - Crea entidad `Congreso`
   - Asocia organizador (`ManyToOne`)
   - Guarda en base de datos
8. **Backend**: Devuelve `CongresoDTO` con ID generado
9. **Frontend**: Muestra mensaje de éxito
10. **Frontend**: Redirige a `/organizador/mis-congresos`

**Archivos Involucrados**:
- `CrearCongresoView.vue` - Formulario de creación
- `CongresoController.java` - Endpoint POST `/api/congresos/crear`
- `CongresoService.java` - Lógica de negocio y validaciones
- `CongresoRepository.java` - Persistencia JPA
- `Congreso.java` - Entidad de dominio
- `CongresoRequest.java` - DTO para request
- `CongresoDTO.java` - DTO para response

**Validaciones Implementadas**:
- ✅ Solo usuarios con rol `ORGANIZADOR` pueden crear
- ✅ Fecha inicio debe ser futura
- ✅ Fecha inicio < fecha fin
- ✅ Si es de pago, precio > 0
- ✅ Nombre obligatorio (max 200 caracteres)
- ✅ Lugar obligatorio (max 200 caracteres)

**Seguridad**:
```java
@PreAuthorize("hasRole('ORGANIZADOR')")
public ResponseEntity<CongresoDTO> crearCongreso(...)
```

---

### CU24: Editar Congreso (Organizador)
**Descripción**: Permite al organizador modificar un congreso existente que él creó.

**Flujo Completo**:
1. **Frontend**: Organizador accede a `/organizador/editar-congreso/:id`
2. **Frontend**: Carga datos del congreso existente
3. **Frontend**: Modifica campos necesarios
4. **Frontend**: Envía PUT `/api/congresos/:id/editar`
5. **Backend**: Valida que el organizador sea el propietario
6. **Backend**: Actualiza campos del congreso
7. **Backend**: Devuelve congreso actualizado

**Restricciones de Seguridad**:
- Solo el organizador propietario puede editar
- Verificación: `congreso.getOrganizador().getEmail().equals(emailOrganizador)`

---

## 🔄 CICLO DE VIDA COMPLETO DE UNA PETICIÓN

```
1. USUARIO (Navegador)
   └─> Hace click en "Inscribirse"

2. VUE.JS (Frontend)
   └─> Llama a http.post('/inscripciones/congreso/123')
       └─> http.js añade header Authorization: Bearer <token>

3. AXIOS (Cliente HTTP)
   └─> Envía HTTP POST a http://localhost:8080/api/inscripciones/congreso/123

4. SPRING BOOT (Backend)
   └─> JwtAuthenticationFilter intercepta
       └─> Valida token JWT
       └─> Extrae usuario (email)
       └─> Autentica en SecurityContext

5. SPRING SECURITY
   └─> SecurityConfig verifica permisos
       └─> ¿Tiene rol ASISTENTE? → SÍ, continúa

6. SPRING MVC
   └─> DispatcherServlet enruta a InscripcionController
       └─> Llama a método inscribirse(Long id)

7. CONTROLLER
   └─> InscripcionController.inscribirse(123L)
       └─> Obtiene usuario autenticado de SecurityContext
       └─> Llama a InscripcionService.inscribir(123L, userId)

8. SERVICE (Lógica de Negocio)
   └─> InscripcionService.inscribir()
       └─> Busca congreso vía CongresoRepository
       └─> Busca usuario vía UsuarioRepository
       └─> Validaciones de negocio
       └─> Crea InscripcionCongreso
       └─> Llama a MS Verificador para generar QR
       └─> Guarda vía InscripcionRepository

9. REPOSITORY
   └─> InscripcionRepository.save(inscripcion)
       └─> Spring Data JPA traduce a SQL
       └─> Hibernate ejecuta INSERT en PostgreSQL

10. POSTGRESQL (Base de Datos)
    └─> Ejecuta INSERT
    └─> Devuelve ID generado

11. HIBERNATE (ORM)
    └─> Mapea resultado a objeto InscripcionCongreso
    └─> Devuelve al Repository

12. REPOSITORY → SERVICE → CONTROLLER
    └─> Devuelve InscripcionCongreso

13. CONTROLLER
    └─> Convierte a InscripcionDTO
    └─> Devuelve ResponseEntity<InscripcionDTO>

14. SPRING MVC
    └─> Jackson convierte DTO a JSON
    └─> Crea HTTP Response 201 CREATED

15. AXIOS (Frontend)
    └─> Recibe respuesta JSON
    └─> Promise se resuelve con data

16. VUE.JS
    └─> Actualiza interfaz
    └─> Muestra mensaje "Inscripción exitosa"

17. USUARIO
    └─> Ve confirmación en pantalla
```

---

## 📚 GLOSARIO DE TÉRMINOS

### Spring Boot
Framework de Java que simplifica crear aplicaciones web. Proporciona configuración automática, servidor embebido (Tomcat), y herramientas para APIs REST.

### JPA (Java Persistence API)
Especificación de Java para ORM (mapeo objeto-relacional). Permite trabajar con objetos Java en vez de SQL.

### Hibernate
Implementación de JPA. Traduce automáticamente objetos Java a SQL y viceversa.

### Repository
Interfaz que proporciona métodos CRUD (Create, Read, Update, Delete) sin escribir SQL.

### Entity (Entidad)
Clase Java que representa una tabla de base de datos. Cada instancia es una fila.

### DTO (Data Transfer Object)
Objeto simple para transferir datos entre capas. No tiene lógica de negocio.

### REST API
Arquitectura para APIs web que usa HTTP (GET, POST, PUT, DELETE) y JSON.

### JWT (JSON Web Token)
Token de autenticación que contiene información firmada. Stateless (sin sesión en servidor).

### ORM (Object-Relational Mapping)
Técnica para convertir objetos a filas de BD y viceversa.

### CORS (Cross-Origin Resource Sharing)
Mecanismo que permite peticiones entre diferentes orígenes (ej: frontend en puerto 5173 → backend en 8080).

### Microservicio
Aplicación pequeña e independiente que hace una cosa específica.

### Adapter Pattern
Patrón de diseño que encapsula la comunicación con sistemas externos.

### Pinia
Librería de Vue.js para gestión de estado global.

### Vite
Build tool para Vue.js, muy rápido.

### Axios
Cliente HTTP para JavaScript, facilita peticiones AJAX.

### Lombok
Librería Java que reduce código boilerplate (getters, setters, constructores) mediante anotaciones.

### Bean
Objeto gestionado por Spring. Spring crea, configura y destruye beans.

### Dependency Injection
Spring inyecta dependencias automáticamente (anotación @Autowired).

### Cascade
Operaciones que se propagan (ej: eliminar Congreso elimina sus Sesiones).

### Lazy Loading
Cargar relaciones solo cuando se accede a ellas (ahorra memoria).

### Eager Loading
Cargar relaciones inmediatamente.

### Transaction
Conjunto de operaciones que se ejecutan todas o ninguna.

### Migration
Cambios en el esquema de BD (ej: añadir columna, crear tabla).

---

## 🚀 CÓMO LEVANTAR EL SISTEMA

### Opción 1: Docker Compose (Recomendado)
```bash
# En la raíz del proyecto
docker-compose up --build

# Esperar a que todos los servicios estén UP
# Frontend: http://localhost:5173
# Backend: http://localhost:8080
```

### Opción 2: Manual

**1. Base de Datos**
```bash
# Instalar PostgreSQL
# Crear base de datos "congresos_db"
# Usuario: admin, Password: admin123
```

**2. Backend Principal**
```bash
cd backend-principal
mvn clean install
mvn spring-boot:run
```

**3. MS Ticket Authority**
```bash
cd ms-ticket-authority
mvn clean install
mvn spring-boot:run
```

**4. MS Verificador Adapter**
```bash
cd ms-verificador-adapter
mvn clean install
mvn spring-boot:run
```

**5. Frontend**
```bash
cd frontend
npm install
npm run dev
```

---

## 🎓 CONCLUSIÓN

Este sistema es un **ejemplo completo de arquitectura de microservicios moderna** que incluye:

✅ **Backend robusto** con Spring Boot, JPA, Spring Security  
✅ **Autenticación segura** con JWT  
✅ **Modelo de datos completo** con relaciones complejas  
✅ **Microservicios independientes** (Ticket Authority, Verificador)  
✅ **Frontend reactivo** con Vue.js 3  
✅ **Gestión de estado** con Pinia  
✅ **Base de datos relacional** PostgreSQL  
✅ **Contenedorización** con Docker  
✅ **Separación de responsabilidades** (Controller → Service → Repository)  
✅ **Manejo de errores centralizado**  
✅ **CORS configurado** para desarrollo  

**Conceptos aprendidos**:
- Arquitectura en capas (Controller, Service, Repository)
- ORM con Hibernate/JPA
- Autenticación JWT
- Microservicios
- RESTful APIs
- Vue.js con Composition API
- Gestión de estado (Pinia)
- Docker y Docker Compose
- Patrones de diseño (Adapter, Repository)

---

**📌 Estos apuntes cubren TODO el sistema en detalle. Guárdalos y consúltalos siempre que necesites recordar cómo funciona algo.**
