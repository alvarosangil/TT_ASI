<template>
  <div class="mis-sesiones-view">
    <h1 class="page-title">📚 Mis Sesiones</h1>

    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>Cargando tus sesiones...</p>
    </div>

    <div v-else-if="inscripciones.length === 0" class="empty-state">
      <p>Aún no estás inscrito en ninguna sesión</p>
      <router-link to="/mis-congresos" class="btn btn-primary">Ver Mis Congresos</router-link>
    </div>

    <div v-else class="sesiones-container">
      <div 
        v-for="inscripcion in inscripciones" 
        :key="inscripcion.idInscripcionSesion"
        class="sesion-card"
      >
        <div class="sesion-header">
          <h3>{{ inscripcion.sesion.titulo }}</h3>
          <span class="badge badge-activa" v-if="inscripcion.activa">Activa</span>
          <span class="badge badge-cancelada" v-else>Cancelada</span>
        </div>

        <p class="congreso-nombre">
          🎓 {{ inscripcion.sesion.congreso.nombre }}
        </p>

        <div class="sesion-info">
          <p>👤 <strong>Ponente:</strong> {{ inscripcion.sesion.ponente || 'Por confirmar' }}</p>
          <p>🕐 <strong>Fecha y Hora:</strong> {{ formatDateTime(inscripcion.sesion.fechaHoraInicio) }}</p>
          <p>🚪 <strong>Sala:</strong> {{ inscripcion.sesion.sala }}</p>
        </div>

        <p class="sesion-descripcion">{{ inscripcion.sesion.descripcion }}</p>

        <div class="sesion-actions">
          <button 
            v-if="inscripcion.ticketQR"
            @click="descargarQR(inscripcion.ticketQR, inscripcion.sesion.titulo)"
            class="btn btn-primary btn-sm"
          >
            📲 Descargar QR
          </button>

          <button 
            v-if="inscripcion.activa"
            @click="cancelarInscripcion(inscripcion.idInscripcionSesion)"
            class="btn btn-danger btn-sm"
          >
            Cancelar
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import http from '../services/http'

const inscripciones = ref([])
const loading = ref(false)

const cargarInscripciones = async () => {
  loading.value = true
  try {
    const response = await http.get('/inscripciones/mis-sesiones')
    inscripciones.value = response.data
  } catch (error) {
    console.error('Error al cargar inscripciones:', error)
  } finally {
    loading.value = false
  }
}

const cancelarInscripcion = async (idInscripcionSesion) => {
  if (!confirm('¿Estás seguro de que deseas cancelar la inscripción a esta sesión?')) {
    return
  }

  try {
    await http.delete(`/inscripciones/sesion/${idInscripcionSesion}`)
    alert('Inscripción a sesión cancelada correctamente')
    cargarInscripciones()
  } catch (error) {
    alert('Error al cancelar inscripción: ' + (error.response?.data?.message || 'Error desconocido'))
  }
}

const descargarQR = (qrData, tituloSesion) => {
  const element = document.createElement('a')
  element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(qrData))
  element.setAttribute('download', `QR_Sesion_${tituloSesion}.txt`)
  element.style.display = 'none'
  document.body.appendChild(element)
  element.click()
  document.body.removeChild(element)
}

const formatDateTime = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString('es-ES', { 
    day: '2-digit', 
    month: '2-digit', 
    year: 'numeric',
    hour: '2-digit', 
    minute: '2-digit' 
  })
}

onMounted(() => {
  cargarInscripciones()
})
</script>

<style scoped>
.page-title {
  font-size: 2.5rem;
  margin-bottom: 2rem;
  color: #2d3748;
}

.sesiones-container {
  display: grid;
  gap: 1.5rem;
}

.sesion-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
  border-left: 4px solid #667eea;
}

.sesion-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 1rem;
}

.sesion-header h3 {
  color: #2d3748;
  font-size: 1.4rem;
  flex: 1;
}

.congreso-nombre {
  color: #667eea;
  font-weight: 600;
  margin-bottom: 1rem;
  font-size: 1.1rem;
}

.badge {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 600;
}

.badge-activa {
  background: #c6f6d5;
  color: #2f855a;
}

.badge-cancelada {
  background: #fed7d7;
  color: #c53030;
}

.sesion-info {
  margin-bottom: 1rem;
  color: #4a5568;
}

.sesion-info p {
  margin-bottom: 0.5rem;
}

.sesion-descripcion {
  color: #718096;
  margin-bottom: 1.5rem;
  line-height: 1.6;
}

.sesion-actions {
  display: flex;
  gap: 0.5rem;
}

.btn-sm {
  padding: 0.5rem 1rem;
  font-size: 0.9rem;
}

.empty-state {
  text-align: center;
  padding: 4rem;
  color: #718096;
}

.empty-state .btn {
  margin-top: 1rem;
}
</style>
