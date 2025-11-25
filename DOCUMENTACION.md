# 📚 DOCUMENTACIÓN COMPLETA DEL SISTEMA DE GESTIÓN DE CONGRESOS ACADÉMICOS

## Índice
1. [Descripción General](#descripción-general)
2. [Arquitectura del Sistema](#arquitectura-del-sistema)
3. [Estructura de Archivos](#estructura-de-archivos)
4. [Backend Principal](#backend-principal)
5. [MS Ticket Authority](#ms-ticket-authority)
6. [MS Verificador Adapter](#ms-verificador-adapter)
7. [Frontend](#frontend)
8. [Comunicación entre Servicios](#comunicación-entre-servicios)
9. [Base de Datos](#base-de-datos)
10. [Docker y Despliegue](#docker-y-despliegue)
11. [Flujos de Uso](#flujos-de-uso)

---

## 📋 Descripción General

Este proyecto es un **Sistema de Gestión de Congresos Académicos** completo que permite:
- Gestionar usuarios (asistentes, organizadores y staff)
- Crear y administrar congresos académicos
- Inscribirse a congresos y sesiones
- Gestionar pagos de inscripciones
- Control de acceso mediante códigos QR
- Estadísticas de asistencia

### Tecnologías Utilizadas

| Componente | Tecnología |
|------------|------------|
| **Backend Principal** | Java 17, Spring Boot 3.2, Spring Security, JPA/Hibernate |
| **MS Ticket Authority** | Java 17, Spring Boot 3.2, JWT |
| **MS Verificador Adapter** | Java 17, Spring Boot 3.2, WebFlux (WebClient) |
| **Frontend** | Vue.js 3, Vite 5, Pinia, Vue Router 4, Axios |
| **Base de Datos** | PostgreSQL 15 |
| **Contenedores** | Docker, Docker Compose |
| **Build** | Maven 3.9, npm |

---

## 🏗️ Arquitectura del Sistema

El sistema sigue una arquitectura de **microservicios** con los siguientes componentes:

```
┌──────────────────┐     ┌──────────────────┐
│                  │     │                  │
│     FRONTEND     │────▶│ BACKEND PRINCIPAL│
│   (Vue.js 3)     │     │  (Spring Boot)   │
│   Puerto: 5173   │     │   Puerto: 8080   │
│                  │     │                  │
└──────────────────┘     └────────┬─────────┘
                                  │
                                  │ HTTP REST
                                  ▼
                         ┌──────────────────┐
                         │                  │
                         │  MS VERIFICADOR  │
                         │    ADAPTER       │
                         │  Puerto: 8082    │
                         │                  │
                         └────────┬─────────┘
                                  │
                                  │ HTTP REST
                                  ▼
                         ┌──────────────────┐
                         │                  │
                         │  MS TICKET       │
                         │  AUTHORITY       │
                         │  Puerto: 8081    │
                         │                  │
                         └──────────────────┘

┌──────────────────┐
│                  │
│   POSTGRESQL     │◀──── (Solo conectado al Backend Principal)
│   Puerto: 5432   │
│                  │
└──────────────────┘
```

### Patrón de Diseño: Adapter
El sistema implementa el **patrón Adapter** a través del microservicio `ms-verificador-adapter`, que actúa como intermediario entre el backend principal y el servicio externo de tickets. Esto permite:
- Desacoplar la lógica de negocio de la implementación del servicio externo
- Manejar reintentos y errores de forma centralizada
- Facilitar cambios futuros en el servicio de tickets

---

## 📁 Estructura de Archivos

```
TT_ASI/
│
├── 📄 README.md                          # Documentación general del proyecto
├── 📄 docker-compose.yml                 # Orquestación de todos los servicios Docker
├── 📄 DOCUMENTACION.md                   # Este archivo
│
├── 📁 backend-principal/                 # 🔧 SERVICIO PRINCIPAL
│   ├── 📄 Dockerfile                     # Imagen Docker del backend
│   ├── 📄 pom.xml                        # Dependencias Maven del proyecto
│   └── 📁 src/main/
│       ├── 📁 java/com/congresos/backend/
│       │   ├── 📄 BackendPrincipalApplication.java    # Clase principal Spring Boot
│       │   │
│       │   ├── 📁 config/
│       │   │   └── 📄 SecurityConfig.java             # Configuración Spring Security + JWT
│       │   │
│       │   ├── 📁 exception/
│       │   │   ├── 📄 BusinessException.java          # Excepción para errores de negocio
│       │   │   ├── 📄 NotFoundException.java          # Excepción para recursos no encontrados
│       │   │   └── 📄 GlobalExceptionHandler.java     # Manejador global de excepciones
│       │   │
│       │   ├── 📁 model/domain/
│       │   │   ├── 📄 Usuario.java                    # Entidad de usuarios del sistema
│       │   │   ├── 📄 Congreso.java                   # Entidad de congresos académicos
│       │   │   ├── 📄 Sesion.java                     # Entidad de sesiones del congreso
│       │   │   ├── 📄 InscripcionCongreso.java        # Inscripción a congreso
│       │   │   ├── 📄 InscripcionSesion.java          # Inscripción a sesión
│       │   │   ├── 📄 Pago.java                       # Gestión de pagos
│       │   │   └── 📄 RegistroAcceso.java             # Registro de escaneos QR
│       │   │
│       │   ├── 📁 repository/
│       │   │   ├── 📄 UsuarioRepository.java          # Acceso a datos de usuarios
│       │   │   ├── 📄 CongresoRepository.java         # Acceso a datos de congresos
│       │   │   ├── 📄 SesionRepository.java           # Acceso a datos de sesiones
│       │   │   ├── 📄 InscripcionCongresoRepository.java
│       │   │   ├── 📄 InscripcionSesionRepository.java
│       │   │   ├── 📄 PagoRepository.java
│       │   │   └── 📄 RegistroAccesoRepository.java
│       │   │
│       │   └── 📁 security/
│       │       ├── 📄 JwtService.java                 # Servicio para generar/validar JWT
│       │       ├── 📄 JwtAuthenticationFilter.java    # Filtro de autenticación JWT
│       │       └── 📄 UserDetailsServiceImpl.java     # Implementación UserDetailsService
│       │
│       └── 📁 resources/
│           └── 📄 application.properties              # Configuración del backend
│
├── 📁 ms-ticket-authority/               # 🎫 MICROSERVICIO DE TICKETS (EXTERNO)
│   ├── 📄 Dockerfile
│   ├── 📄 pom.xml
│   └── 📁 src/main/
│       ├── 📁 java/com/congresos/ticketauthority/
│       │   ├── 📄 TicketAuthorityApplication.java     # Clase principal
│       │   │
│       │   ├── 📁 controller/
│       │   │   └── 📄 TicketController.java           # API REST de tickets
│       │   │
│       │   ├── 📁 dto/
│       │   │   ├── 📄 EmitTicketRequest.java          # Request para emitir ticket
│       │   │   ├── 📄 EmitTicketResponse.java         # Response de emisión
│       │   │   ├── 📄 VerifyTicketRequest.java        # Request para verificar
│       │   │   └── 📄 VerifyTicketResponse.java       # Response de verificación
│       │   │
│       │   ├── 📁 model/
│       │   │   └── 📄 Ticket.java                     # Modelo de ticket en memoria
│       │   │
│       │   └── 📁 service/
│       │       ├── 📄 TicketService.java              # Lógica de emisión/verificación
│       │       └── 📄 SignatureService.java           # Firma criptográfica con JWT
│       │
│       └── 📁 resources/
│           └── 📄 application.properties
│
├── 📁 ms-verificador-adapter/            # 🔄 MICROSERVICIO ADAPTADOR
│   ├── 📄 Dockerfile
│   ├── 📄 pom.xml
│   └── 📁 src/main/
│       ├── 📁 java/com/congresos/verificador/
│       │   ├── 📄 VerificadorAdapterApplication.java  # Clase principal
│       │   │
│       │   ├── 📁 controller/
│       │   │   └── 📄 VerificadorController.java      # API REST del adaptador
│       │   │
│       │   ├── 📁 dto/
│       │   │   ├── 📄 VerifyTicketRequest.java
│       │   │   └── 📄 VerifyTicketResponse.java
│       │   │
│       │   └── 📁 service/
│       │       ├── 📄 VerificadorAdapter.java         # Servicio con reintentos
│       │       └── 📄 TicketAuthorityClient.java      # Cliente HTTP WebClient
│       │
│       └── 📁 resources/
│           └── 📄 application.properties
│
└── 📁 frontend/                          # 🖥️ INTERFAZ DE USUARIO
    ├── 📄 Dockerfile
    ├── 📄 package.json                   # Dependencias npm
    ├── 📄 package-lock.json
    ├── 📄 vite.config.js                 # Configuración de Vite
    ├── 📄 index.html                     # HTML principal
    │
    └── 📁 src/
        ├── 📄 main.js                    # Punto de entrada de Vue
        ├── 📄 App.vue                    # Componente raíz con navegación
        │
        ├── 📁 assets/
        │   └── 📄 main.css               # Estilos globales
        │
        ├── 📁 router/
        │   └── 📄 index.js               # Configuración de Vue Router
        │
        ├── 📁 services/
        │   └── 📄 http.js                # Cliente HTTP Axios configurado
        │
        ├── 📁 stores/
        │   └── 📄 auth.js                # Store Pinia para autenticación
        │
        └── 📁 views/
            ├── 📄 CatalogoView.vue       # Vista catálogo de congresos
            ├── 📄 CongresoDetalleView.vue # Detalle de un congreso
            ├── 📄 LoginView.vue          # Inicio de sesión
            ├── 📄 RegistroView.vue       # Registro de usuarios
            ├── 📄 MisCongresosView.vue   # Congresos del usuario
            ├── 📄 MisSesionesView.vue    # Sesiones del usuario
            └── 📄 PerfilView.vue         # Perfil del usuario
```

---

## 🔧 Backend Principal

### Descripción
El **Backend Principal** es el núcleo del sistema. Gestiona:
- Autenticación y autorización de usuarios
- CRUD de congresos y sesiones
- Inscripciones a congresos y sesiones
- Pagos de inscripciones
- Comunicación con el microservicio verificador para el control de acceso

### Archivos Clave

#### 📄 `BackendPrincipalApplication.java`
**Ubicación:** `backend-principal/src/main/java/com/congresos/backend/`

```java
@SpringBootApplication
public class BackendPrincipalApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendPrincipalApplication.class, args);
    }
}
```
**Función:** Punto de entrada de la aplicación Spring Boot. La anotación `@SpringBootApplication` combina:
- `@Configuration`: Indica que es una clase de configuración
- `@EnableAutoConfiguration`: Habilita auto-configuración de Spring
- `@ComponentScan`: Escanea componentes en el paquete

#### 📄 `SecurityConfig.java`
**Ubicación:** `backend-principal/src/main/java/com/congresos/backend/config/`

**Función:** Configura la seguridad de Spring Security con JWT.

**Características principales:**
- **CSRF deshabilitado:** Necesario para APIs REST
- **Rutas públicas:** `/api/auth/**`, `/api/congresos/buscar`, etc.
- **Rutas protegidas por rol:**
  - `ASISTENTE`: Inscripciones, ver sesiones
  - `ORGANIZADOR`: Crear congresos, ver estadísticas
  - `STAFF`: Escaneo de QR
- **Sesiones stateless:** No se mantiene estado de sesión en el servidor
- **CORS configurado:** Para permitir llamadas desde el frontend

---

### 📁 Modelo de Dominio (Entidades JPA)

#### 📄 `Usuario.java`
**Ubicación:** `backend-principal/src/main/java/com/congresos/backend/model/domain/`

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `idUsuario` | Long | ID autoincremental |
| `nombreCompleto` | String | Nombre completo del usuario |
| `email` | String | Email único (usado para login) |
| `password` | String | Contraseña encriptada con BCrypt |
| `tipoUsuario` | TipoUsuario | Enum: ASISTENTE, ORGANIZADOR, STAFF |
| `organizacion` | String | Organización del usuario |
| `fotoPerfil` | String | URL de la foto |
| `activo` | Boolean | Si el usuario está activo |
| `fechaRegistro` | LocalDateTime | Fecha de registro |
| `fechaActualizacion` | LocalDateTime | Última actualización |

**Enum TipoUsuario:**
- `ASISTENTE`: Usuario que asiste a congresos
- `ORGANIZADOR`: Crea y gestiona congresos
- `STAFF`: Personal de control de acceso

#### 📄 `Congreso.java`
**Función:** Representa un congreso académico.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `idCongreso` | Long | ID autoincremental |
| `nombre` | String | Nombre del congreso |
| `descripcion` | String | Descripción detallada |
| `fechaInicio` | LocalDate | Fecha de inicio |
| `fechaFin` | LocalDate | Fecha de finalización |
| `lugar` | String | Lugar del evento |
| `ciudad` | String | Ciudad |
| `precio` | BigDecimal | Precio de inscripción |
| `esPago` | Boolean | Si requiere pago |
| `imagenPortada` | String | URL de imagen |
| `tematica` | String | Temática del congreso |
| `organizador` | Usuario | Relación ManyToOne |
| `sesiones` | List<Sesion> | Relación OneToMany |
| `inscripciones` | List<InscripcionCongreso> | Relación OneToMany |

**Métodos de lógica de negocio:**
- `haFinalizado()`: Verifica si el congreso ya terminó
- `estaEnCurso()`: Verifica si está en curso actualmente

#### 📄 `Sesion.java`
**Función:** Representa una sesión dentro de un congreso (charla, taller, etc.).

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `idSesion` | Long | ID autoincremental |
| `congreso` | Congreso | Relación ManyToOne |
| `titulo` | String | Título de la sesión |
| `descripcion` | String | Descripción |
| `ponente` | String | Nombre del ponente |
| `sala` | String | Sala asignada |
| `fechaHoraInicio` | LocalDateTime | Inicio de la sesión |
| `fechaHoraFin` | LocalDateTime | Fin de la sesión |
| `aforoMaximo` | Integer | Capacidad máxima |
| `aforoActual` | Integer | Inscripciones actuales |

**Métodos de lógica de negocio:**
- `hayPlazasDisponibles()`: Verifica si hay cupo
- `incrementarAforo()`: Aumenta contador de inscritos
- `decrementarAforo()`: Disminuye contador
- `tieneConflictoHorarioCon(Sesion)`: Detecta conflictos de horario

#### 📄 `InscripcionCongreso.java`
**Función:** Representa la inscripción de un asistente a un congreso.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `idInscripcion` | Long | ID autoincremental |
| `asistente` | Usuario | El usuario inscrito |
| `congreso` | Congreso | El congreso de la inscripción |
| `estado` | EstadoInscripcion | PENDIENTE, CONFIRMADA, CANCELADA |
| `pago` | Pago | Relación OneToOne |
| `ticketQR` | String | Código QR generado |
| `ticketId` | String | ID del ticket externo |

> ⚠️ **Nota técnica:** En el código fuente actual (`InscripcionCongreso.java`), el campo `congreso` está incorrectamente tipado como `Usuario` en lugar de `Congreso`. Esta documentación refleja el tipo correcto esperado.

**Enum EstadoInscripcion:**
- `PENDIENTE`: Esperando pago
- `CONFIRMADA`: Inscripción válida
- `CANCELADA`: Cancelada por el usuario

#### 📄 `InscripcionSesion.java`
**Función:** Representa la inscripción a una sesión específica.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `idInscripcionSesion` | Long | ID autoincremental |
| `asistente` | Usuario | El usuario inscrito |
| `sesion` | Sesion | La sesión |
| `ticketQR` | String | Código QR de la sesión |
| `ticketId` | String | ID del ticket externo |
| `activa` | Boolean | Si está activa |

#### 📄 `Pago.java`
**Función:** Gestiona los pagos de inscripciones.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `idPago` | Long | ID autoincremental |
| `inscripcionCongreso` | InscripcionCongreso | Relación OneToOne |
| `monto` | BigDecimal | Cantidad pagada |
| `estado` | EstadoPago | Estado del pago |
| `metodoPago` | MetodoPago | Método utilizado |
| `transaccionId` | String | ID de la pasarela |
| `fechaPago` | LocalDateTime | Fecha del pago |
| `fechaConfirmacion` | LocalDateTime | Fecha de confirmación |

**Enum EstadoPago:** PENDIENTE, PROCESANDO, COMPLETADO, FALLIDO, REEMBOLSADO

**Enum MetodoPago:** TARJETA_CREDITO, TARJETA_DEBITO, PAYPAL, TRANSFERENCIA, BIZUM

#### 📄 `RegistroAcceso.java`
**Función:** Registra cada escaneo de QR para control de acceso.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `idRegistro` | Long | ID autoincremental |
| `usuario` | Usuario | Usuario que accede |
| `congreso` | Congreso | Congreso (si aplica) |
| `sesion` | Sesion | Sesión (si aplica) |
| `tipoAcceso` | TipoAcceso | CONGRESO o SESION |
| `resultado` | ResultadoEscaneo | Resultado del escaneo |
| `gateId` | String | Identificador de puerta |
| `staff` | Usuario | Staff que escanea |
| `qrEscaneado` | String | Código QR escaneado |
| `observaciones` | String | Notas adicionales |
| `fechaHoraEscaneo` | LocalDateTime | Momento del escaneo |

**Enum ResultadoEscaneo:** VALIDO, YA_USADO, FALSIFICADO, CADUCADO, NO_VALIDO_SALA, NO_VALIDO_HORARIO

---

### 📁 Repositorios (Data Access Layer)

Los repositorios extienden `JpaRepository` y proporcionan métodos de acceso a datos:

#### 📄 `UsuarioRepository.java`
```java
Optional<Usuario> findByEmail(String email);
boolean existsByEmail(String email);
```

#### 📄 `CongresoRepository.java`
```java
List<Congreso> findByActivoTrue();
List<Congreso> findByCiudadContainingIgnoreCaseAndActivoTrue(String ciudad);
List<Congreso> findByTematicaContainingIgnoreCaseAndActivoTrue(String tematica);
List<Congreso> findByFechaRange(LocalDate fechaInicio, LocalDate fechaFin);
```

#### 📄 `SesionRepository.java`
```java
List<Sesion> findByCongreso_IdCongresoAndActivaTrueOrderByFechaHoraInicio(Long idCongreso);
List<Sesion> findSesionesConConflicto(Long idCongreso, LocalDateTime fechaInicio, LocalDateTime fechaFin);
```

#### 📄 `InscripcionCongresoRepository.java`
```java
List<InscripcionCongreso> findByAsistente_IdUsuarioAndActivaTrue(Long idAsistente);
boolean existsByAsistente_IdUsuarioAndCongreso_IdCongresoAndActivaTrue(Long idAsistente, Long idCongreso);
Long countInscripcionesConfirmadas(Long idCongreso);
```

#### 📄 `InscripcionSesionRepository.java`
```java
List<InscripcionSesion> findByAsistente_IdUsuarioAndActivaTrueOrderBySesion_FechaHoraInicio(Long idAsistente);
boolean existsByAsistente_IdUsuarioAndSesion_IdSesionAndActivaTrue(Long idAsistente, Long idSesion);
List<InscripcionSesion> findSesionesConConflictoParaAsistente(Long idAsistente, LocalDateTime fechaInicio, LocalDateTime fechaFin);
```

---

### 📁 Seguridad (Security Layer)

#### 📄 `JwtService.java`
**Función:** Servicio para operaciones con JWT.

**Métodos principales:**
- `generateToken(UserDetails)`: Genera un token JWT
- `extractUsername(String token)`: Extrae el email del token
- `isTokenValid(String token, UserDetails)`: Valida el token

**Configuración:** Lee `jwt.secret` y `jwt.expiration` de `application.properties`

#### 📄 `JwtAuthenticationFilter.java`
**Función:** Filtro que intercepta todas las peticiones HTTP.

**Flujo:**
1. Lee el header `Authorization`
2. Si contiene `Bearer <token>`, extrae el token
3. Valida el token
4. Autentica al usuario en el contexto de seguridad

#### 📄 `UserDetailsServiceImpl.java`
**Función:** Implementa `UserDetailsService` de Spring Security.

**Funcionamiento:**
1. Busca el usuario por email en la base de datos
2. Convierte el `TipoUsuario` a rol de Spring Security (`ROLE_ASISTENTE`, etc.)
3. Construye el objeto `UserDetails` con las credenciales

---

### 📄 `application.properties`
**Ubicación:** `backend-principal/src/main/resources/`

```properties
# Servidor
spring.application.name=backend-principal
server.port=8080

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/congresos_db
spring.datasource.username=admin
spring.datasource.password=admin123

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
jwt.secret=TU_CLAVE_SECRETA_MUY_LARGA_Y_SEGURA_PARA_FIRMAR_TOKENS_JWT_2025
jwt.expiration=86400000  # 24 horas en milisegundos

# Microservicio Verificador
ms.verificador.url=http://localhost:8082
```

---

## 🎫 MS Ticket Authority

### Descripción
El **MS Ticket Authority** es un servicio externo simulado que:
- Emite tickets QR con firma criptográfica
- Verifica tickets cuando son escaneados
- Mantiene un registro de tickets emitidos y usados

### Archivos Clave

#### 📄 `TicketController.java`
**Endpoints:**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/tickets` | Emitir un nuevo ticket |
| POST | `/api/v1/tickets/verify` | Verificar un ticket |
| GET | `/api/v1/tickets/{ticketId}/status` | Consultar estado |
| GET | `/api/v1/tickets/health` | Health check |

#### 📄 `TicketService.java`
**Función:** Lógica principal del servicio.

**Método `emitTicket`:**
1. Genera ID único: `TCK-XXXXXXXX`
2. Crea objeto `Ticket`
3. Genera firma JWT
4. Almacena en memoria (ConcurrentHashMap)
5. Genera payload QR en Base64
6. Retorna `EmitTicketResponse`

**Método `verifyTicket`:**
1. Valida la firma JWT
2. Extrae el ticketId
3. Busca el ticket en memoria
4. Verifica estados: caducado, no válido aún, ya usado
5. Si es válido, marca como usado
6. Retorna `VerifyTicketResponse`

**Estados de verificación:**
- `VALID`: Ticket válido
- `ALREADY_USED`: Ya fue usado
- `TAMPERED`: Firma manipulada
- `EXPIRED`: Caducado
- `NOT_YET_VALID`: Aún no es válido

#### 📄 `SignatureService.java`
**Función:** Firma y verifica tickets usando JWT.

**Métodos:**
- `generateSignature`: Crea JWT con ticketId y eventId
- `isSignatureValid`: Verifica la firma
- `extractTicketId`: Extrae ID del ticket

#### 📄 `Ticket.java`
**Función:** Modelo en memoria para tickets.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `ticketId` | String | ID único |
| `eventId` | String | ID del evento |
| `holderName` | String | Nombre del titular |
| `holderDoc` | String | Documento de identidad |
| `validFrom` | LocalDateTime | Válido desde |
| `validTo` | LocalDateTime | Válido hasta |
| `usages` | List<TicketUsage> | Lista de usos |
| `signature` | String | Firma JWT |

---

## 🔄 MS Verificador Adapter

### Descripción
El **MS Verificador Adapter** implementa el **patrón Adapter** para:
- Encapsular la comunicación con MS Ticket Authority
- Manejar reintentos automáticos
- Proporcionar una interfaz simplificada al backend principal

### ¿Por qué un Adapter?

1. **Desacoplamiento:** El backend principal no conoce los detalles de la API de Ticket Authority
2. **Resiliencia:** Implementa reintentos con backoff exponencial
3. **Extensibilidad:** Fácil de añadir caché, logging, métricas
4. **Intercambiabilidad:** Se puede cambiar el servicio de tickets sin modificar el backend

### Archivos Clave

#### 📄 `VerificadorController.java`
**Endpoints:**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/verificador/verifyTicket` | Verificar un ticket |
| GET | `/api/verificador/health` | Health check |

#### 📄 `VerificadorAdapter.java`
**Función:** Servicio adaptador con lógica de reintentos.

```java
public Mono<VerifyTicketResponse> verifyTicket(String qrPayload, String signature, String gateId) {
    return ticketAuthorityClient.verifyTicket(request)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))  // 3 reintentos
            .doOnSuccess(response -> log.info("OK"))
            .doOnError(error -> log.error("Error"));
}
```

**Características:**
- Usa `Mono` de Project Reactor (programación reactiva)
- 3 reintentos con backoff exponencial
- Logging antes de cada reintento

#### 📄 `TicketAuthorityClient.java`
**Función:** Cliente HTTP para llamar a Ticket Authority.

```java
public Mono<VerifyTicketResponse> verifyTicket(VerifyTicketRequest request) {
    return webClient.post()
            .uri("/api/v1/tickets/verify")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(VerifyTicketResponse.class);
}
```

**Características:**
- Usa `WebClient` de Spring WebFlux (no bloqueante)
- URL base configurada por variable de entorno

---

## 🖥️ Frontend

### Descripción
El **Frontend** es una aplicación **Vue.js 3** con:
- **Vite** como bundler (rápido y moderno)
- **Pinia** para manejo de estado
- **Vue Router** para navegación
- **Axios** para llamadas HTTP

### Archivos Clave

#### 📄 `main.js`
**Función:** Punto de entrada de la aplicación.

```javascript
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './assets/main.css'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.mount('#app')
```

#### 📄 `App.vue`
**Función:** Componente raíz con:
- Barra de navegación dinámica
- `router-view` para renderizar vistas
- Footer
- Lógica de autenticación para mostrar/ocultar enlaces

**Secciones del navbar:**
- Logo y enlace a catálogo
- Catálogo (siempre visible)
- Mis Congresos (solo asistentes)
- Mis Sesiones (solo asistentes)
- Crear Congreso (solo organizadores)
- Perfil (usuarios autenticados)
- Login/Registro (usuarios no autenticados)

#### 📄 `router/index.js`
**Función:** Configuración de rutas.

| Ruta | Vista | Autenticación | Rol |
|------|-------|---------------|-----|
| `/` | CatalogoView | No | - |
| `/congreso/:id` | CongresoDetalleView | No | - |
| `/login` | LoginView | Solo invitados | - |
| `/registro` | RegistroView | Solo invitados | - |
| `/mis-congresos` | MisCongresosView | Sí | ASISTENTE |
| `/mis-sesiones` | MisSesionesView | Sí | ASISTENTE |
| `/perfil` | PerfilView | Sí | - |

**Guard de navegación:**
```javascript
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.meta.guestOnly && authStore.isAuthenticated) {
    next('/')
  } else {
    next()
  }
})
```

#### 📄 `services/http.js`
**Función:** Cliente HTTP Axios configurado.

**Características:**
- URL base desde variable de entorno `VITE_API_URL`
- Interceptor de request: añade token JWT automáticamente
- Interceptor de response: maneja errores 401 (token expirado)

```javascript
http.interceptors.request.use((config) => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})
```

#### 📄 `stores/auth.js`
**Función:** Store Pinia para autenticación.

**Estado:**
- `user`: Objeto del usuario actual
- `token`: Token JWT

**Getters (computed):**
- `isAuthenticated`: ¿Está logueado?
- `isAsistente`, `isOrganizador`, `isStaff`: Rol del usuario

**Actions:**
- `login(email, password)`: Inicia sesión
- `registro(userData)`: Registra nuevo usuario
- `logout()`: Cierra sesión

---

### 📁 Vistas

#### 📄 `CatalogoView.vue`
**Función:** Muestra el catálogo de congresos disponibles.

**Características:**
- Filtros por nombre, ciudad y temática
- Grid de tarjetas de congresos
- Información: fechas, lugar, precio, temática
- Enlace a detalle de cada congreso

#### 📄 `CongresoDetalleView.vue`
**Función:** Muestra el detalle de un congreso y su programa.

**Características:**
- Información completa del congreso
- Botón de inscripción (si está autenticado)
- Lista de sesiones con horarios y aforo
- Inscripción a sesiones individuales

#### 📄 `LoginView.vue`
**Función:** Formulario de inicio de sesión.

**Flujo:**
1. Usuario ingresa email y contraseña
2. Se llama a `authStore.login()`
3. Si es exitoso, redirige a catálogo
4. Si falla, muestra mensaje de error

#### 📄 `RegistroView.vue`
**Función:** Formulario de registro de usuarios.

**Campos:**
- Nombre completo
- Email
- Contraseña (mínimo 6 caracteres)
- Tipo de usuario (ASISTENTE, ORGANIZADOR, STAFF)
- Organización (opcional)

#### 📄 `MisCongresosView.vue`
**Función:** Muestra los congresos donde está inscrito el usuario.

**Características:**
- Lista de inscripciones con estado (PENDIENTE, CONFIRMADA, CANCELADA)
- Botón para descargar QR
- Botón para cancelar inscripción
- Enlace al programa de cada congreso

#### 📄 `MisSesionesView.vue`
**Función:** Muestra las sesiones donde está inscrito el usuario.

**Características:**
- Lista de inscripciones a sesiones
- Información del ponente, fecha, sala
- Botón para descargar QR
- Botón para cancelar inscripción

#### 📄 `PerfilView.vue`
**Función:** Muestra y permite editar el perfil del usuario.

**Modos:**
- Visualización: Muestra datos del usuario
- Edición: Formulario para modificar datos

---

## 🔗 Comunicación entre Servicios

### Diagrama de Flujo de Comunicación

```
┌─────────────────────────────────────────────────────────────────────┐
│                           FLUJO DE DATOS                            │
└─────────────────────────────────────────────────────────────────────┘

1. AUTENTICACIÓN (Login)
   ┌────────┐    POST /api/auth/login    ┌─────────────────┐
   │Frontend│ ──────────────────────────▶│Backend Principal│
   │        │◀────────────────────────── │                 │
   └────────┘    {token, usuario}        └─────────────────┘

2. INSCRIPCIÓN A CONGRESO
   ┌────────┐  POST /api/inscripciones   ┌─────────────────┐
   │Frontend│ ──────────────────────────▶│Backend Principal│───▶ DB
   │        │◀────────────────────────── │                 │◀───
   └────────┘      {inscripcion}         └─────────────────┘

3. ESCANEO DE QR (Control de Acceso)
   ┌────────┐  POST /api/escaneo         ┌─────────────────┐
   │Frontend│ ──────────────────────────▶│Backend Principal│
   │        │                            └────────┬────────┘
   │        │                                     │
   │        │                                     ▼
   │        │                            ┌────────────────┐
   │        │                            │  Verificador   │
   │        │                            │    Adapter     │
   │        │                            └────────┬───────┘
   │        │                                     │
   │        │                                     ▼
   │        │                            ┌────────────────┐
   │        │                            │Ticket Authority│
   │        │                            └────────┬───────┘
   │        │                                     │
   │        │◀────────────────────────────────────┘
   └────────┘     {resultado verificación}
```

### Endpoints de Comunicación

#### Frontend → Backend Principal

| Acción | Método | Endpoint | Autenticación |
|--------|--------|----------|---------------|
| Login | POST | `/api/auth/login` | No |
| Registro | POST | `/api/auth/registro` | No |
| Buscar congresos | GET | `/api/congresos/buscar` | No |
| Ver detalle | GET | `/api/congresos/{id}/detalle` | No |
| Ver programa | GET | `/api/congresos/{id}/programa` | No |
| Inscribirse congreso | POST | `/api/inscripciones/congreso` | JWT |
| Inscribirse sesión | POST | `/api/inscripciones/sesion` | JWT |
| Mis congresos | GET | `/api/inscripciones/mis-congresos` | JWT |
| Mis sesiones | GET | `/api/inscripciones/mis-sesiones` | JWT |
| Cancelar inscripción | DELETE | `/api/inscripciones/...` | JWT |
| Escaneo QR | POST | `/api/escaneo/verificar` | JWT (Staff) |

#### Backend Principal → Verificador Adapter

| Acción | Método | Endpoint |
|--------|--------|----------|
| Verificar ticket | POST | `/api/verificador/verifyTicket` |

#### Verificador Adapter → Ticket Authority

| Acción | Método | Endpoint |
|--------|--------|----------|
| Verificar ticket | POST | `/api/v1/tickets/verify` |
| Emitir ticket | POST | `/api/v1/tickets` |
| Estado ticket | GET | `/api/v1/tickets/{id}/status` |

---

## 🗄️ Base de Datos

### Esquema de Tablas

```sql
┌─────────────────────────────────────────────────────────────────┐
│                    ESQUEMA DE BASE DE DATOS                      │
└─────────────────────────────────────────────────────────────────┘

┌─────────────┐     ┌──────────────────┐     ┌─────────────────┐
│  usuarios   │     │    congresos     │     │    sesiones     │
├─────────────┤     ├──────────────────┤     ├─────────────────┤
│ id_usuario  │◀───┐│ id_congreso      │◀───┐│ id_sesion       │
│ nombre      │    ││ nombre           │    ││ id_congreso (FK)│
│ email       │    ││ descripcion      │    ││ titulo          │
│ password    │    ││ fecha_inicio     │    ││ ponente         │
│ tipo_usuario│    ││ fecha_fin        │    ││ sala            │
│ ...         │    ││ id_organizador(FK)────┘│ aforo_maximo    │
└─────────────┘    │└──────────────────┘     │ ...             │
      │            │                         └─────────────────┘
      │            │                                  │
      ▼            ▼                                  ▼
┌──────────────────────────┐              ┌───────────────────────┐
│ inscripciones_congreso   │              │ inscripciones_sesion  │
├──────────────────────────┤              ├───────────────────────┤
│ id_inscripcion           │              │ id_inscripcion_sesion │
│ id_asistente (FK)        │              │ id_asistente (FK)     │
│ id_congreso (FK)         │              │ id_sesion (FK)        │
│ estado                   │              │ ticket_qr             │
│ ticket_qr                │              │ activa                │
│ ticket_id                │              │ ...                   │
│ ...                      │              └───────────────────────┘
└───────────┬──────────────┘
            │
            ▼
┌──────────────────┐              ┌──────────────────────┐
│     pagos        │              │  registros_acceso    │
├──────────────────┤              ├──────────────────────┤
│ id_pago          │              │ id_registro          │
│ id_inscripcion(FK)              │ id_usuario (FK)      │
│ monto            │              │ id_congreso (FK)     │
│ estado           │              │ id_sesion (FK)       │
│ metodo_pago      │              │ tipo_acceso          │
│ transaccion_id   │              │ resultado            │
│ ...              │              │ gate_id              │
└──────────────────┘              │ ...                  │
                                  └──────────────────────┘
```

### Relaciones

| Tabla Origen | Tabla Destino | Tipo | Descripción |
|--------------|---------------|------|-------------|
| congresos | usuarios | ManyToOne | Organizador del congreso |
| sesiones | congresos | ManyToOne | Congreso al que pertenece |
| inscripciones_congreso | usuarios | ManyToOne | Asistente inscrito |
| inscripciones_congreso | congresos | ManyToOne | Congreso de inscripción |
| inscripciones_sesion | usuarios | ManyToOne | Asistente inscrito |
| inscripciones_sesion | sesiones | ManyToOne | Sesión de inscripción |
| pagos | inscripciones_congreso | OneToOne | Pago de inscripción |
| registros_acceso | usuarios | ManyToOne | Usuario que accede |
| registros_acceso | congresos | ManyToOne | Congreso de acceso |
| registros_acceso | sesiones | ManyToOne | Sesión de acceso |
| registros_acceso | usuarios | ManyToOne | Staff que escanea |

---

## 🐳 Docker y Despliegue

### docker-compose.yml

El archivo `docker-compose.yml` define 5 servicios:

#### 1. database (PostgreSQL)
```yaml
database:
  image: postgres:15-alpine
  container_name: congresos-db
  environment:
    POSTGRES_DB: congresos_db
    POSTGRES_USER: admin
    POSTGRES_PASSWORD: admin123
  ports:
    - "5432:5432"
  healthcheck:
    test: ["CMD-SHELL", "pg_isready -U admin -d congresos_db"]
```

#### 2. backend-principal
```yaml
backend-principal:
  build: ./backend-principal
  container_name: backend-principal
  ports:
    - "8080:8080"
  environment:
    SPRING_DATASOURCE_URL: jdbc:postgresql://database:5432/congresos_db
    MS_VERIFICADOR_URL: http://ms-verificador-adapter:8082
  depends_on:
    database:
      condition: service_healthy
```

#### 3. ms-ticket-authority
```yaml
ms-ticket-authority:
  build: ./ms-ticket-authority
  container_name: ms-ticket-authority
  ports:
    - "8081:8081"
```

#### 4. ms-verificador-adapter
```yaml
ms-verificador-adapter:
  build: ./ms-verificador-adapter
  container_name: ms-verificador-adapter
  ports:
    - "8082:8082"
  environment:
    TICKET_AUTHORITY_URL: http://ms-ticket-authority:8081
  depends_on:
    - ms-ticket-authority
```

#### 5. frontend
```yaml
frontend:
  build: ./frontend
  container_name: frontend-congresos
  ports:
    - "5173:5173"
  environment:
    VITE_API_URL: http://localhost:8080
  depends_on:
    - backend-principal
```

### Comandos de Despliegue

```bash
# Construir y levantar todos los servicios
docker-compose up --build

# Levantar en segundo plano
docker-compose up -d

# Ver logs de un servicio
docker-compose logs -f backend-principal

# Detener todos los servicios
docker-compose down

# Detener y eliminar volúmenes (¡borra datos!)
docker-compose down -v
```

### Puertos Expuestos

| Servicio | Puerto | URL |
|----------|--------|-----|
| Frontend | 5173 | http://localhost:5173 |
| Backend Principal | 8080 | http://localhost:8080 |
| MS Ticket Authority | 8081 | http://localhost:8081 |
| MS Verificador Adapter | 8082 | http://localhost:8082 |
| PostgreSQL | 5432 | localhost:5432 |

---

## 🔄 Flujos de Uso

### CU1: Registro de Asistente

```
1. Usuario accede a /registro
2. Completa formulario con datos
3. Frontend envía POST /api/auth/registro
4. Backend valida datos
5. Backend encripta contraseña con BCrypt
6. Backend guarda usuario en BD
7. Redirige a login
```

### CU2: Inicio de Sesión

```
1. Usuario accede a /login
2. Ingresa email y contraseña
3. Frontend envía POST /api/auth/login
4. Backend valida credenciales
5. Backend genera token JWT
6. Frontend almacena token en localStorage
7. Redirige a catálogo
```

### CU4: Inscribirse a Congreso

```
1. Usuario ve detalle de congreso
2. Click en "Inscribirse"
3. Frontend envía POST /api/inscripciones/congreso
4. Backend verifica:
   - Usuario autenticado
   - No inscrito previamente
   - Congreso activo
5. Backend crea InscripcionCongreso
6. Si es de pago, estado = PENDIENTE
7. Si es gratuito:
   - Estado = CONFIRMADA
   - Llama a MS Ticket Authority para generar QR
8. Retorna inscripción con ticketQR
```

### CU5: Pago de Inscripción

```
1. Usuario ve inscripción PENDIENTE
2. Selecciona método de pago
3. Frontend envía POST /api/pagos/procesar
4. Backend crea registro de Pago
5. (Simulado) Pasarela confirma pago
6. Backend actualiza estado a COMPLETADO
7. Backend actualiza inscripción a CONFIRMADA
8. Genera ticket QR
```

### CU7: Inscribirse a Sesión

```
1. Usuario ve programa del congreso
2. Click en "Inscribirse a Sesión"
3. Frontend envía POST /api/inscripciones/sesion
4. Backend verifica:
   - Usuario inscrito al congreso
   - Hay plazas disponibles
   - No hay conflicto de horario
5. Backend incrementa aforoActual
6. Genera ticket QR para sesión
7. Retorna inscripción
```

### CU9: Escaneo y Control de Acceso

```
1. Staff escanea código QR
2. Frontend envía POST /api/escaneo/verificar
3. Backend llama a MS Verificador Adapter
4. Verificador llama a MS Ticket Authority
5. Ticket Authority verifica:
   - Firma válida
   - No caducado
   - No usado previamente
6. Si es válido, marca como usado
7. Backend registra en registros_acceso
8. Muestra resultado al staff
```

---

## 📚 Glosario

| Término | Descripción |
|---------|-------------|
| **JWT** | JSON Web Token, estándar para tokens de autenticación |
| **JPA** | Java Persistence API, especificación para ORM en Java |
| **Hibernate** | Implementación de JPA usada en Spring |
| **WebFlux** | Framework reactivo de Spring para I/O no bloqueante |
| **Pinia** | Librería de manejo de estado para Vue.js 3 |
| **Vite** | Build tool moderno y rápido para desarrollo frontend |
| **QR** | Quick Response, código bidimensional |
| **CORS** | Cross-Origin Resource Sharing, política de seguridad web |
| **BCrypt** | Algoritmo de hash para contraseñas |
| **Adapter Pattern** | Patrón de diseño que permite la comunicación entre interfaces incompatibles |

---

## 🔐 Seguridad

### Medidas Implementadas

1. **Contraseñas encriptadas** con BCrypt
2. **Tokens JWT** con expiración de 24 horas
3. **CORS** configurado para orígenes específicos
4. **Autorización por roles** (ASISTENTE, ORGANIZADOR, STAFF)
5. **CSRF deshabilitado** (apropiado para APIs stateless)
6. **Filtro de autenticación** en cada petición
7. **Firmas criptográficas** en tickets QR

### Roles y Permisos

| Rol | Permisos |
|-----|----------|
| ASISTENTE | Ver congresos, inscribirse, ver mis inscripciones |
| ORGANIZADOR | Todo lo de asistente + crear congresos, ver estadísticas |
| STAFF | Todo lo de asistente + escanear QR en puertas |

---

## 📝 Notas Adicionales

### Mejoras Potenciales

1. **Caché distribuida** (Redis) para sesiones de tickets
2. **Message broker** (RabbitMQ/Kafka) para eventos asíncronos
3. **API Gateway** para unificar endpoints
4. **Service discovery** (Eureka) para microservicios
5. **Circuit breaker** (Resilience4j) para tolerancia a fallos
6. **Tests automatizados** con JUnit y Cypress

### Consideraciones de Producción

1. Cambiar `ddl-auto` de `update` a `validate`
2. Externalizar secrets (Vault, AWS Secrets Manager)
3. Implementar logging centralizado (ELK Stack)
4. Añadir monitorización (Prometheus, Grafana)
5. Configurar HTTPS con certificados SSL/TLS
6. Implementar rate limiting

---

*Documentación generada para el proyecto TT_ASI - Sistema de Gestión de Congresos Académicos*
*Última actualización: 2025*
