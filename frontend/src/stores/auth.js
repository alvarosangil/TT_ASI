import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import http from '../services/http'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const token = ref(null)

  // Cargar datos del localStorage al iniciar
  const loadFromStorage = () => {
    const storedToken = localStorage.getItem('token')
    const storedUser = localStorage.getItem('user')
    
    if (storedToken && storedUser) {
      token.value = storedToken
      user.value = JSON.parse(storedUser)
    }
  }

  // Computed
  const isAuthenticated = computed(() => !!token.value)
  const isAsistente = computed(() => user.value?.tipoUsuario === 'ASISTENTE')
  const isOrganizador = computed(() => user.value?.tipoUsuario === 'ORGANIZADOR')
  const isStaff = computed(() => user.value?.tipoUsuario === 'STAFF')

  // Actions
  const login = async (email, password) => {
    try {
      const response = await http.post('/auth/login', { email, password })
      const data = response.data
      
      // El backend envía los datos directamente en el response
      token.value = data.token
      user.value = {
        idUsuario: data.idUsuario,
        email: data.email,
        nombreCompleto: data.nombreCompleto,
        tipoUsuario: data.tipoUsuario
      }
      
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(user.value))
      
      return { success: true }
    } catch (error) {
      console.error('Error en login:', error)
      return { 
        success: false, 
        message: error.response?.data?.message || 'Error al iniciar sesión' 
      }
    }
  }

  const registro = async (userData) => {
    try {
      const response = await http.post('/auth/registro', userData)
      const data = response.data
      
      // Auto-login después del registro - datos vienen directamente
      token.value = data.token
      user.value = {
        idUsuario: data.idUsuario,
        email: data.email,
        nombreCompleto: data.nombreCompleto,
        tipoUsuario: data.tipoUsuario
      }
      
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(user.value))
      
      return { success: true, data: response.data }
    } catch (error) {
      return { 
        success: false, 
        message: error.response?.data?.message || 'Error al registrarse' 
      }
    }
  }

  const logout = () => {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  // Inicializar
  loadFromStorage()

  return {
    user,
    token,
    isAuthenticated,
    isAsistente,
    isOrganizador,
    isStaff,
    login,
    registro,
    logout
  }
})
