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
      const { token: authToken, usuario } = response.data
      
      token.value = authToken
      user.value = usuario
      
      localStorage.setItem('token', authToken)
      localStorage.setItem('user', JSON.stringify(usuario))
      
      return { success: true }
    } catch (error) {
      return { 
        success: false, 
        message: error.response?.data?.message || 'Error al iniciar sesión' 
      }
    }
  }

  const registro = async (userData) => {
    try {
      const response = await http.post('/auth/registro', userData)
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
