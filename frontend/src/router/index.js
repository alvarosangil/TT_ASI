import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

// Vistas
import CatalogoView from '../views/CatalogoView.vue'
import CongresoDetalleView from '../views/CongresoDetalleView.vue'
import LoginView from '../views/LoginView.vue'
import RegistroView from '../views/RegistroView.vue'
import MisCongresosView from '../views/MisCongresosView.vue'
import MisSesionesView from '../views/MisSesionesView.vue'
import PerfilView from '../views/PerfilView.vue'

const routes = [
  {
    path: '/',
    name: 'catalogo',
    component: CatalogoView,
    meta: { requiresAuth: false }
  },
  {
    path: '/congreso/:id',
    name: 'congreso-detalle',
    component: CongresoDetalleView,
    meta: { requiresAuth: false }
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { requiresAuth: false, guestOnly: true }
  },
  {
    path: '/registro',
    name: 'registro',
    component: RegistroView,
    meta: { requiresAuth: false, guestOnly: true }
  },
  {
    path: '/mis-congresos',
    name: 'mis-congresos',
    component: MisCongresosView,
    meta: { requiresAuth: true, role: 'ASISTENTE' }
  },
  {
    path: '/mis-sesiones',
    name: 'mis-sesiones',
    component: MisSesionesView,
    meta: { requiresAuth: true, role: 'ASISTENTE' }
  },
  {
    path: '/perfil',
    name: 'perfil',
    component: PerfilView,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Guard de navegación
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.meta.guestOnly && authStore.isAuthenticated) {
    next('/')
  } else {
    next()
  }
})

export default router
