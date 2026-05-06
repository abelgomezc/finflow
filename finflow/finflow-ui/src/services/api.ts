import axios from 'axios'
import { useAuthStore } from '../stores/authStore'

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  timeout: 30000, // 30 segundos timeout
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor para agregar token
api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token
  console.log('[API] Request:', config.method?.toUpperCase(), config.url)
  console.log('[API] Token present:', !!token)

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  // Agregar correlation ID
  config.headers['X-Correlation-ID'] = crypto.randomUUID()
  return config
})

// Response interceptor para manejar errores
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      useAuthStore.getState().logout()
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api
