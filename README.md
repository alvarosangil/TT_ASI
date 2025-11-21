# Sistema de Gestión de Congresos Académicos

## 📋 Descripción del Proyecto

Sistema completo para gestionar congresos académicos con inscripciones, pagos, sesiones y control de acceso mediante códigos QR.

## 🏗️ Arquitectura

El proyecto está compuesto por:

- **Backend Principal** (Spring Boot + PostgreSQL): Gestión de usuarios, congresos, inscripciones y sesiones
- **MS Ticket Authority** (Spring Boot): Servicio externo para emisión y verificación de tickets QR
- **MS Verificador Adapter** (Spring Boot): Microservicio adaptador que encapsula la comunicación con Ticket Authority
- **Frontend** (Vue.js 3 + Vite): Interfaz de usuario para asistentes, organizadores y staff
- **Base de Datos** (PostgreSQL): Almacenamiento relacional

## 🚀 Tecnologías

- **Backend**: Java 17, Spring Boot 3.x, Spring Security, JWT, JPA/Hibernate
- **Frontend**: Vue.js 3, Vite, Vue Router, Axios
- **Base de Datos**: PostgreSQL 15
- **Contenedores**: Docker, Docker Compose
- **Build**: Maven

## 📁 Estructura del Proyecto

```
TT_ASI/
├── backend-principal/          # Servicio principal
├── ms-ticket-authority/        # Servicio externo de tickets
├── ms-verificador-adapter/     # Adaptador de verificación
├── frontend/                   # Aplicación Vue.js
├── docker-compose.yml          # Orquestación de servicios
└── README.md
```

## 🐳 Inicio Rápido con Docker

```bash
# Construir y levantar todos los servicios
docker-compose up --build

# Servicios disponibles:
# - Backend Principal: http://localhost:8080
# - MS Ticket Authority: http://localhost:8081
# - MS Verificador: http://localhost:8082
# - Frontend: http://localhost:5173
# - PostgreSQL: localhost:5432
```

## 👥 Actores del Sistema

- **Asistente**: Usuario que se inscribe a congresos y sesiones
- **Organizador**: Crea y gestiona congresos, sesiones y estadísticas
- **Staff**: Personal que escanea QR en puertas y salas

## 🎯 Casos de Uso Principales

### Asistente
- CU1: Registro de Asistente
- CU2: Inicio de Sesión
- CU4: Inscribirse a Congreso
- CU5: Pago de Inscripción
- CU6: Ver Mis Congresos
- CU7: Inscribirse a Sesión
- CU8: Ver Mis Sesiones

### Organizador
- CU23: Crear Congreso
- CU21: Ver Estadísticas

### Staff
- CU9: Escaneo y Control de Acceso

## 📦 Instalación Manual

### Backend Principal
```bash
cd backend-principal
mvn clean install
mvn spring-boot:run
```

### MS Ticket Authority
```bash
cd ms-ticket-authority
mvn clean install
mvn spring-boot:run
```

### MS Verificador Adapter
```bash
cd ms-verificador-adapter
mvn clean install
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

## 🔐 Autenticación

El sistema utiliza JWT (JSON Web Tokens) para la autenticación. Los tokens se generan en el login y deben incluirse en el header `Authorization: Bearer <token>` en todas las peticiones protegidas.

## 📊 Base de Datos

Ver scripts SQL en `backend-principal/src/main/resources/schema.sql`

## 📝 Licencia

Proyecto Académico - Universidad
