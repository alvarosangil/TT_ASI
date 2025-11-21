<template>
  <div class="registro-view">
    <div class="registro-container">
      <div class="card registro-card">
        <h1>📝 Registro de Usuario</h1>
        <p class="subtitle">Crea tu cuenta para acceder a todos los congresos</p>

        <div v-if="error" class="error-message">{{ error }}</div>
        <div v-if="success" class="success-message">{{ success }}</div>

        <form @submit.prevent="handleRegistro">
          <div class="form-group">
            <label for="nombreCompleto">Nombre Completo</label>
            <input 
              id="nombreCompleto"
              v-model="formData.nombreCompleto" 
              type="text" 
              required
              placeholder="Juan Pérez"
            />
          </div>

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
              minlength="6"
              placeholder="Mínimo 6 caracteres"
            />
          </div>

          <div class="form-group">
            <label for="tipoUsuario">Tipo de Usuario</label>
            <select id="tipoUsuario" v-model="formData.tipoUsuario" required>
              <option value="">Selecciona...</option>
              <option value="ASISTENTE">Asistente</option>
              <option value="ORGANIZADOR">Organizador</option>
              <option value="STAFF">Staff</option>
            </select>
          </div>

          <div class="form-group" v-if="formData.tipoUsuario !== 'ASISTENTE'">
            <label for="organizacion">Organización (opcional)</label>
            <input 
              id="organizacion"
              v-model="formData.organizacion" 
              type="text" 
              placeholder="Universidad, Empresa, etc."
            />
          </div>

          <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
            {{ loading ? 'Registrando...' : 'Crear Cuenta' }}
          </button>
        </form>

        <div class="registro-footer">
          <p>¿Ya tienes cuenta? 
            <router-link to="/login">Inicia sesión aquí</router-link>
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
  nombreCompleto: '',
  email: '',
  password: '',
  tipoUsuario: '',
  organizacion: ''
})

const loading = ref(false)
const error = ref('')
const success = ref('')

const handleRegistro = async () => {
  loading.value = true
  error.value = ''
  success.value = ''

  const result = await authStore.registro(formData.value)

  if (result.success) {
    success.value = 'Registro exitoso. Redirigiendo...'
    setTimeout(() => {
      router.push('/login')
    }, 2000)
  } else {
    error.value = result.message
  }

  loading.value = false
}
</script>

<style scoped>
.registro-view {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
  padding: 2rem 0;
}

.registro-container {
  width: 100%;
  max-width: 500px;
}

.registro-card {
  padding: 2.5rem;
}

.registro-card h1 {
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

.registro-footer {
  text-align: center;
  margin-top: 1.5rem;
  color: #718096;
}

.registro-footer a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
}

.registro-footer a:hover {
  text-decoration: underline;
}
</style>
