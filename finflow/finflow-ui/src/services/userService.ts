import api from './api'

export interface UserProfile {
  id: number
  username: string
  email: string
  fullName: string
  profilePhotoUrl: string
  role: string
  customerId?: number
  customerName?: string
  documentType?: string
  documentNumber?: string
}

export const userService = {
  getMyProfile: async (): Promise<UserProfile> => {
    const response = await api.get<UserProfile>('/users/me')
    return response.data
  },

  updateProfile: async (updates: { fullName?: string; email?: string }): Promise<{ success: boolean; message: string }> => {
    const response = await api.put<{ success: boolean; message: string }>('/users/me', updates)
    return response.data
  },

  uploadProfilePhoto: async (file: File): Promise<{ success: boolean; message: string; profilePhotoUrl: string }> => {
    const formData = new FormData()
    formData.append('file', file)

    const response = await api.post<{ success: boolean; message: string; profilePhotoUrl: string }>(
      '/users/me/photo',
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    )
    return response.data
  },
}
