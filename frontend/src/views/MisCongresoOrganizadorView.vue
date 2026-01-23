<template>
  <div class="container">
    <div class="header">
      <h1>Mis Congresos</h1>
      <button @click="crearCongreso" class="btn btn-primary">
        + Crear Nuevo Congreso
      </button>
    </div>

    <div v-if="loading" class="loading">
      Cargando congresos...
    </div>

    <div v-else-if="error" class="error-message">
      {{ error }}
    </div>

    <div v-else-if="congresos.length === 0" class="empty-state">
      <p>Aún no has creado ningún congreso.</p>
      <button @click="crearCongreso" class="btn btn-primary">
        Crear Mi Primer Congreso
      </button>
    </div>

    <div v-else class="congresos-grid">
      <div v-for="congreso in congresos" :key="congreso.idCongreso" class="congreso-card">
        <div v-if="congreso.imagenPortada" class="card-image">
          <img :src="congreso.imagenPortada" :alt="congreso.nombre" />
        </div>
        <div class="card-content">
          <h3>{{ congreso.nombre }}</h3>
          <div class="congreso-info">
            <p><strong>Fecha:</strong> {{ formatearFecha(congreso.fechaInicio) }} - {{ formatearFecha(congreso.fechaFin) }}</p>
            <p><strong>Lugar:</strong> {{ congreso.lugar }}{{ congreso.ciudad ? ', ' + congreso.ciudad : '' }}</p>
            <p v-if="congreso.tematica"><strong>Temática:</strong> {{ congreso.tematica }}</p>
            <p><strong>Precio:</strong> {{ congreso.esPago ? '$' + congreso.precio : 'Gratuito' }}</p>
            <p><strong>Estado:</strong> 
              <span :class="getEstadoClass(congreso)">{{ getEstadoTexto(congreso) }}</span>
            </p>
          </div>
          <div class="card-actions">
            <button @click="verPrograma(congreso.idCongreso)" class="btn btn-secondary">
              Gestionar Sesiones
            </button>
            <button @click="editarCongreso(congreso.idCongreso)" class="btn btn-primary">
              Editar
            </button>
            <button 
              @click="eliminarCongreso(congreso.idCongreso, congreso.nombre)" 
              class="btn btn-danger"
              :disabled="!puedeEliminar(congreso)"
              :title="!puedeEliminar(congreso) ? 'No se puede eliminar un congreso que ya inició' : 'Eliminar congreso'"
            >
              Eliminar
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import http from '../services/http'

const router = useRouter()
const loading = ref(false)
const error = ref('')
const congresos = ref([])

onMounted(() => {
  cargarCongresos()
})

const cargarCongresos = async () => {
  try {
    loading.value = true
    error.value = ''
    const response = await http.get('/congresos/mis-congresos')
    congresos.value = response.data
  } catch (err) {
    error.value = err.response?.data?.message || 'Error al cargar los congresos'
    console.error('Error:', err)
  } finally {
    loading.value = false
  }
}

const crearCongreso = () => {
  router.push('/organizador/crear-congreso')
}

const editarCongreso = (id) => {
  router.push(`/organizador/editar-congreso/${id}`)
}

const eliminarCongreso = async (id, nombre) => {
  if (!confirm(`¿Estás seguro de que deseas eliminar el congreso "${nombre}"?\n\nEsta acción no se puede deshacer.`)) {
    return
  }

  try {
    await http.delete(`/congresos/${id}/eliminar`)
    // Recargar la lista de congresos
    cargarCongresos()
  } catch (err) {
    alert(err.response?.data?.message || 'Error al eliminar el congreso')
    console.error('Error:', err)
  }
}

const puedeEliminar = (congreso) => {
  const hoy = new Date()
  const inicio = new Date(congreso.fechaInicio)
  return inicio > hoy
}

const verPrograma = (id) => {
  router.push(`/organizador/congreso/${id}/sesiones`)
}

const verEstadisticas = (id) => {
  router.push(`/organizador/estadisticas/${id}`)
}

const formatearFecha = (fecha) => {
  return new Date(fecha).toLocaleDateString('es-MX', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const getEstadoTexto = (congreso) => {
  const hoy = new Date()
  const inicio = new Date(congreso.fechaInicio)
  const fin = new Date(congreso.fechaFin)

  if (hoy > fin) return 'Finalizado'
  if (hoy >= inicio && hoy <= fin) return 'En Curso'
  return 'Próximamente'
}

const getEstadoClass = (congreso) => {
  const hoy = new Date()
  const inicio = new Date(congreso.fechaInicio)
  const fin = new Date(congreso.fechaFin)

  if (hoy > fin) return 'estado-finalizado'
  if (hoy >= inicio && hoy <= fin) return 'estado-encurso'
  return 'estado-proximo'
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
  align-items: center;
  margin-bottom: 2rem;
}

h1 {
  margin: 0;
  color: #2c3e50;
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

.congresos-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
}

.congreso-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
}

.congreso-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.card-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f5f5f5;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-content {
  padding: 1.5rem;
}

.card-content h3 {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 1.25rem;
}

.congreso-info {
  margin-bottom: 1.5rem;
}

.congreso-info p {
  margin: 0.5rem 0;
  color: #555;
  font-size: 0.95rem;
}

.estado-finalizado {
  color: #95a5a6;
  font-weight: 600;
}

.estado-encurso {
  color: #27ae60;
  font-weight: 600;
}

.estado-proximo {
  color: #3498db;
  font-weight: 600;
}

.card-actions {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s;
  flex: 1;
  min-width: 100px;
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

.btn-info {
  background-color: #16a085;
  color: white;
}

.btn-info:hover {
  background-color: #138d75;
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

@media (max-width: 768px) {
  .container {
    padding: 1rem;
  }

  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .congresos-grid {
    grid-template-columns: 1fr;
  }

  .card-actions {
    flex-direction: column;
  }

  .btn {
    min-width: auto;
  }
}
</style>
