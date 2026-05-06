import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import { Account } from '../services/accountService'

interface AccountState {
  // Lista de cuentas del usuario
  accounts: Account[]
  // Cuenta actualmente seleccionada
  selectedAccount: Account | null
  // Estado de carga
  isLoading: boolean

  // Acciones
  setAccounts: (accounts: Account[]) => void
  selectAccount: (account: Account) => void
  clearSelection: () => void
  reset: () => void
}

export const useAccountStore = create<AccountState>()(
  persist(
    (set) => ({
      accounts: [],
      selectedAccount: null,
      isLoading: false,

      setAccounts: (accounts: Account[]) => {
        set((state) => {
          // Si hay cuenta seleccionada, actualizar sus datos
          const updatedSelected = state.selectedAccount
            ? accounts.find(a => a.id === state.selectedAccount?.id) || null
            : null

          return {
            accounts,
            selectedAccount: updatedSelected,
          }
        })
      },

      selectAccount: (account: Account) => {
        set({ selectedAccount: account })
      },

      clearSelection: () => {
        set({ selectedAccount: null })
      },

      reset: () => {
        set({
          accounts: [],
          selectedAccount: null,
          isLoading: false,
        })
      },
    }),
    {
      name: 'finflow-account',
      partialize: (state) => ({
        // Solo persistir el ID de la cuenta seleccionada, no todos los datos
        selectedAccountId: state.selectedAccount?.id,
      }),
    }
  )
)
