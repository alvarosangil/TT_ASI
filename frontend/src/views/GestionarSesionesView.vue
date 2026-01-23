<template>
  <div class="container">
    <div class="header">
      <div>
        <h1>Gestión de Sesiones</h1>
        <p class="congreso-nombre">{{ congreso?.nombre }}</p>
      </div>
      <button @click="crearSesion" class="btn btn-primary">
        + Nueva Sesión
      </button>
    </div>

    <div v-if="loading" class="loading">
      Cargando sesiones...
    </div>

    <div v-else-if="error" class="error-message">
      {{ error }}
    </div>

    <div v-else-if="sesiones.length === 0" class="empty-state">
      <p>Este congreso aún no tiene sesiones programadas.</p>
      <button @click="crearSesion" class="btn btn-primary">
        Crear Primera Sesión
      </button>
    </div>

    <div v-else class="sesiones-list">
      <div v-for="sesion in sesionesPorFecha" :key="sesion.fecha" class="dia-grupo">
        <h3 class="fecha-titulo">{{ sesion.fecha }}</h3>
        
        <div class="sesion-card" v-for="s in sesion.sesiones" :key="s.idSesion">
          <div class="sesion-header">
            <div>
              <h4>{{ s.titulo }}</h4>
              <p class="sesion-hora">
                {{ formatearHora(s.fechaHoraInicio) }} - {{ formatearHora(s.fechaHoraFin) }}
              </p>
            </div>
            <div class="sesion-aforo">
              <span class="aforo-badge" :class="getAforoClass(s)">
                {{ s.aforoActual }} / {{ s.aforoMaximo }}
              </span>
            </div>
          </div>

          <div class="sesion-info">
            <p v-if="s.ponente"><strong>Ponente:</strong> {{ s.ponente }}</p>
            <p v-if="s.sala"><strong>Sala:</strong> {{ s.sala }}</p>
            <p v-if="s.descripcion" class="descripcion">{{ s.descripcion }}</p>
          </div>

          <div class="sesion-actions">
            <button @click="editarSesion(s.idSesion)" class="btn btn-primary btn-sm">
              Editar
            </button>
            <button 
              @click="eliminarSesion(s.idSesion, s.titulo, s.aforoActual)" 
              class="btn btn-danger btn-sm"
              :disabled="!puedeEliminar(s)"
              :title="!puedeEliminar(s) ? getMensajeEliminar(s) : 'Eliminar sesión'"
            >
              Eliminar
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="footer-actions">
      <button @click="volver" class="btn btn-secondary">
        ← Volver a Mis Congresos
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import http from '../services/http'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const error = ref('')
const congreso = ref(null)
const sesiones = ref([])

onMounted(() => {
  cargarCongreso()
  cargarSesiones()
})

const cargarCongreso = async () => {
  try {
    const response = await http.get(`/congresos/${route.params.idCongreso}/detalle`)
    congreso.value = response.data
  } catch (err) {
    error.value = 'Error al cargar el congreso'
    console.error('Error:', err)
  }
}

const cargarSesiones = async () => {
  try {
    loading.value = true
    error.value = ''
    const response = await http.get(`/sesiones/congreso/${route.params.idCongreso}`)
    sesiones.value = response.data
  } catch (err) {
    error.value = err.response?.data?.message || 'Error al cargar las sesiones'
    console.error('Error:', err)
  } finally {
    loading.value = false
  }
}

const sesionesPorFecha = computed(() => {
  const grupos = {}
  
  sesiones.value.forEach(sesion => {
    const fecha = new Date(sesion.fechaHoraInicio).toLocaleDateString('es-MX', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    })
    
    if (!grupos[fecha]) {
      grupos[fecha] = []
    }
    grupos[fecha].push(sesion)
  })

  // Convertir a array y ordenar por fecha
  return Object.entries(grupos)
    .map(([fecha, sesiones]) => ({ fecha, sesiones }))
    .sort((a, b) => {
      const fechaA = new Date(a.sesiones[0].fechaHoraInicio)
      const fechaB = new Date(b.sesiones[0].fechaHoraInicio)
      return fechaA - fechaB
    })
})

const crearSesion = () => {
  router.push(`/organizador/congreso/${route.params.idCongreso}/sesion/nueva`)
}

const editarSesion = (idSesion) => {
  router.push(`/organizador/congreso/${route.params.idCongreso}/sesion/${idSesion}/editar`)
}

const eliminarSesion = async (idSesion, titulo, aforoActual) => {
  if (aforoActual > 0) {
    alert(`No se puede eliminar la sesión "${titulo}" porque tiene ${aforoActual} inscripciones.`)
    return
  }

  if (!confirm(`¿Estás seguro de que deseas eliminar la sesión "${titulo}"?\n\nEsta acción no se puede deshacer.`)) {
    return
  }

  try {
    await http.delete(`/sesiones/${idSesion}/eliminar`)
    cargarSesiones() // Recargar lista
  } catch (err) {
    alert(err.response?.data?.message || 'Error al eliminar la sesión')
    console.error('Error:', err)
  }
}

const puedeEliminar = (sesion) => {
  const ahora = new Date()
  const inicio = new Date(sesion.fechaHoraInicio)
  return inicio > ahora && sesion.aforoActual === 0
}

const getMensajeEliminar = (sesion) => {
  if (sesion.aforoActual > 0) {
    return 'No se puede eliminar una sesión con inscripciones'
  }
  const ahora = new Date()
  const inicio = new Date(sesion.fechaHoraInicio)
  if (inicio <= ahora) {
    return 'No se puede eliminar una sesión que ya inició'
  }
  return 'Eliminar sesión'
}

const getAforoClass = (sesion) => {
  const porcentaje = (sesion.aforoActual / sesion.aforoMaximo) * 100
  if (porcentaje >= 90) return 'aforo-lleno'
  if (porcentaje >= 70) return 'aforo-alto'
  return 'aforo-normal'
}

const volver = () => {
  router.push('/organizador/mis-congresos')
}

const formatearHora = (fechaHora) => {
  return new Date(fechaHora).toLocaleTimeString('es-MX', {
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 2rem;
}

h1 {
  margin: 0;
  color: #2c3e50;
}

.congreso-nombre {
  color: #7f8c8d;
  margin: 0.5rem 0 0 0;
  font-size: 1.1rem;
}

.loading {
  text-align: center;
  padding: 3rem;
  color: #7f8c8d;
}

.error-message {
  background-color: #fee;
  color: #c33;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.empty-state {
  text-align: center;
  padding: 3rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.empty-state p {
  color: #7f8c8d;
  margin-bottom: 1.5rem;
  font-size: 1.1rem;
}

.sesiones-list {
  margin-bottom: 2rem;
}

.dia-grupo {
  margin-bottom: 2rem;
}

.fecha-titulo {
  color: #34495e;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #3498db;
  text-transform: capitalize;
}

.sesion-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
  margin-bottom: 1rem;
  transition: transform 0.3s, box-shadow 0.3s;
}

.sesion-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.sesion-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.sesion-header h4 {
  margin: 0 0 0.5rem 0;
  color: #2c3e50;
  font-size: 1.2rem;
}

.sesion-hora {
  color: #7f8c8d;
  margin: 0;
  font-size: 0.95rem;
}

.aforo-badge {
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-weight: 600;
  font-size: 0.9rem;
}

.aforo-normal {
  background-color: #d5f4e6;
  color: #27ae60;
}

.aforo-alto {
  background-color: #fff3cd;
  color: #f39c12;
}

.aforo-lleno {
  background-color: #f8d7da;
  color: #e74c3c;
}

.sesion-info {
  margin-bottom: 1rem;
}

.sesion-info p {
  margin: 0.5rem 0;
  color: #555;
}

.descripcion {
  color: #7f8c8d;
  font-style: italic;
}

.sesion-actions {
  display: flex;
  gap: 0.5rem;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-sm {
  padding: 0.5rem 1rem;
  font-size: 0.9rem;
}

.btn-primary {
  background-color: #3498db;
  color: white;
}

.btn-primary:hover {
  background-color: #2980b9;
}

.btn-secondary {
  background-color: #95a5a6;
  color: white;
}

.btn-secondary:hover {
  background-color: #7f8c8d;
}

.btn-danger {
  background-color: #e74c3c;
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background-color: #c0392b;
}

.btn-danger:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
  opacity: 0.6;
}

.footer-actions {
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 1px solid #ecf0f1;
}
</style>
