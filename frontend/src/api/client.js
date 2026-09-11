import axios from 'axios'

const apiClient = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json'
  }
})

// Attach JWT token if available
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('ennas_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, (error) => {
  return Promise.reject(error)
})

// Handle common responses & 401s
apiClient.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response && error.response.status === 401) {
      // If token expired while on admin, redirect
      if (window.location.pathname.startsWith('/admin') && window.location.pathname !== '/admin/login') {
        localStorage.removeItem('ennas_token')
        localStorage.removeItem('ennas_user')
        window.location.href = '/admin/login'
      }
    }
    const message = error.response?.data?.message || error.message || 'Une erreur est survenue'
    return Promise.reject(new Error(message))
  }
)

export default apiClient
