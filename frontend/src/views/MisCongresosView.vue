<template>
  <div class="mis-congresos-view">
    <h1 class="page-title">🎫 Mis Congresos</h1>

    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>Cargando tus congresos...</p>
    </div>

    <div v-else-if="inscripciones.length === 0" class="empty-state">
      <p>Aún no estás inscrito en ningún congreso</p>
      <router-link to="/" class="btn btn-primary">Explorar Congresos</router-link>
    </div>

    <div v-else class="grid grid-2">
      <div 
        v-for="inscripcion in inscripciones" 
        :key="inscripcion.idInscripcion"
        class="inscripcion-card"
      >
        <div class="inscripcion-header">
          <h3>{{ inscripcion.congreso.nombre }}</h3>
          <span class="badge" :class="getBadgeClass(inscripcion.estado)">
            {{ inscripcion.estado }}
          </span>
        </div>
        
        <div class="inscripcion-info">
          <p>📅 {{ formatDate(inscripcion.congreso.fechaInicio) }} - {{ formatDate(inscripcion.congreso.fechaFin) }}</p>
          <p>📍 {{ inscripcion.congreso.lugar }}</p>
          <p>💰 {{ inscripcion.congreso.esPago ? `${inscripcion.congreso.precio}€` : 'Gratuito' }}</p>
        </div>

        <div class="inscripcion-actions">
          <router-link 
            :to="`/congreso/${inscripcion.congreso.idCongreso}`" 
            class="btn btn-secondary btn-sm"
          >
            Ver Programa
          </router-link>
          
          <button 
            v-if="inscripcion.ticketQR"
            @click="descargarQR(inscripcion.ticketQR, inscripcion.congreso.nombre)"
            class="btn btn-primary btn-sm"
          >
            📲 Descargar QR
          </button>

          <button 
            v-if="inscripcion.estado === 'CONFIRMADA'"
            @click="cancelarInscripcion(inscripcion.idInscripcion)"
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
    const response = await http.get('/inscripciones/mis-congresos')
    inscripciones.value = response.data
  } catch (error) {
    console.error('Error al cargar inscripciones:', error)
  } finally {
    loading.value = false
  }
}

const cancelarInscripcion = async (idInscripcion) => {
  if (!confirm('¿Estás seguro de que deseas cancelar esta inscripción?')) {
    return
  }

  try {
    await http.delete(`/inscripciones/congreso/${idInscripcion}`)
    alert('Inscripción cancelada correctamente')
    cargarInscripciones()
  } catch (error) {
    alert('Error al cancelar inscripción: ' + (error.response?.data?.message || 'Error desconocido'))
  }
}

const descargarQR = (qrData, nombreCongreso) => {
  // Implementación simplificada para descargar QR
  const element = document.createElement('a')
  element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(qrData))
  element.setAttribute('download', `QR_${nombreCongreso}.txt`)
  element.style.display = 'none'
  document.body.appendChild(element)
  element.click()
  document.body.removeChild(element)
}

const getBadgeClass = (estado) => {
  return {
    'badge-confirmada': estado === 'CONFIRMADA',
    'badge-pendiente': estado === 'PENDIENTE',
    'badge-cancelada': estado === 'CANCELADA'
  }
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric' })
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

.inscripcion-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

.inscripcion-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 1rem;
}

.inscripcion-header h3 {
  color: #2d3748;
  font-size: 1.3rem;
  flex: 1;
}

.badge {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 600;
}

.badge-confirmada {
  background: #c6f6d5;
  color: #2f855a;
}

.badge-pendiente {
  background: #feebc8;
  color: #c05621;
}

.badge-cancelada {
  background: #fed7d7;
  color: #c53030;
}

.inscripcion-info {
  margin-bottom: 1.5rem;
  color: #4a5568;
}

.inscripcion-info p {
  margin-bottom: 0.5rem;
}

.inscripcion-actions {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
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
