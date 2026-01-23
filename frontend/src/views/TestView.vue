<template>
  <div style="padding: 2rem; background: white; margin: 2rem; border-radius: 8px;">
    <h1>Página de Prueba</h1>
    <div v-if="authStore.isAuthenticated">
      <p><strong>Usuario:</strong> {{ authStore.user?.nombreCompleto }}</p>
      <p><strong>Email:</strong> {{ authStore.user?.email }}</p>
      <p><strong>Tipo Usuario:</strong> {{ authStore.user?.tipoUsuario }}</p>
      <p><strong>Token existe:</strong> {{ !!authStore.token }}</p>
      <hr>
      <p>Usuario completo (JSON):</p>
      <pre>{{ JSON.stringify(authStore.user, null, 2) }}</pre>
    </div>
    <div v-else>
      <p>No autenticado</p>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()
</script>
