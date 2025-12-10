# 🏗️ DIAGRAMA DE COMPONENTES DETALLADO - Sistema de Gestión de Congresos

## Arquitectura Completa con todos los componentes

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                              <<Frontend>> WebApp Vue.js                                 │
│                                                                                          │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐  │
│  │                           <<Component>> Components                                │  │
│  │  ┌────────────────────────────────────────────────────────────────────────────┐  │  │
│  │  │ LoginView.vue    │ RegistroView.vue  │ CatalogoView.vue                    │  │  │
│  │  │ PerfilView.vue   │ MisCongresosView.vue │ MisSesionesView.vue              │  │  │
│  │  │ CongresoDetalleView.vue │ CrearCongresoView.vue                            │  │  │
│  │  │ MisCongresoOrganizadorView.vue │ GestionarSesionesView.vue                 │  │  │
│  │  │ CrearSesionView.vue                                                         │  │  │
│  │  └────────────────────────────────────────────────────────────────────────────┘  │  │
│  └──────────────────────────────────────────────────────────────────────────────────┘  │
│                                         │                                                │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐  │
│  │                           <<Component>> Routing/State                             │  │
│  │  ┌────────────────────────────────────────────────────────────────────────────┐  │  │
│  │  │ router/index.js  (Vue Router)                                              │  │  │
│  │  │ stores/auth.js   (Pinia Store - Estado de autenticación)                  │  │  │
│  │  │ services/http.js (Axios HTTP Client con interceptores JWT)                │  │  │
│  │  └────────────────────────────────────────────────────────────────────────────┘  │  │
│  └──────────────────────────────────────────────────────────────────────────────────┘  │
└────────────────────────────────┬────────────────────────────────────────────────────────┘
                                 │ HTTP/REST (JSON)
                                 │ Authorization: Bearer <JWT>
                                 ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                        <<Backend>> Backend Principal (Spring Boot)                      │
│                                    Puerto: 8080                                          │
│                                                                                          │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐  │
│  │                         <<Component>> REST Controllers                            │  │
│  │  ┌────────────────────────────────────────────────────────────────────────────┐  │  │
│  │  │ AuthController                                                             │  │  │
│  │  │   POST /api/auth/login                                                     │  │  │
│  │  │   POST /api/auth/registro                                                  │  │  │
│  │  │                                                                            │  │  │
│  │  │ CongresoController                                                         │  │  │
│  │  │   GET  /api/congresos/catalogo                                            │  │  │
│  │  │   GET  /api/congresos/{id}                                                │  │  │
│  │  │   GET  /api/congresos/mis-congresos                                       │  │  │
│  │  │   POST /api/congresos/crear                                               │  │  │
│  │  │   PUT  /api/congresos/{id}/editar                                         │  │  │
│  │  │   DELETE /api/congresos/{id}/eliminar                                     │  │  │
│  │  │                                                                            │  │  │
│  │  │ SesionController                                                           │  │  │
│  │  │   GET  /api/sesiones/congreso/{idCongreso}                                │  │  │
│  │  │   GET  /api/sesiones/{id}                                                 │  │  │
│  │  │   POST /api/sesiones/crear                                                │  │  │
│  │  │   PUT  /api/sesiones/{id}/editar                                          │  │  │
│  │  │   DELETE /api/sesiones/{id}/eliminar                                      │  │  │
│  │  └────────────────────────────────────────────────────────────────────────────┘  │  │
│  └────────────────────────────────┬─────────────────────────────────────────────────┘  │
│                                   │                                                      │
│  ┌────────────────────────────────▼─────────────────────────────────────────────────┐  │
│  │                          <<Component>> DTOs (Data Transfer Objects)               │  │
│  │  ┌────────────────────────────────────────────────────────────────────────────┐  │  │
│  │  │ LoginRequest, LoginResponse, RegistroRequest                              │  │  │
│  │  │ CongresoDTO, CongresoRequest                                              │  │  │
│  │  │ SesionDTO, SesionRequest                                                  │  │  │
│  │  └────────────────────────────────────────────────────────────────────────────┘  │  │
│  └────────────────────────────────┬─────────────────────────────────────────────────┘  │
│                                   │                                                      │
│  ┌────────────────────────────────▼─────────────────────────────────────────────────┐  │
│  │                          <<Component>> Services                                   │  │
│  │  ┌────────────────────────────────────────────────────────────────────────────┐  │  │
│  │  │ AuthService                                                                │  │  │
│  │  │   - login(email, password): LoginResponse                                 │  │  │
│  │  │   - registrar(datos): LoginResponse                                       │  │  │
│  │  │   - generarToken(usuario): String                                         │  │  │
│  │  │                                                                            │  │  │
│  │  │ CongresoService                                                            │  │  │
│  │  │   - obtenerCatalogo(): List<CongresoDTO>                                  │  │  │
│  │  │   - obtenerPorId(id): CongresoDTO                                         │  │  │
│  │  │   - obtenerCongresosPorOrganizador(email): List<CongresoDTO>              │  │  │
│  │  │   - crearCongreso(request, organizadorId): CongresoDTO                    │  │  │
│  │  │   - editarCongreso(id, request, organizadorId): CongresoDTO               │  │  │
│  │  │   - eliminarCongreso(id, organizadorId): void                             │  │  │
│  │  │                                                                            │  │  │
│  │  │ SesionService                                                              │  │  │
│  │  │   - obtenerSesionesPorCongreso(idCongreso): List<SesionDTO>               │  │  │
│  │  │   - obtenerPorId(id): SesionDTO                                           │  │  │
│  │  │   - crearSesion(request, organizadorId): SesionDTO                        │  │  │
│  │  │   - editarSesion(id, request, organizadorId): SesionDTO                   │  │  │
│  │  │   - eliminarSesion(id, organizadorId): void                               │  │  │
│  │  │   - validarFechasSesion(sesion, congreso): void                           │  │  │
│  │  └────────────────────────────────────────────────────────────────────────────┘  │  │
│  └────────────────────────────────┬─────────────────────────────────────────────────┘  │
│                                   │                                                      │
│  ┌────────────────────────────────▼─────────────────────────────────────────────────┐  │
│  │                      <<Component>> Security                                       │  │
│  │  ┌────────────────────────────────────────────────────────────────────────────┐  │  │
│  │  │ JwtAuthenticationFilter   (Valida JWT en cada petición)                   │  │  │
│  │  │ JwtService               (Genera y valida tokens JWT)                     │  │  │
│  │  │ SecurityConfig           (Configuración Spring Security)                  │  │  │
│  │  │ CustomUserDetailsService (Carga detalles del usuario)                     │  │  │
│  │  └────────────────────────────────────────────────────────────────────────────┘  │  │
│  └──────────────────────────────────────────────────────────────────────────────────┘  │
│                                   │                                                      │
│  ┌────────────────────────────────▼─────────────────────────────────────────────────┐  │
│  │                     <<Persistence>> Repositories (Spring Data JPA)               │  │
│  │  ┌────────────────────────────────────────────────────────────────────────────┐  │  │
│  │  │ UsuarioRepository extends JpaRepository<Usuario, Long>                    │  │  │
│  │  │   - findByEmail(email): Optional<Usuario>                                 │  │  │
│  │  │   - existsByEmail(email): boolean                                         │  │  │
│  │  │                                                                            │  │  │
│  │  │ CongresoRepository extends JpaRepository<Congreso, Long>                  │  │  │
│  │  │   - findByActivoTrueOrderByFechaCreacionDesc(): List<Congreso>            │  │  │
│  │  │   - findByOrganizadorEmailOrderByFechaCreacionDesc(email): List<Congreso> │  │  │
│  │  │                                                                            │  │  │
│  │  │ SesionRepository extends JpaRepository<Sesion, Long>                      │  │  │
│  │  │   - findByCongresoIdOrderByFechaHoraInicio(idCongreso): List<Sesion>      │  │  │
│  │  │                                                                            │  │  │
│  │  │ InscripcionCongresoRepository                                             │  │  │
│  │  │ InscripcionSesionRepository                                               │  │  │
│  │  │ PagoRepository                                                            │  │  │
│  │  │ RegistroAccesoRepository                                                  │  │  │
│  │  └────────────────────────────────────────────────────────────────────────────┘  │  │
│  └──────────────────────────────────────────────────────────────────────────────────┘  │
│                                   │                                                      │
│                                   │ JPA/Hibernate                                        │
│                                   │ JDBC                                                 │
└───────────────────────────────────┼──────────────────────────────────────────────────────┘
                                    │
                                    ▼
                    ┌───────────────────────────────────────┐
                    │      <<Database>> PostgreSQL          │
                    │         Puerto: 5432                  │
                    │                                       │
                    │  Tablas:                              │
                    │  - usuarios                           │
                    │  - congresos                          │
                    │  - sesiones                           │
                    │  - inscripciones_congreso             │
                    │  - inscripciones_sesion               │
                    │  - pagos                              │
                    │  - registros_acceso                   │
                    └───────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                   <<Microservicio>> MS Verificador Adapter (Spring Boot)                │
│                                    Puerto: 8082                                          │
│                                                                                          │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐  │
│  │                         <<Component>> REST Controller                             │  │
│  │  ┌────────────────────────────────────────────────────────────────────────────┐  │  │
│  │  │ VerificadorController                                                      │  │  │
│  │  │   POST /api/verificador/verifyTicket                                      │  │  │
│  │  │   GET  /api/verificador/health                                            │  │  │
│  │  └────────────────────────────────────────────────────────────────────────────┘  │  │
│  └────────────────────────────────┬─────────────────────────────────────────────────┘  │
│                                   │                                                      │
│  ┌────────────────────────────────▼─────────────────────────────────────────────────┐  │
│  │                          <<Component>> DTOs                                       │  │
│  │  ┌────────────────────────────────────────────────────────────────────────────┐  │  │
│  │  │ VerifyTicketRequest, VerifyTicketResponse                                 │  │  │
│  │  └────────────────────────────────────────────────────────────────────────────┘  │  │
│  └────────────────────────────────┬─────────────────────────────────────────────────┘  │
│                                   │                                                      │
│  ┌────────────────────────────────▼─────────────────────────────────────────────────┐  │
│  │                          <<Component>> Service                                    │  │
│  │  ┌────────────────────────────────────────────────────────────────────────────┐  │  │
│  │  │ VerificadorAdapter                                                         │  │  │
│  │  │   - verifyTicket(qrPayload, signature, gateId): Mono<VerifyTicketResponse>│  │  │
│  │  │   - Implementa lógica de reintentos (Retry.backoff 3 intentos, 1s)        │  │  │
│  │  │   - Logging de todas las verificaciones                                   │  │  │
│  │  │   - Preparado para caché (futuro)                                         │  │  │
│  │  └────────────────────────────────────────────────────────────────────────────┘  │  │
│  └────────────────────────────────┬─────────────────────────────────────────────────┘  │
│                                   │                                                      │
│  ┌────────────────────────────────▼─────────────────────────────────────────────────┐  │
│  │                          <<Component>> HTTP Client                                │  │
│  │  ┌────────────────────────────────────────────────────────────────────────────┐  │  │
│  │  │ TicketAuthorityClient (WebClient reactivo)                                │  │  │
│  │  │   - verifyTicket(request): Mono<VerifyTicketResponse>                     │  │  │
│  │  │   - Usa WebFlux para llamadas no bloqueantes                              │  │  │
│  │  └────────────────────────────────────────────────────────────────────────────┘  │  │
│  └────────────────────────────────┬─────────────────────────────────────────────────┘  │
└────────────────────────────────────┼──────────────────────────────────────────────────────┘
                                     │ HTTP/REST
                                     │ POST http://ms-ticket-authority:8081/api/v1/tickets/verify
                                     ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                   <<Microservicio>> MS Ticket Authority (Spring Boot)                   │
│                                    Puerto: 8081                                          │
│                                                                                          │
│  ┌──────────────────────────────────────────────────────────────────────────────────┐  │
│  │                         <<Component>> REST Controller                             │  │
│  │  ┌────────────────────────────────────────────────────────────────────────────┐  │  │
│  │  │ TicketController                                                           │  │  │
│  │  │   POST   /api/v1/tickets              (Emitir nuevo ticket)               │  │  │
│  │  │   POST   /api/v1/tickets/verify       (Verificar ticket QR)               │  │  │
│  │  │   GET    /api/v1/tickets/{id}/status  (Consultar estado)                 │  │  │
│  │  │   GET    /api/v1/tickets/health       (Health check)                     │  │  │
│  │  └────────────────────────────────────────────────────────────────────────────┘  │  │
│  └────────────────────────────────┬─────────────────────────────────────────────────┘  │
│                                   │                                                      │
│  ┌────────────────────────────────▼─────────────────────────────────────────────────┐  │
│  │                          <<Component>> DTOs                                       │  │
│  │  ┌────────────────────────────────────────────────────────────────────────────┐  │  │
│  │  │ EmitTicketRequest, EmitTicketResponse                                     │  │  │
│  │  │ VerifyTicketRequest, VerifyTicketResponse                                 │  │  │
│  │  └────────────────────────────────────────────────────────────────────────────┘  │  │
│  └────────────────────────────────┬─────────────────────────────────────────────────┘  │
│                                   │                                                      │
│  ┌────────────────────────────────▼─────────────────────────────────────────────────┐  │
│  │                          <<Component>> Services                                   │  │
│  │  ┌────────────────────────────────────────────────────────────────────────────┐  │  │
│  │  │ TicketService                                                              │  │  │
│  │  │   - emitTicket(request): EmitTicketResponse                               │  │  │
│  │  │     * Genera ticketId único (TCK-XXXXXXXX)                                │  │  │
│  │  │     * Crea firma criptográfica                                            │  │  │
│  │  │     * Almacena en ticketStore (ConcurrentHashMap)                         │  │  │
│  │  │     * Genera QR payload (Base64)                                          │  │  │
│  │  │                                                                            │  │  │
│  │  │   - verifyTicket(request): VerifyTicketResponse                           │  │  │
│  │  │     * Valida firma criptográfica                                          │  │  │
│  │  │     * Verifica fechas (validFrom, validTo)                                │  │  │
│  │  │     * Verifica que no esté usado                                          │  │  │
│  │  │     * Marca como usado (registra gateId, timestamp)                       │  │  │
│  │  │     * Devuelve: VALID | TAMPERED | EXPIRED | NOT_YET_VALID | ALREADY_USED │  │  │
│  │  │                                                                            │  │  │
│  │  │   - getTicketStatus(ticketId): Map<String, Object>                        │  │  │
│  │  │                                                                            │  │  │
│  │  │ SignatureService                                                           │  │  │
│  │  │   - generateSignature(ticketId, eventId, validTo): String                │  │  │
│  │  │     * Usa HMAC-SHA256 con clave secreta                                   │  │  │
│  │  │   - isSignatureValid(signature): boolean                                  │  │  │
│  │  │   - extractTicketId(signature): String                                    │  │  │
│  │  └────────────────────────────────────────────────────────────────────────────┘  │  │
│  └────────────────────────────────┬─────────────────────────────────────────────────┘  │
│                                   │                                                      │
│  ┌────────────────────────────────▼─────────────────────────────────────────────────┐  │
│  │                          <<Component>> Model                                      │  │
│  │  ┌────────────────────────────────────────────────────────────────────────────┐  │  │
│  │  │ Ticket (POJO - almacenado en memoria)                                     │  │  │
│  │  │   - ticketId: String                                                      │  │  │
│  │  │   - eventId: String                                                       │  │  │
│  │  │   - holderName: String                                                    │  │  │
│  │  │   - holderDoc: String                                                     │  │  │
│  │  │   - validFrom: LocalDateTime                                              │  │  │
│  │  │   - validTo: LocalDateTime                                                │  │  │
│  │  │   - signature: String                                                     │  │  │
│  │  │   - usages: List<Usage>                                                   │  │  │
│  │  │   - createdAt: LocalDateTime                                              │  │  │
│  │  └────────────────────────────────────────────────────────────────────────────┘  │  │
│  └────────────────────────────────┬─────────────────────────────────────────────────┘  │
│                                   │                                                      │
│  ┌────────────────────────────────▼─────────────────────────────────────────────────┐  │
│  │                    <<Persistence>> In-Memory Storage                              │  │
│  │  ┌────────────────────────────────────────────────────────────────────────────┐  │  │
│  │  │ ConcurrentHashMap<String, Ticket> ticketStore                             │  │  │
│  │  │   - Thread-safe para concurrencia                                         │  │  │
│  │  │   - Sin base de datos (stateless)                                         │  │  │
│  │  │   - Alto rendimiento                                                      │  │  │
│  │  └────────────────────────────────────────────────────────────────────────────┘  │  │
│  └──────────────────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

## 📊 Resumen de Componentes por Capa

### Frontend (Vue.js)
- **11 Vistas**: Login, Registro, Catálogo, Detalle, Perfil, Mis Congresos, Mis Sesiones, Crear/Editar Congreso, Gestionar Sesiones, Crear/Editar Sesión
- **Router**: Gestión de rutas con guards de autenticación
- **Store**: Estado global con Pinia (auth)
- **HTTP Client**: Axios con interceptores JWT

### Backend Principal (Spring Boot)
- **3 Controllers**: Auth, Congreso, Sesion (15 endpoints REST)
- **6 DTOs**: LoginRequest/Response, RegistroRequest, CongresoDTO/Request, SesionDTO/Request
- **3 Services**: Lógica de negocio para autenticación, congresos y sesiones
- **7 Repositories (DAOs)**: Acceso a datos con Spring Data JPA
- **Security**: JWT Filter, JWT Service, Security Config
- **Database**: PostgreSQL con 7 tablas

### MS Verificador Adapter (Spring Boot)
- **1 Controller**: Verificador (2 endpoints)
- **2 DTOs**: VerifyTicketRequest, VerifyTicketResponse
- **1 Service**: VerificadorAdapter con lógica de reintentos
- **1 HTTP Client**: WebClient reactivo hacia Ticket Authority
- **Sin BD**: Stateless, solo proxy/adaptador

### MS Ticket Authority (Spring Boot)
- **1 Controller**: Ticket (4 endpoints)
- **4 DTOs**: EmitTicketRequest/Response, VerifyTicketRequest/Response
- **2 Services**: TicketService, SignatureService
- **1 Model**: Ticket (POJO)
- **Storage**: ConcurrentHashMap en memoria
- **Sin BD**: Stateless para máximo rendimiento

## 🔄 Flujo de Comunicación

```
Frontend → Backend Principal → PostgreSQL
                ↓
        MS Verificador Adapter → MS Ticket Authority (memoria)
```

## 🔐 Seguridad

- **Frontend → Backend**: JWT en header Authorization
- **Backend ↔ Microservicios**: HTTP interno en red Docker
- **Ticket Authority**: Firma HMAC-SHA256 con clave secreta

## 🐳 Puertos Docker

- Frontend: 5173
- Backend Principal: 8080
- MS Verificador: 8082
- MS Ticket Authority: 8081
- PostgreSQL: 5432
