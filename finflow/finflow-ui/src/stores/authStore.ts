import { create } from 'zustand'
import { persist } from 'zustand/middleware'

interface User {
  userId: string
  username: string
  fullName: string
  roles: string[]
  profilePhotoUrl?: string
}

interface AuthState {
  token: string | null
  user: User | null
  isAuthenticated: boolean
  login: (token: string, user: User) => void
  logout: () => void
  updateProfilePhoto: (url: string) => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      user: null,
      isAuthenticated: false,

      login: (token: string, user: User) => {
        set({
          token,
          user,
          isAuthenticated: true,
        })
      },

      logout: () => {
        set({
          token: null,
          user: null,
          isAuthenticated: false,
        })
      },

      updateProfilePhoto: (url: string) => {
        set((state) => ({
          user: state.user ? { ...state.user, profilePhotoUrl: url } : null,
        }))
      },
    }),
    {
      name: 'finflow-auth',
    }
  )
)
