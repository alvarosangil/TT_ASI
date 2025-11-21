<template>
  <div class="login-view">
    <div class="login-container">
      <div class="card login-card">
        <h1>🔐 Iniciar Sesión</h1>
        <p class="subtitle">Accede a tu cuenta para gestionar tus congresos</p>

        <div v-if="error" class="error-message">{{ error }}</div>

        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label for="email">Email</label>
            <input 
              id="email"
              v-model="formData.email" 
              type="email" 
              required
              placeholder="tu@email.com"
            />
          </div>

          <div class="form-group">
            <label for="password">Contraseña</label>
            <input 
              id="password"
              v-model="formData.password" 
              type="password" 
              required
              placeholder="••••••••"
            />
          </div>

          <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
            {{ loading ? 'Iniciando sesión...' : 'Iniciar Sesión' }}
          </button>
        </form>

        <div class="login-footer">
          <p>¿No tienes cuenta? 
            <router-link to="/registro">Regístrate aquí</router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const formData = ref({
  email: '',
  password: ''
})

const loading = ref(false)
const error = ref('')

const handleLogin = async () => {
  loading.value = true
  error.value = ''

  const result = await authStore.login(formData.value.email, formData.value.password)

  if (result.success) {
    router.push('/')
  } else {
    error.value = result.message
  }

  loading.value = false
}
</script>

<style scoped>
.login-view {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}

.login-container {
  width: 100%;
  max-width: 450px;
}

.login-card {
  padding: 2.5rem;
}

.login-card h1 {
  text-align: center;
  margin-bottom: 0.5rem;
  color: #2d3748;
}

.subtitle {
  text-align: center;
  color: #718096;
  margin-bottom: 2rem;
}

.btn-block {
  width: 100%;
  margin-top: 1rem;
}

.login-footer {
  text-align: center;
  margin-top: 1.5rem;
  color: #718096;
}

.login-footer a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
}

.login-footer a:hover {
  text-decoration: underline;
}
</style>
