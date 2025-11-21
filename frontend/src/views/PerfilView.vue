<template>
  <div class="perfil-view">
    <h1 class="page-title">👤 Mi Perfil</h1>

    <div class="card perfil-card">
      <div v-if="!editando" class="perfil-info">
        <div class="perfil-header">
          <div class="perfil-avatar">
            <img 
              :src="usuario.fotoPerfil || 'https://via.placeholder.com/150'" 
              alt="Foto de perfil"
            />
          </div>
          <div>
            <h2>{{ usuario.nombreCompleto }}</h2>
            <p class="tipo-usuario">{{ usuario.tipoUsuario }}</p>
          </div>
        </div>

        <div class="perfil-datos">
          <p><strong>Email:</strong> {{ usuario.email }}</p>
          <p v-if="usuario.organizacion">
            <strong>Organización:</strong> {{ usuario.organizacion }}
          </p>
          <p><strong>Miembro desde:</strong> {{ formatDate(usuario.fechaRegistro) }}</p>
        </div>

        <button @click="editando = true" class="btn btn-primary">
          Editar Perfil
        </button>
      </div>

      <div v-else class="perfil-editar">
        <h2>Editar Perfil</h2>

        <div v-if="error" class="error-message">{{ error }}</div>
        <div v-if="success" class="success-message">{{ success }}</div>

        <form @submit.prevent="guardarCambios">
          <div class="form-group">
            <label>Nombre Completo</label>
            <input v-model="formData.nombreCompleto" type="text" required />
          </div>

          <div class="form-group">
            <label>Email</label>
            <input v-model="formData.email" type="email" required />
          </div>

          <div class="form-group">
            <label>Organización</label>
            <input v-model="formData.organizacion" type="text" />
          </div>

          <div class="form-actions">
            <button type="submit" class="btn btn-primary" :disabled="loading">
              {{ loading ? 'Guardando...' : 'Guardar Cambios' }}
            </button>
            <button type="button" @click="cancelarEdicion" class="btn btn-secondary">
              Cancelar
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import http from '../services/http'

const authStore = useAuthStore()

const usuario = ref(authStore.user || {})
const editando = ref(false)
const loading = ref(false)
const error = ref('')
const success = ref('')

const formData = ref({
  nombreCompleto: '',
  email: '',
  organizacion: ''
})

const cargarPerfil = () => {
  formData.value = {
    nombreCompleto: usuario.value.nombreCompleto,
    email: usuario.value.email,
    organizacion: usuario.value.organizacion || ''
  }
}

const guardarCambios = async () => {
  loading.value = true
  error.value = ''
  success.value = ''

  try {
    const response = await http.put('/usuarios/perfil', formData.value)
    usuario.value = response.data
    
    // Actualizar store
    authStore.user = response.data
    localStorage.setItem('user', JSON.stringify(response.data))
    
    success.value = 'Perfil actualizado correctamente'
    setTimeout(() => {
      editando.value = false
      success.value = ''
    }, 2000)
  } catch (err) {
    error.value = err.response?.data?.message || 'Error al actualizar perfil'
  } finally {
    loading.value = false
  }
}

const cancelarEdicion = () => {
  editando.value = false
  error.value = ''
  success.value = ''
  cargarPerfil()
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('es-ES', { 
    day: '2-digit', 
    month: 'long', 
    year: 'numeric' 
  })
}

onMounted(() => {
  cargarPerfil()
})
</script>

<style scoped>
.page-title {
  font-size: 2.5rem;
  margin-bottom: 2rem;
  color: #2d3748;
}

.perfil-card {
  max-width: 600px;
}

.perfil-header {
  display: flex;
  gap: 2rem;
  align-items: center;
  margin-bottom: 2rem;
}

.perfil-avatar img {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #667eea;
}

.perfil-header h2 {
  color: #2d3748;
  margin-bottom: 0.5rem;
}

.tipo-usuario {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  display: inline-block;
  font-size: 0.9rem;
  font-weight: 600;
}

.perfil-datos {
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: #f7fafc;
  border-radius: 8px;
}

.perfil-datos p {
  margin-bottom: 1rem;
  color: #4a5568;
}

.perfil-datos strong {
  color: #2d3748;
}

.perfil-editar h2 {
  margin-bottom: 1.5rem;
  color: #2d3748;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}
</style>
