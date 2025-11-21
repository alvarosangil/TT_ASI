<template>
  <div class="catalogo-view">
    <h1 class="page-title">📚 Catálogo de Congresos</h1>
    
    <!-- Filtros de búsqueda (CU12) -->
    <div class="card filtros">
      <div class="filtros-grid">
        <div class="form-group">
          <input 
            v-model="filtros.busqueda" 
            type="text" 
            placeholder="Buscar por nombre..."
            @input="buscarCongresos"
          />
        </div>
        <div class="form-group">
          <input 
            v-model="filtros.ciudad" 
            type="text" 
            placeholder="Ciudad..."
            @input="buscarCongresos"
          />
        </div>
        <div class="form-group">
          <input 
            v-model="filtros.tematica" 
            type="text" 
            placeholder="Temática..."
            @input="buscarCongresos"
          />
        </div>
      </div>
    </div>

    <!-- Lista de congresos -->
    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>Cargando congresos...</p>
    </div>

    <div v-else-if="congresos.length === 0" class="empty-state">
      <p>No se encontraron congresos</p>
    </div>

    <div v-else class="grid grid-2">
      <div 
        v-for="congreso in congresos" 
        :key="congreso.idCongreso"
        class="congreso-card"
      >
        <div class="congreso-imagen">
          <img 
            :src="congreso.imagenPortada || '/placeholder-congreso.jpg'" 
            :alt="congreso.nombre"
          />
        </div>
        <div class="congreso-info">
          <h3>{{ congreso.nombre }}</h3>
          <p class="congreso-fecha">
            📅 {{ formatDate(congreso.fechaInicio) }} - {{ formatDate(congreso.fechaFin) }}
          </p>
          <p class="congreso-lugar">📍 {{ congreso.lugar }}, {{ congreso.ciudad }}</p>
          <p class="congreso-tematica" v-if="congreso.tematica">
            🏷️ {{ congreso.tematica }}
          </p>
          <p class="congreso-precio">
            <strong>{{ congreso.esPago ? `${congreso.precio}€` : 'Gratuito' }}</strong>
          </p>
          <router-link 
            :to="`/congreso/${congreso.idCongreso}`" 
            class="btn btn-primary"
          >
            Ver Detalle
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import http from '../services/http'

const congresos = ref([])
const loading = ref(false)
const filtros = ref({
  busqueda: '',
  ciudad: '',
  tematica: ''
})

const cargarCongresos = async () => {
  loading.value = true
  try {
    const response = await http.get('/congresos/buscar', { params: filtros.value })
    congresos.value = response.data
  } catch (error) {
    console.error('Error al cargar congresos:', error)
  } finally {
    loading.value = false
  }
}

const buscarCongresos = () => {
  cargarCongresos()
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

onMounted(() => {
  cargarCongresos()
})
</script>

<style scoped>
.page-title {
  font-size: 2.5rem;
  margin-bottom: 2rem;
  color: #2d3748;
}

.filtros {
  margin-bottom: 2rem;
}

.filtros-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
}

.congreso-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.congreso-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.15);
}

.congreso-imagen {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.congreso-imagen img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.congreso-info {
  padding: 1.5rem;
}

.congreso-info h3 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: #2d3748;
}

.congreso-fecha, .congreso-lugar, .congreso-tematica {
  margin-bottom: 0.5rem;
  color: #4a5568;
}

.congreso-precio {
  font-size: 1.2rem;
  margin: 1rem 0;
  color: #667eea;
}

.empty-state {
  text-align: center;
  padding: 4rem;
  color: #718096;
  font-size: 1.2rem;
}
</style>
