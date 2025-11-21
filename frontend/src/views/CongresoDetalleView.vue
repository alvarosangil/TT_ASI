<template>
  <div class="congreso-detalle">
    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>Cargando información del congreso...</p>
    </div>

    <div v-else-if="congreso">
      <!-- Cabecera del congreso (CU19) -->
      <div class="card congreso-header">
        <h1>{{ congreso.nombre }}</h1>
        <div class="congreso-meta">
          <p><strong>📅 Fechas:</strong> {{ formatDate(congreso.fechaInicio) }} - {{ formatDate(congreso.fechaFin) }}</p>
          <p><strong>📍 Lugar:</strong> {{ congreso.lugar }}, {{ congreso.ciudad }}</p>
          <p><strong>💰 Precio:</strong> {{ congreso.esPago ? `${congreso.precio}€` : 'Gratuito' }}</p>
          <p v-if="congreso.tematica"><strong>🏷️ Temática:</strong> {{ congreso.tematica }}</p>
        </div>
        <p class="congreso-descripcion">{{ congreso.descripcion }}</p>

        <!-- Botón de inscripción (CU4) -->
        <div v-if="authStore.isAuthenticated && authStore.isAsistente" class="actions">
          <button 
            v-if="!estaInscrito" 
            @click="inscribirse" 
            class="btn btn-primary"
            :disabled="inscribiendo"
          >
            {{ inscribiendo ? 'Inscribiendo...' : 'Inscribirse al Congreso' }}
          </button>
          <p v-else class="success-message">✅ Ya estás inscrito en este congreso</p>
        </div>
        <p v-else-if="!authStore.isAuthenticated" class="info-message">
          Inicia sesión para inscribirte
        </p>
      </div>

      <!-- Programa de sesiones (CU3) -->
      <div class="card programa-sesiones">
        <h2>📋 Programa de Sesiones</h2>
        
        <div v-if="sesiones.length === 0" class="empty-state">
          <p>Aún no hay sesiones programadas</p>
        </div>

        <div v-else class="sesiones-list">
          <div 
            v-for="sesion in sesiones" 
            :key="sesion.idSesion"
            class="sesion-card"
          >
            <h3>{{ sesion.titulo }}</h3>
            <p class="sesion-ponente" v-if="sesion.ponente">👤 {{ sesion.ponente }}</p>
            <p class="sesion-descripcion">{{ sesion.descripcion }}</p>
            <div class="sesion-detalles">
              <p>🕐 {{ formatDateTime(sesion.fechaHoraInicio) }} - {{ formatTime(sesion.fechaHoraFin) }}</p>
              <p>🚪 Sala: {{ sesion.sala }}</p>
              <p>👥 Aforo: {{ sesion.aforoActual }} / {{ sesion.aforoMaximo }}</p>
            </div>
            
            <!-- Botón de inscripción a sesión (CU7) -->
            <button 
              v-if="authStore.isAuthenticated && authStore.isAsistente && estaInscrito"
              @click="inscribirseASesion(sesion.idSesion)"
              class="btn btn-secondary"
              :disabled="!sesion.hayPlazasDisponibles"
            >
              {{ sesion.hayPlazasDisponibles ? 'Inscribirse a Sesión' : 'Aforo Completo' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import http from '../services/http'

const route = useRoute()
const authStore = useAuthStore()

const congreso = ref(null)
const sesiones = ref([])
const loading = ref(false)
const estaInscrito = ref(false)
const inscribiendo = ref(false)

const cargarCongreso = async () => {
  loading.value = true
  try {
    const id = route.params.id
    const response = await http.get(`/congresos/${id}/detalle`)
    congreso.value = response.data
    
    // Cargar sesiones
    const sesionesResponse = await http.get(`/congresos/${id}/programa`)
    sesiones.value = sesionesResponse.data
    
    // Verificar si está inscrito
    if (authStore.isAuthenticated) {
      const inscripcionResponse = await http.get(`/inscripciones/congreso/${id}/estado`)
      estaInscrito.value = inscripcionResponse.data.inscrito
    }
  } catch (error) {
    console.error('Error al cargar congreso:', error)
  } finally {
    loading.value = false
  }
}

const inscribirse = async () => {
  inscribiendo.value = true
  try {
    await http.post('/inscripciones/congreso', {
      idCongreso: congreso.value.idCongreso
    })
    estaInscrito.value = true
    alert('Inscripción realizada con éxito')
  } catch (error) {
    alert('Error al inscribirse: ' + (error.response?.data?.message || 'Error desconocido'))
  } finally {
    inscribiendo.value = false
  }
}

const inscribirseASesion = async (idSesion) => {
  try {
    await http.post('/inscripciones/sesion', { idSesion })
    alert('Te has inscrito a la sesión correctamente')
    cargarCongreso() // Recargar para actualizar aforo
  } catch (error) {
    alert('Error al inscribirse a la sesión: ' + (error.response?.data?.message || 'Error desconocido'))
  }
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

const formatDateTime = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString('es-ES', { 
    day: '2-digit', 
    month: '2-digit', 
    hour: '2-digit', 
    minute: '2-digit' 
  })
}

const formatTime = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' })
}

onMounted(() => {
  cargarCongreso()
})
</script>

<style scoped>
.congreso-header h1 {
  font-size: 2.5rem;
  margin-bottom: 1.5rem;
  color: #2d3748;
}

.congreso-meta {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: #f7fafc;
  border-radius: 8px;
}

.congreso-descripcion {
  margin-bottom: 2rem;
  line-height: 1.8;
  color: #4a5568;
}

.actions {
  margin-top: 2rem;
}

.info-message {
  background: #bee3f8;
  color: #2c5282;
  padding: 1rem;
  border-radius: 6px;
  margin-top: 1rem;
}

.programa-sesiones h2 {
  margin-bottom: 1.5rem;
  color: #2d3748;
}

.sesiones-list {
  display: grid;
  gap: 1.5rem;
}

.sesion-card {
  background: #f7fafc;
  padding: 1.5rem;
  border-radius: 8px;
  border-left: 4px solid #667eea;
}

.sesion-card h3 {
  color: #2d3748;
  margin-bottom: 0.5rem;
}

.sesion-ponente {
  color: #667eea;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.sesion-descripcion {
  color: #4a5568;
  margin-bottom: 1rem;
}

.sesion-detalles {
  display: flex;
  gap: 2rem;
  margin-bottom: 1rem;
  color: #4a5568;
  font-size: 0.9rem;
}

.empty-state {
  text-align: center;
  padding: 3rem;
  color: #718096;
}
</style>
