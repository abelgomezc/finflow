import { create } from 'zustand'
import { persist } from 'zustand/middleware'

interface ThemeState {
  isDark: boolean
  toggleTheme: () => void
  setDark: (dark: boolean) => void
}

export const useThemeStore = create<ThemeState>()(
  persist(
    (set) => ({
      isDark: false,
      toggleTheme: () => set((state) => {
        const newDark = !state.isDark
        if (newDark) {
          document.documentElement.classList.add('dark')
        } else {
          document.documentElement.classList.remove('dark')
        }
        return { isDark: newDark }
      }),
      setDark: (dark: boolean) => set(() => {
        if (dark) {
          document.documentElement.classList.add('dark')
        } else {
          document.documentElement.classList.remove('dark')
        }
        return { isDark: dark }
      }),
    }),
    {
      name: 'finflow-theme',
      onRehydrateStorage: () => (state) => {
        // Apply theme on page load
        if (state?.isDark) {
          document.documentElement.classList.add('dark')
        }
      },
    }
  )
)
