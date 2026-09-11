import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import apiClient from '@/api/client'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('ennas_token') || null)
  const user = ref(JSON.parse(localStorage.getItem('ennas_user') || 'null'))
  const loading = ref(false)
  const error = ref(null)

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ROLE_ADMIN')

  async function login(email, password) {
    loading.value = true
    error.value = null
    try {
      const res = await apiClient.post('/auth/login', { email, password })
      if (res.success && res.data) {
        token.value = res.data.token
        user.value = {
          id: res.data.id,
          email: res.data.email,
          fullName: res.data.fullName,
          role: res.data.role
        }
        localStorage.setItem('ennas_token', res.data.token)
        localStorage.setItem('ennas_user', JSON.stringify(user.value))
        return true
      }
      return false
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('ennas_token')
    localStorage.removeItem('ennas_user')
  }

  return {
    token,
    user,
    loading,
    error,
    isAuthenticated,
    isAdmin,
    login,
    logout
  }
})
