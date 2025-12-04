<template>
  <div class="container">
    <div class="card">
      <h2>{{ esEdicion ? 'Editar Sesión' : 'Nueva Sesión' }}</h2>
      <p class="subtitle">{{ congreso?.nombre }}</p>

      <form @submit.prevent="guardarSesion">
        <!-- Título de la Sesión -->
        <div class="form-group">
          <label for="titulo">Título de la Sesión *</label>
          <input
            type="text"
            id="titulo"
            v-model="sesion.titulo"
            required
            maxlength="200"
            placeholder="Ej: Inteligencia Artificial en la Medicina Moderna"
          />
        </div>

        <!-- Descripción -->
        <div class="form-group">
          <label for="descripcion">Descripción</label>
          <textarea
            id="descripcion"
            v-model="sesion.descripcion"
            rows="4"
            maxlength="2000"
            placeholder="Describe el contenido de la sesión..."
          ></textarea>
        </div>

        <!-- Ponente y Sala (en línea) -->
        <div class="form-row">
          <div class="form-group">
            <label for="ponente">Ponente</label>
            <input
              type="text"
              id="ponente"
              v-model="sesion.ponente"
              maxlength="200"
              placeholder="Nombre del ponente"
            />
          </div>
          <div class="form-group">
            <label for="sala">Sala</label>
            <input
              type="text"
              id="sala"
              v-model="sesion.sala"
              maxlength="100"
              placeholder="Ej: Auditorio Principal"
            />
          </div>
        </div>

        <!-- Fechas y Horas (en línea) -->
        <div class="form-row">
          <div class="form-group">
            <label for="fechaInicio">Fecha y Hora de Inicio *</label>
            <input
              type="datetime-local"
              id="fechaInicio"
              v-model="sesion.fechaHoraInicio"
              required
              :min="fechaMinima"
              :max="fechaMaxima"
            />
          </div>
          <div class="form-group">
            <label for="fechaFin">Fecha y Hora de Fin *</label>
            <input
              type="datetime-local"
              id="fechaFin"
              v-model="sesion.fechaHoraFin"
              required
              :min="fechaMinima"
              :max="fechaMaxima"
            />
          </div>
        </div>

        <!-- Aforo Máximo -->
        <div class="form-group">
          <label for="aforoMaximo">Aforo Máximo *</label>
          <input
            type="number"
            id="aforoMaximo"
            v-model.number="sesion.aforoMaximo"
            required
            min="1"
            max="10000"
            placeholder="Ej: 100"
          />
          <span class="help-text">Capacidad máxima de asistentes para esta sesión</span>
        </div>

        <!-- Mensajes de error -->
        <div v-if="error" class="error-message">
          {{ error }}
        </div>

        <!-- Mensajes de éxito -->
        <div v-if="mensaje" class="success-message">
          {{ mensaje }}
        </div>

        <!-- Botones de acción -->
        <div class="form-actions">
          <button type="button" @click="cancelar" class="btn btn-secondary">
            Cancelar
          </button>
          <button type="submit" :disabled="loading" class="btn btn-primary">
            {{ loading ? 'Guardando...' : (esEdicion ? 'Actualizar Sesión' : 'Crear Sesión') }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import http from '../services/http'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const error = ref('')
const mensaje = ref('')
const esEdicion = ref(false)
const congreso = ref(null)

// Datos del formulario
const sesion = ref({
  idCongreso: null,
  titulo: '',
  descripcion: '',
  ponente: '',
  sala: '',
  fechaHoraInicio: '',
  fechaHoraFin: '',
  aforoMaximo: 50
})

// Fechas mínimas y máximas
const fechaMinima = ref('')
const fechaMaxima = ref('')

onMounted(async () => {
  const idCongreso = route.params.idCongreso
  sesion.value.idCongreso = parseInt(idCongreso)

  // Cargar datos del congreso
  await cargarCongreso(idCongreso)

  // Si hay ID de sesión en la ruta, es edición
  if (route.params.idSesion) {
    esEdicion.value = true
    await cargarSesion()
  }
})

const cargarCongreso = async (id) => {
  try {
    const response = await http.get(`/congresos/${id}/detalle`)
    congreso.value = response.data

    // Configurar límites de fecha basados en el congreso
    const inicio = new Date(congreso.value.fechaInicio)
    const fin = new Date(congreso.value.fechaFin)
    
    fechaMinima.value = inicio.toISOString().slice(0, 16)
    
    // Agregar un día completo al final
    fin.setHours(23, 59)
    fechaMaxima.value = fin.toISOString().slice(0, 16)
  } catch (err) {
    error.value = 'Error al cargar el congreso'
    console.error('Error:', err)
  }
}

const cargarSesion = async () => {
  try {
    loading.value = true
    error.value = ''

    const response = await http.get(`/sesiones/${route.params.idSesion}`)
    const data = response.data

    sesion.value = {
      idCongreso: sesion.value.idCongreso,
      titulo: data.titulo,
      descripcion: data.descripcion,
      ponente: data.ponente,
      sala: data.sala,
      fechaHoraInicio: formatearFechaHoraParaInput(data.fechaHoraInicio),
      fechaHoraFin: formatearFechaHoraParaInput(data.fechaHoraFin),
      aforoMaximo: data.aforoMaximo
    }
  } catch (err) {
    error.value = err.response?.data?.message || 'Error al cargar la sesión'
    console.error('Error:', err)
  } finally {
    loading.value = false
  }
}

const guardarSesion = async () => {
  try {
    loading.value = true
    error.value = ''
    mensaje.value = ''

    // Validaciones adicionales
    const inicio = new Date(sesion.value.fechaHoraInicio)
    const fin = new Date(sesion.value.fechaHoraFin)

    if (inicio >= fin) {
      error.value = 'La hora de inicio debe ser anterior a la hora de fin'
      loading.value = false
      return
    }

    if (esEdicion.value) {
      // Actualizar sesión existente
      await http.put(`/sesiones/${route.params.idSesion}/editar`, sesion.value)
      mensaje.value = 'Sesión actualizada exitosamente'
    } else {
      // Crear nueva sesión
      await http.post('/sesiones/crear', sesion.value)
      mensaje.value = 'Sesión creada exitosamente'
    }

    // Redirigir después de 1 segundo
    setTimeout(() => {
      router.push(`/organizador/congreso/${sesion.value.idCongreso}/sesiones`)
    }, 1000)

  } catch (err) {
    error.value = err.response?.data?.message || 'Error al guardar la sesión'
    console.error('Error:', err)
  } finally {
    loading.value = false
  }
}

const cancelar = () => {
  router.push(`/organizador/congreso/${sesion.value.idCongreso}/sesiones`)
}

const formatearFechaHoraParaInput = (fechaHora) => {
  // Convertir "2025-01-15T10:00:00" a formato compatible con datetime-local
  if (!fechaHora) return ''
  return fechaHora.slice(0, 16)
}
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}

.card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 2rem;
}

h2 {
  margin: 0 0 0.5rem 0;
  color: #2c3e50;
}

.subtitle {
  color: #7f8c8d;
  margin-bottom: 2rem;
  font-size: 1.1rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #34495e;
}

input,
textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

input:focus,
textarea:focus {
  outline: none;
  border-color: #3498db;
}

textarea {
  resize: vertical;
  min-height: 100px;
  font-family: inherit;
}

.help-text {
  display: block;
  margin-top: 0.25rem;
  font-size: 0.875rem;
  color: #7f8c8d;
}

.error-message {
  background-color: #fee;
  color: #c33;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.success-message {
  background-color: #efe;
  color: #2a7;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  flex: 1;
}

.btn-primary {
  background-color: #3498db;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #2980b9;
}

.btn-primary:disabled {
  background-color: #bdc3c7;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #95a5a6;
  color: white;
}

.btn-secondary:hover {
  background-color: #7f8c8d;
}
</style>
