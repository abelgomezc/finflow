import api from './api'

export interface Transfer {
  id: number
  referenceNumber: string
  sourceAccountId: number
  sourceAccountNumber?: string
  targetAccountId: number
  targetAccountNumber?: string
  amount: number
  currency: string
  status: string
  description?: string
  initiatedBy: string
  initiatedAt: string
  completedAt?: string
  failedAt?: string
  failureReason?: string
  failureMessage?: string
  // Saldos después de la transferencia
  sourceBalanceAfter?: number
  targetBalanceAfter?: number
}

export interface InitiateTransferRequest {
  sourceAccountId: number
  targetAccountId: number
  amount: number
  currency: string
  description?: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export const transferService = {
  initiate: async (data: InitiateTransferRequest): Promise<Transfer> => {
    const response = await api.post<Transfer>('/transfers', data)
    return response.data
  },

  getById: async (transferId: number): Promise<Transfer> => {
    const response = await api.get<Transfer>(`/transfers/${transferId}`)
    return response.data
  },

  getByReference: async (referenceNumber: string): Promise<Transfer> => {
    const response = await api.get<Transfer>(`/transfers/reference/${referenceNumber}`)
    return response.data
  },

  getHistory: async (userId: string, page = 0, size = 20): Promise<PageResponse<Transfer>> => {
    const response = await api.get<PageResponse<Transfer>>('/transfers/history', {
      params: { userId, page, size },
    })
    return response.data
  },

  getAccountTransfers: async (
    accountId: number,
    page = 0,
    size = 20
  ): Promise<PageResponse<Transfer>> => {
    const response = await api.get<PageResponse<Transfer>>(`/transfers/account/${accountId}`, {
      params: { page, size },
    })
    return response.data
  },

  cancel: async (transferId: number, reason: string): Promise<Transfer> => {
    const response = await api.post<Transfer>(`/transfers/${transferId}/cancel`, null, {
      params: { reason },
    })
    return response.data
  },

  reverse: async (transferId: number, reason: string): Promise<Transfer> => {
    const response = await api.post<Transfer>(`/transfers/${transferId}/reverse`, null, {
      params: { reason },
    })
    return response.data
  },

  /**
   * Obtiene las transferencias del usuario autenticado.
   */
  getMyTransfers: async (page = 0, size = 20): Promise<PageResponse<Transfer>> => {
    const response = await api.get<PageResponse<Transfer>>('/transfers/my-transfers', {
      params: { page, size },
    })
    return response.data
  },
}
