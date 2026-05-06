import api from './api'

export interface AdminUser {
  id: number
  username: string
  email: string
  fullName: string
  role: string
  isActive: boolean
  isLocked: boolean
  failedAttempts: number
  lastLoginAt?: string
  lastLoginIp?: string
  profilePhotoUrl?: string
  customerId?: number
  customerName?: string
  documentType?: string
  documentNumber?: string
  createdAt: string
  updatedAt: string
}

export interface SystemStats {
  totalUsers: number
  activeUsers: number
  lockedUsers: number
  adminUsers: number
  totalCustomers: number
  activeCustomers: number
  totalAccounts: number
  activeAccounts: number
  totalBalance: number
}

export interface TransferStats {
  totalTransfers: number
  pendingTransfers: number
  validatingTransfers: number
  processingTransfers: number
  completedTransfers: number
  failedTransfers: number
  cancelledTransfers: number
  reversedTransfers: number
  totalAmount: number
  completedAmount: number
}

export interface AdminAccount {
  id: number
  accountNumber: string
  accountType: string
  balance: number
  availableBalance: number
  currency: string
  status: string
  customerId: number
  customerName: string
  documentNumber: string
  username: string
  createdAt: string
  updatedAt: string
}

export interface BalanceAdjustRequest {
  type: 'DEPOSIT' | 'WITHDRAW'
  amount: number
  description: string
}

export interface BalanceHistoryEntry {
  id: number
  accountId: number
  transactionType: string
  amount: number
  balanceBefore: number
  balanceAfter: number
  description: string
  referenceId: string
  createdAt: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export const adminService = {
  // ==================== USUARIOS ====================

  getUsers: async (page = 0, size = 20): Promise<PageResponse<AdminUser>> => {
    const response = await api.get<PageResponse<AdminUser>>('/admin/users', {
      params: { page, size },
    })
    return response.data
  },

  getUser: async (userId: number): Promise<AdminUser> => {
    const response = await api.get<AdminUser>(`/admin/users/${userId}`)
    return response.data
  },

  lockUser: async (userId: number): Promise<AdminUser> => {
    const response = await api.put<AdminUser>(`/admin/users/${userId}/lock`)
    return response.data
  },

  unlockUser: async (userId: number): Promise<AdminUser> => {
    const response = await api.put<AdminUser>(`/admin/users/${userId}/unlock`)
    return response.data
  },

  activateUser: async (userId: number): Promise<AdminUser> => {
    const response = await api.put<AdminUser>(`/admin/users/${userId}/activate`)
    return response.data
  },

  deactivateUser: async (userId: number): Promise<AdminUser> => {
    const response = await api.put<AdminUser>(`/admin/users/${userId}/deactivate`)
    return response.data
  },

  changeUserRole: async (userId: number, newRole: string): Promise<AdminUser> => {
    const response = await api.put<AdminUser>(`/admin/users/${userId}/role`, null, {
      params: { newRole },
    })
    return response.data
  },

  // ==================== ESTADISTICAS ====================

  getSystemStats: async (): Promise<SystemStats> => {
    const response = await api.get<SystemStats>('/admin/stats')
    return response.data
  },

  getTransferStats: async (): Promise<TransferStats> => {
    const response = await api.get<TransferStats>('/admin/transfers/stats')
    return response.data
  },

  // ==================== TRANSFERENCIAS ADMIN ====================

  getAllTransfers: async (page = 0, size = 20, status?: string): Promise<PageResponse<any>> => {
    const response = await api.get<PageResponse<any>>('/admin/transfers', {
      params: { page, size, status },
    })
    return response.data
  },

  approveTransfer: async (transferId: number): Promise<any> => {
    const response = await api.put(`/admin/transfers/${transferId}/approve`)
    return response.data
  },

  rejectTransfer: async (transferId: number, reason: string): Promise<any> => {
    const response = await api.put(`/admin/transfers/${transferId}/reject`, null, {
      params: { reason },
    })
    return response.data
  },

  reverseTransfer: async (transferId: number, reason: string): Promise<any> => {
    const response = await api.put(`/admin/transfers/${transferId}/reverse`, null, {
      params: { reason },
    })
    return response.data
  },

  // ==================== CUENTAS ADMIN ====================

  getAllAccounts: async (page = 0, size = 20, search?: string): Promise<PageResponse<AdminAccount>> => {
    const response = await api.get<PageResponse<AdminAccount>>('/admin/accounts', {
      params: { page, size, search },
    })
    return response.data
  },

  getAccount: async (accountId: number): Promise<AdminAccount> => {
    const response = await api.get<AdminAccount>(`/admin/accounts/${accountId}`)
    return response.data
  },

  adjustAccountBalance: async (accountId: number, request: BalanceAdjustRequest): Promise<AdminAccount> => {
    const response = await api.post<AdminAccount>(`/admin/accounts/${accountId}/adjust`, request)
    return response.data
  },

  getAccountHistory: async (accountId: number, page = 0, size = 20): Promise<PageResponse<BalanceHistoryEntry>> => {
    const response = await api.get<PageResponse<BalanceHistoryEntry>>(`/admin/accounts/${accountId}/history`, {
      params: { page, size },
    })
    return response.data
  },
}
