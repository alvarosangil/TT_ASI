<template>
  <div class="container">
    <div class="card">
      <h2>{{ esEdicion ? 'Editar Congreso' : 'Crear Nuevo Congreso' }}</h2>
      <p class="subtitle">{{ esEdicion ? 'Modifica la información del congreso' : 'Completa la información para crear un congreso académico' }}</p>

      <form @submit.prevent="guardarCongreso">
        <!-- Nombre del Congreso -->
        <div class="form-group">
          <label for="nombre">Nombre del Congreso *</label>
          <input
            type="text"
            id="nombre"
            v-model="congreso.nombre"
            required
            maxlength="200"
            placeholder="Ej: Congreso Internacional de Inteligencia Artificial 2025"
          />
          <span class="help-text">Máximo 200 caracteres</span>
        </div>

        <!-- Descripción -->
        <div class="form-group">
          <label for="descripcion">Descripción</label>
          <textarea
            id="descripcion"
            v-model="congreso.descripcion"
            rows="5"
            maxlength="2000"
            placeholder="Describe el propósito, objetivos y características principales del congreso..."
          ></textarea>
          <span class="help-text">Máximo 2000 caracteres</span>
        </div>

        <!-- Fechas (en línea) -->
        <div class="form-row">
          <div class="form-group">
            <label for="fechaInicio">Fecha de Inicio *</label>
            <input
              type="date"
              id="fechaInicio"
              v-model="congreso.fechaInicio"
              required
              :min="fechaMinima"
            />
          </div>
          <div class="form-group">
            <label for="fechaFin">Fecha de Fin *</label>
            <input
              type="date"
              id="fechaFin"
              v-model="congreso.fechaFin"
              required
              :min="congreso.fechaInicio || fechaMinima"
            />
          </div>
        </div>

        <!-- Lugar y Ciudad -->
        <div class="form-row">
          <div class="form-group">
            <label for="lugar">Lugar *</label>
            <input
              type="text"
              id="lugar"
              v-model="congreso.lugar"
              required
              maxlength="200"
              placeholder="Ej: Centro de Convenciones Banamex"
            />
          </div>
          <div class="form-group">
            <label for="ciudad">Ciudad</label>
            <input
              type="text"
              id="ciudad"
              v-model="congreso.ciudad"
              maxlength="100"
              placeholder="Ej: Ciudad de México"
            />
          </div>
        </div>

        <!-- Temática -->
        <div class="form-group">
          <label for="tematica">Temática</label>
          <input
            type="text"
            id="tematica"
            v-model="congreso.tematica"
            maxlength="100"
            placeholder="Ej: Inteligencia Artificial, Ciencia de Datos, Machine Learning"
          />
          <span class="help-text">Categoría o área de conocimiento principal</span>
        </div>

        <!-- URL Imagen -->
        <div class="form-group">
          <label for="imagenPortada">URL de Imagen de Portada</label>
          <input
            type="url"
            id="imagenPortada"
            v-model="congreso.imagenPortada"
            maxlength="255"
            placeholder="https://ejemplo.com/imagen.jpg"
          />
          <span class="help-text">URL de la imagen que se mostrará en el catálogo</span>
        </div>

        <!-- Precio y Tipo de Pago -->
        <div class="form-row">
          <div class="form-group">
            <label>
              <input
                type="checkbox"
                v-model="congreso.esPago"
              />
              ¿Es un congreso de pago?
            </label>
          </div>
          <div class="form-group" v-if="congreso.esPago">
            <label for="precio">Precio (MXN) *</label>
            <input
              type="number"
              id="precio"
              v-model.number="congreso.precio"
              min="0.01"
              step="0.01"
              :required="congreso.esPago"
              placeholder="0.00"
            />
          </div>
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
            {{ loading ? 'Guardando...' : (esEdicion ? 'Actualizar Congreso' : 'Crear Congreso') }}
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

// Datos del formulario
const congreso = ref({
  nombre: '',
  descripcion: '',
  fechaInicio: '',
  fechaFin: '',
  lugar: '',
  ciudad: '',
  tematica: '',
  imagenPortada: '',
  precio: 0,
  esPago: false
})

// Fecha mínima (hoy)
const fechaMinima = ref('')

onMounted(() => {
  const hoy = new Date()
  fechaMinima.value = hoy.toISOString().split('T')[0]
  
  // Si hay ID en la ruta, es edición
  if (route.params.id) {
    esEdicion.value = true
    cargarCongreso()
  }
})

const cargarCongreso = async () => {
  try {
    loading.value = true
    error.value = ''
    
    const response = await http.get(`/congresos/${route.params.id}/detalle`)
    const data = response.data
    
    congreso.value = {
      nombre: data.nombre,
      descripcion: data.descripcion,
      fechaInicio: data.fechaInicio,
      fechaFin: data.fechaFin,
      lugar: data.lugar,
      ciudad: data.ciudad,
      tematica: data.tematica,
      imagenPortada: data.imagenPortada,
      precio: data.precio || 0,
      esPago: data.esPago
    }
  } catch (err) {
    error.value = err.response?.data?.message || 'Error al cargar el congreso'
    console.error('Error cargando congreso:', err)
  } finally {
    loading.value = false
  }
}

const guardarCongreso = async () => {
  try {
    loading.value = true
    error.value = ''
    mensaje.value = ''

    // Validaciones adicionales
    if (new Date(congreso.value.fechaInicio) > new Date(congreso.value.fechaFin)) {
      error.value = 'La fecha de inicio no puede ser posterior a la fecha de fin'
      loading.value = false
      return
    }

    if (congreso.value.esPago && (!congreso.value.precio || congreso.value.precio <= 0)) {
      error.value = 'Debes especificar un precio mayor a 0 para congresos de pago'
      loading.value = false
      return
    }

    // Preparar datos
    const datos = {
      ...congreso.value,
      precio: congreso.value.esPago ? congreso.value.precio : 0
    }

    if (esEdicion.value) {
      // Actualizar congreso existente (CU24)
      await http.put(`/congresos/${route.params.id}/editar`, datos)
      mensaje.value = 'Congreso actualizado exitosamente'
    } else {
      // Crear nuevo congreso (CU23)
      await http.post('/congresos/crear', datos)
      mensaje.value = 'Congreso creado exitosamente'
    }

    // Redirigir después de 1.5 segundos
    setTimeout(() => {
      router.push('/organizador/mis-congresos')
    }, 1500)

  } catch (err) {
    error.value = err.response?.data?.message || 'Error al guardar el congreso'
    console.error('Error guardando congreso:', err)
  } finally {
    loading.value = false
  }
}

const cancelar = () => {
  router.push('/organizador/mis-congresos')
}
</script>

<style scoped>
.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 2rem;
}

.card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

h2 {
  margin: 0 0 0.5rem 0;
  color: #2c3e50;
}

.subtitle {
  color: #7f8c8d;
  margin-bottom: 2rem;
}

.form-group {
  margin-bottom: 1.5rem;
  flex: 1;
}

.form-row {
  display: flex;
  gap: 1rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #2c3e50;
}

input[type="text"],
input[type="url"],
input[type="date"],
input[type="number"],
textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  box-sizing: border-box;
}

input[type="text"]:focus,
input[type="url"]:focus,
input[type="date"]:focus,
input[type="number"]:focus,
textarea:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

input[type="checkbox"] {
  margin-right: 0.5rem;
  cursor: pointer;
}

textarea {
  resize: vertical;
  font-family: inherit;
}

.help-text {
  display: block;
  margin-top: 0.25rem;
  font-size: 0.875rem;
  color: #95a5a6;
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
  color: #3c763d;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid #eee;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background-color: #3498db;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #2980b9;
}

.btn-secondary {
  background-color: #95a5a6;
  color: white;
}

.btn-secondary:hover {
  background-color: #7f8c8d;
}

@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
  }

  .container {
    padding: 1rem;
  }

  .card {
    padding: 1.5rem;
  }
}
</style>
