<template>
  <div id="app">
    <nav class="navbar">
      <div class="nav-container">
        <router-link to="/" class="nav-brand">
          <h1>🎓 Congresos Académicos</h1>
        </router-link>
        
        <div class="nav-links">
          <router-link to="/" class="nav-link">Catálogo</router-link>
          
          <template v-if="isAuthenticated">
            <router-link v-if="isAsistente" to="/mis-congresos" class="nav-link">
              Mis Congresos
            </router-link>
            <router-link v-if="isAsistente" to="/mis-sesiones" class="nav-link">
              Mis Sesiones
            </router-link>
            <router-link v-if="isOrganizador" to="/crear-congreso" class="nav-link">
              Crear Congreso
            </router-link>
            <router-link to="/perfil" class="nav-link">Perfil</router-link>
            <button @click="logout" class="btn-logout">Cerrar Sesión</button>
          </template>
          
          <template v-else>
            <router-link to="/login" class="btn-primary">Iniciar Sesión</router-link>
            <router-link to="/registro" class="btn-secondary">Registrarse</router-link>
          </template>
        </div>
      </div>
    </nav>

    <main class="main-content">
      <router-view />
    </main>

    <footer class="footer">
      <p>&copy; 2025 Sistema de Gestión de Congresos Académicos</p>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const isAuthenticated = computed(() => authStore.isAuthenticated)
const isAsistente = computed(() => authStore.user?.tipoUsuario === 'ASISTENTE')
const isOrganizador = computed(() => authStore.user?.tipoUsuario === 'ORGANIZADOR')

const logout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 1rem 0;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-brand {
  text-decoration: none;
  color: white;
}

.nav-brand h1 {
  font-size: 1.5rem;
  margin: 0;
}

.nav-links {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.nav-link {
  color: white;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background 0.3s;
}

.nav-link:hover {
  background: rgba(255,255,255,0.1);
}

.btn-primary, .btn-secondary, .btn-logout {
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  text-decoration: none;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s;
}

.btn-primary {
  background: white;
  color: #667eea;
  font-weight: 600;
}

.btn-secondary {
  background: transparent;
  color: white;
  border: 2px solid white;
}

.btn-logout {
  background: rgba(255,255,255,0.2);
  color: white;
}

.btn-primary:hover, .btn-secondary:hover, .btn-logout:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.2);
}

.main-content {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 2rem auto;
  padding: 0 2rem;
}

.footer {
  background: #2d3748;
  color: white;
  text-align: center;
  padding: 2rem;
  margin-top: auto;
}
</style>
