import api from './api'

export interface UserActivity {
  id: number
  activityType: string
  description: string
  entityType?: string
  entityId?: string
  ipAddress?: string
  createdAt: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

// Configuración de tipos de actividad para mostrar en UI
export const activityTypeConfig: Record<string, {
  label: string
  color: string
  icon: string
  description: string
}> = {
  LOGIN: {
    label: 'Inicio de Sesión',
    color: 'bg-blue-100 text-blue-800',
    icon: 'ArrowRightOnRectangleIcon',
    description: 'Acceso al sistema',
  },
  LOGOUT: {
    label: 'Cierre de Sesión',
    color: 'bg-gray-100 text-gray-800',
    icon: 'ArrowLeftOnRectangleIcon',
    description: 'Salida del sistema',
  },
  LOGIN_FAILED: {
    label: 'Login Fallido',
    color: 'bg-red-100 text-red-800',
    icon: 'ExclamationTriangleIcon',
    description: 'Intento de acceso fallido',
  },
  TRANSFER_INITIATED: {
    label: 'Transferencia Iniciada',
    color: 'bg-yellow-100 text-yellow-800',
    icon: 'PaperAirplaneIcon',
    description: 'Nueva transferencia',
  },
  TRANSFER_COMPLETED: {
    label: 'Transferencia Completada',
    color: 'bg-green-100 text-green-800',
    icon: 'CheckCircleIcon',
    description: 'Transferencia exitosa',
  },
  TRANSFER_FAILED: {
    label: 'Transferencia Fallida',
    color: 'bg-red-100 text-red-800',
    icon: 'XCircleIcon',
    description: 'Transferencia falló',
  },
  PROFILE_UPDATED: {
    label: 'Perfil Actualizado',
    color: 'bg-purple-100 text-purple-800',
    icon: 'UserIcon',
    description: 'Cambios en perfil',
  },
  PASSWORD_CHANGED: {
    label: 'Contraseña Cambiada',
    color: 'bg-orange-100 text-orange-800',
    icon: 'KeyIcon',
    description: 'Cambio de contraseña',
  },
  ACCOUNT_VIEWED: {
    label: 'Cuenta Vista',
    color: 'bg-gray-100 text-gray-600',
    icon: 'EyeIcon',
    description: 'Consulta de cuenta',
  },
}

export const activityService = {
  /**
   * Obtiene el historial de actividad del usuario autenticado.
   */
  getMyActivity: async (page = 0, size = 20): Promise<PageResponse<UserActivity>> => {
    const response = await api.get<PageResponse<UserActivity>>('/users/me/activity', {
      params: { page, size },
    })
    return response.data
  },
}
