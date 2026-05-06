import api from './api'

export interface Account {
  id: number
  accountNumber: string
  accountType: string
  currency: string
  balance: number
  availableBalance: number
  status: string
  customerId: number
  customerName: string
  customerDocumentType: string
  customerDocumentNumber: string
  createdAt: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export interface CreateAccountRequest {
  customerId: number
  accountType: 'CHECKING' | 'SAVINGS' | 'BUSINESS'
  currency: string
  initialBalance: number
  dailyTransferLimit: number
  perTransferLimit: number
}

export interface CreateAccountResponse {
  id: number
  accountNumber: string
  accountType: string
  currency: string
  balance: number
  availableBalance: number
  status: string
  customerId: number
  customerName: string
}

export const accountService = {
  getById: async (accountId: number): Promise<Account> => {
    const response = await api.get<Account>(`/accounts/${accountId}`)
    return response.data
  },

  getByNumber: async (accountNumber: string): Promise<Account> => {
    console.log(`[accountService] Getting account by number: ${accountNumber}`)
    const response = await api.get<Account>(`/accounts/by-number/${accountNumber}`)
    console.log(`[accountService] Response for ${accountNumber}:`, response.data)
    return response.data
  },

  getByCustomer: async (
    customerId: number,
    page = 0,
    size = 20
  ): Promise<PageResponse<Account>> => {
    const response = await api.get<PageResponse<Account>>(`/accounts/customer/${customerId}`, {
      params: { page, size },
    })
    return response.data
  },

  /**
   * Obtiene las cuentas del usuario autenticado
   */
  getMyAccounts: async (): Promise<Account[]> => {
    console.log(`[accountService] Getting my accounts`)
    const response = await api.get<Account[]>(`/accounts/my-accounts`)
    console.log(`[accountService] My accounts:`, response.data)
    return response.data
  },

  /**
   * Crea una nueva cuenta bancaria
   */
  createAccount: async (data: CreateAccountRequest): Promise<CreateAccountResponse> => {
    console.log(`[accountService] Creating new account:`, data)
    const response = await api.post<CreateAccountResponse>(`/accounts`, data)
    console.log(`[accountService] Account created:`, response.data)
    return response.data
  },
}
