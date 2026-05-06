import api from './api'

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  email: string
  fullName: string
}

export interface AuthResponse {
  success: boolean
  token: string
  userId: string
  username: string
  fullName?: string
  roles: string[]
  message: string
  profilePhotoUrl?: string
}

export const authService = {
  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/login', data)
    return response.data
  },

  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/register', data)
    return response.data
  },

  validate: async (): Promise<AuthResponse> => {
    const response = await api.get<AuthResponse>('/auth/validate')
    return response.data
  },

  refresh: async (): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/refresh')
    return response.data
  },
}
