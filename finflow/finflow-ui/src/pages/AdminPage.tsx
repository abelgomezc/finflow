import { useState, useEffect } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import toast from 'react-hot-toast'
import { useAuthStore } from '../stores/authStore'
import { Navigate, useSearchParams } from 'react-router-dom'
import {
  adminService,
  AdminUser,
  SystemStats,
  TransferStats,
  AdminAccount,
  BalanceHistoryEntry,
} from '../services/adminService'
import { Transfer } from '../services/transferService'
import {
  UsersIcon,
  ArrowsRightLeftIcon,
  ChartBarIcon,
  ShieldCheckIcon,
  LockClosedIcon,
  LockOpenIcon,
  CheckCircleIcon,
  XCircleIcon,
  ArrowPathIcon,
  UserCircleIcon,
  BanknotesIcon,
  UserGroupIcon,
  BuildingLibraryIcon,
  CreditCardIcon,
  PlusIcon,
  MinusIcon,
  ClockIcon,
  MagnifyingGlassIcon,
  XMarkIcon,
} from '@heroicons/react/24/outline'

type TabType = 'stats' | 'users' | 'transfers' | 'accounts'

const statusConfig: Record<string, { color: string; label: string }> = {
  COMPLETED: { color: 'bg-green-100 text-green-800', label: 'Completada' },
  FAILED: { color: 'bg-red-100 text-red-800', label: 'Fallida' },
  PENDING: { color: 'bg-yellow-100 text-yellow-800', label: 'Pendiente' },
  PROCESSING: { color: 'bg-blue-100 text-blue-800', label: 'Procesando' },
  VALIDATING: { color: 'bg-purple-100 text-purple-800', label: 'Validando' },
  REVERSED: { color: 'bg-orange-100 text-orange-800', label: 'Reversada' },
  CANCELLED: { color: 'bg-gray-100 text-gray-800', label: 'Cancelada' },
}

function formatAccountNumber(accountNumber?: string): string {
  if (accountNumber && accountNumber.length >= 8) {
    return `${accountNumber.slice(0, 4)}****${accountNumber.slice(-4)}`
  }
  return accountNumber || '---'
}

function formatDate(dateString?: string): string {
  if (!dateString) return '---'
  const date = new Date(dateString)
  return date.toLocaleString('es-EC', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function formatCurrency(amount: number): string {
  return `$${amount.toLocaleString('es-EC', { minimumFractionDigits: 2 })}`
}

export default function AdminPage() {
  const { user } = useAuthStore()
  const [searchParams, setSearchParams] = useSearchParams()
  const tabParam = searchParams.get('tab') as TabType | null
  const [activeTab, setActiveTab] = useState<TabType>(tabParam || 'stats')
  const [loading, setLoading] = useState(true)

  // Data states
  const [systemStats, setSystemStats] = useState<SystemStats | null>(null)
  const [transferStats, setTransferStats] = useState<TransferStats | null>(null)
  const [users, setUsers] = useState<AdminUser[]>([])
  const [transfers, setTransfers] = useState<Transfer[]>([])
  const [accounts, setAccounts] = useState<AdminAccount[]>([])
  const [accountSearch, setAccountSearch] = useState('')

  // Pagination
  const [userPage, setUserPage] = useState(0)
  const [userTotalPages, setUserTotalPages] = useState(0)
  const [transferPage, setTransferPage] = useState(0)
  const [transferTotalPages, setTransferTotalPages] = useState(0)
  const [accountPage, setAccountPage] = useState(0)
  const [accountTotalPages, setAccountTotalPages] = useState(0)

  // Account adjustment modal
  const [selectedAccount, setSelectedAccount] = useState<AdminAccount | null>(null)
  const [showAdjustModal, setShowAdjustModal] = useState(false)
  const [adjustType, setAdjustType] = useState<'DEPOSIT' | 'WITHDRAW'>('DEPOSIT')
  const [adjustAmount, setAdjustAmount] = useState('')
  const [adjustDescription, setAdjustDescription] = useState('')
  const [adjusting, setAdjusting] = useState(false)

  // Account history modal
  const [showHistoryModal, setShowHistoryModal] = useState(false)
  const [accountHistory, setAccountHistory] = useState<BalanceHistoryEntry[]>([])
  const [historyPage, setHistoryPage] = useState(0)
  const [historyTotalPages, setHistoryTotalPages] = useState(0)
  const [loadingHistory, setLoadingHistory] = useState(false)

  // Check admin role
  if (!user?.roles.includes('ADMIN')) {
    toast.error('Acceso denegado: se requiere rol de administrador')
    return <Navigate to="/dashboard" replace />
  }

  // Sync tab with URL
  useEffect(() => {
    if (tabParam && tabParam !== activeTab) {
      setActiveTab(tabParam)
    }
  }, [tabParam])

  // Update URL when tab changes
  const handleTabChange = (tab: TabType) => {
    setActiveTab(tab)
    if (tab === 'stats') {
      setSearchParams({})
    } else {
      setSearchParams({ tab })
    }
  }

  useEffect(() => {
    loadData()
  }, [activeTab, userPage, transferPage, accountPage])

  const loadData = async () => {
    setLoading(true)
    try {
      if (activeTab === 'stats') {
        const [sysStats, txStats] = await Promise.all([
          adminService.getSystemStats(),
          adminService.getTransferStats(),
        ])
        setSystemStats(sysStats)
        setTransferStats(txStats)
      } else if (activeTab === 'users') {
        const response = await adminService.getUsers(userPage, 10)
        setUsers(response.content)
        setUserTotalPages(response.totalPages)
      } else if (activeTab === 'transfers') {
        const response = await adminService.getAllTransfers(transferPage, 10)
        setTransfers(response.content)
        setTransferTotalPages(response.totalPages)
      } else if (activeTab === 'accounts') {
        const response = await adminService.getAllAccounts(accountPage, 10, accountSearch || undefined)
        setAccounts(response.content)
        setAccountTotalPages(response.totalPages)
      }
    } catch (error) {
      console.error('Error loading admin data:', error)
      toast.error('Error al cargar datos de administración')
    } finally {
      setLoading(false)
    }
  }

  const handleSearchAccounts = () => {
    setAccountPage(0)
    loadData()
  }

  const handleOpenAdjustModal = (account: AdminAccount, type: 'DEPOSIT' | 'WITHDRAW') => {
    setSelectedAccount(account)
    setAdjustType(type)
    setAdjustAmount('')
    setAdjustDescription('')
    setShowAdjustModal(true)
  }

  const handleAdjustBalance = async () => {
    if (!selectedAccount || !adjustAmount || !adjustDescription) {
      toast.error('Complete todos los campos')
      return
    }

    const amount = parseFloat(adjustAmount)
    if (isNaN(amount) || amount <= 0) {
      toast.error('Monto inválido')
      return
    }

    if (adjustType === 'WITHDRAW' && amount > selectedAccount.availableBalance) {
      toast.error('Saldo insuficiente para retiro')
      return
    }

    setAdjusting(true)
    try {
      await adminService.adjustAccountBalance(selectedAccount.id, {
        type: adjustType,
        amount,
        description: adjustDescription,
      })
      toast.success(adjustType === 'DEPOSIT' ? 'Depósito realizado' : 'Retiro realizado')
      setShowAdjustModal(false)
      loadData()
    } catch {
      toast.error('Error al ajustar saldo')
    } finally {
      setAdjusting(false)
    }
  }

  const handleViewHistory = async (account: AdminAccount) => {
    setSelectedAccount(account)
    setHistoryPage(0)
    setShowHistoryModal(true)
    await loadAccountHistory(account.id, 0)
  }

  const loadAccountHistory = async (accountId: number, page: number) => {
    setLoadingHistory(true)
    try {
      const response = await adminService.getAccountHistory(accountId, page, 10)
      setAccountHistory(response.content)
      setHistoryTotalPages(response.totalPages)
    } catch {
      toast.error('Error al cargar historial')
    } finally {
      setLoadingHistory(false)
    }
  }

  const handleLockUser = async (userId: number) => {
    try {
      await adminService.lockUser(userId)
      toast.success('Usuario bloqueado')
      loadData()
    } catch {
      toast.error('Error al bloquear usuario')
    }
  }

  const handleUnlockUser = async (userId: number) => {
    try {
      await adminService.unlockUser(userId)
      toast.success('Usuario desbloqueado')
      loadData()
    } catch {
      toast.error('Error al desbloquear usuario')
    }
  }

  const handleActivateUser = async (userId: number) => {
    try {
      await adminService.activateUser(userId)
      toast.success('Usuario activado')
      loadData()
    } catch {
      toast.error('Error al activar usuario')
    }
  }

  const handleDeactivateUser = async (userId: number) => {
    try {
      await adminService.deactivateUser(userId)
      toast.success('Usuario desactivado')
      loadData()
    } catch {
      toast.error('Error al desactivar usuario')
    }
  }

  const handleChangeRole = async (userId: number, currentRole: string) => {
    const newRole = currentRole === 'ADMIN' ? 'USER' : 'ADMIN'
    try {
      await adminService.changeUserRole(userId, newRole)
      toast.success(`Rol cambiado a ${newRole}`)
      loadData()
    } catch {
      toast.error('Error al cambiar rol')
    }
  }

  const handleApproveTransfer = async (transferId: number) => {
    try {
      await adminService.approveTransfer(transferId)
      toast.success('Transferencia aprobada')
      loadData()
    } catch {
      toast.error('Error al aprobar transferencia')
    }
  }

  const handleRejectTransfer = async (transferId: number) => {
    const reason = prompt('Motivo del rechazo:')
    if (!reason) return
    try {
      await adminService.rejectTransfer(transferId, reason)
      toast.success('Transferencia rechazada')
      loadData()
    } catch {
      toast.error('Error al rechazar transferencia')
    }
  }

  const tabs = [
    { id: 'stats' as TabType, name: 'Estadísticas', icon: ChartBarIcon },
    { id: 'users' as TabType, name: 'Usuarios', icon: UsersIcon },
    { id: 'accounts' as TabType, name: 'Cuentas', icon: CreditCardIcon },
    { id: 'transfers' as TabType, name: 'Transferencias', icon: ArrowsRightLeftIcon },
  ]

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center gap-3">
        <div className="p-3 bg-primary-100 rounded-xl">
          <ShieldCheckIcon className="w-8 h-8 text-primary-600" />
        </div>
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Panel de Administración</h1>
          <p className="text-sm text-gray-500">Gestiona usuarios, transferencias y estadísticas del sistema</p>
        </div>
      </div>

      {/* Tabs */}
      <div className="card p-1">
        <nav className="flex space-x-1">
          {tabs.map((tab) => (
            <button
              key={tab.id}
              onClick={() => handleTabChange(tab.id)}
              className={`flex-1 flex items-center justify-center gap-2 px-4 py-3 text-sm font-medium rounded-lg transition-all ${
                activeTab === tab.id
                  ? 'bg-primary-100 text-primary-700'
                  : 'text-gray-500 hover:text-gray-700 hover:bg-gray-100'
              }`}
            >
              <tab.icon className="w-5 h-5" />
              {tab.name}
            </button>
          ))}
        </nav>
      </div>

      {/* Loading */}
      {loading && (
        <div className="card flex items-center justify-center py-12">
          <ArrowPathIcon className="w-8 h-8 animate-spin text-primary-500" />
          <span className="ml-3 text-gray-500">Cargando...</span>
        </div>
      )}

      {/* Content */}
      <AnimatePresence mode="wait">
        {!loading && (
          <motion.div
            key={activeTab}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            transition={{ duration: 0.2 }}
          >
            {/* Stats Tab */}
            {activeTab === 'stats' && systemStats && transferStats && (
              <div className="space-y-6">
                {/* User Stats */}
                <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                  <StatsCard
                    title="Total Usuarios"
                    value={systemStats.totalUsers}
                    icon={UserGroupIcon}
                    color="blue"
                  />
                  <StatsCard
                    title="Usuarios Activos"
                    value={systemStats.activeUsers}
                    icon={CheckCircleIcon}
                    color="green"
                  />
                  <StatsCard
                    title="Usuarios Bloqueados"
                    value={systemStats.lockedUsers}
                    icon={LockClosedIcon}
                    color="red"
                  />
                  <StatsCard
                    title="Administradores"
                    value={systemStats.adminUsers}
                    icon={ShieldCheckIcon}
                    color="purple"
                  />
                </div>

                {/* Account Stats */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <StatsCard
                    title="Total Cuentas"
                    value={systemStats.totalAccounts}
                    icon={BuildingLibraryIcon}
                    color="blue"
                  />
                  <StatsCard
                    title="Cuentas Activas"
                    value={systemStats.activeAccounts}
                    icon={CheckCircleIcon}
                    color="green"
                  />
                  <StatsCard
                    title="Balance Total"
                    value={formatCurrency(systemStats.totalBalance)}
                    icon={BanknotesIcon}
                    color="emerald"
                    isText
                  />
                </div>

                {/* Transfer Stats */}
                <div className="card">
                  <h3 className="text-lg font-semibold text-gray-900 mb-4">Estadísticas de Transferencias</h3>
                  <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                    <div className="text-center p-4 bg-gray-50 rounded-lg">
                      <p className="text-2xl font-bold text-gray-900">{transferStats.totalTransfers}</p>
                      <p className="text-sm text-gray-500">Total</p>
                    </div>
                    <div className="text-center p-4 bg-yellow-50 rounded-lg">
                      <p className="text-2xl font-bold text-yellow-600">{transferStats.pendingTransfers}</p>
                      <p className="text-sm text-gray-500">Pendientes</p>
                    </div>
                    <div className="text-center p-4 bg-green-50 rounded-lg">
                      <p className="text-2xl font-bold text-green-600">{transferStats.completedTransfers}</p>
                      <p className="text-sm text-gray-500">Completadas</p>
                    </div>
                    <div className="text-center p-4 bg-red-50 rounded-lg">
                      <p className="text-2xl font-bold text-red-600">{transferStats.failedTransfers}</p>
                      <p className="text-sm text-gray-500">Fallidas</p>
                    </div>
                  </div>
                  <div className="mt-4 pt-4 border-t">
                    <div className="flex justify-between items-center">
                      <span className="text-gray-500">Monto Total Procesado:</span>
                      <span className="text-xl font-bold text-gray-900">{formatCurrency(transferStats.completedAmount)}</span>
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* Users Tab */}
            {activeTab === 'users' && (
              <div className="card p-0 overflow-hidden">
                <div className="overflow-x-auto">
                  <table className="w-full">
                    <thead className="bg-gray-50">
                      <tr className="text-left text-sm text-gray-500">
                        <th className="px-6 py-4 font-medium">Usuario</th>
                        <th className="px-6 py-4 font-medium">Email</th>
                        <th className="px-6 py-4 font-medium">Rol</th>
                        <th className="px-6 py-4 font-medium">Estado</th>
                        <th className="px-6 py-4 font-medium">Último Login</th>
                        <th className="px-6 py-4 font-medium">Acciones</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y">
                      {users.map((u) => (
                        <motion.tr
                          key={u.id}
                          initial={{ opacity: 0 }}
                          animate={{ opacity: 1 }}
                          className="text-sm hover:bg-gray-50"
                        >
                          <td className="px-6 py-4">
                            <div className="flex items-center gap-3">
                              {u.profilePhotoUrl ? (
                                <img
                                  src={u.profilePhotoUrl}
                                  alt=""
                                  className="w-8 h-8 rounded-full object-cover"
                                />
                              ) : (
                                <UserCircleIcon className="w-8 h-8 text-gray-400" />
                              )}
                              <div>
                                <p className="font-medium text-gray-900">{u.fullName}</p>
                                <p className="text-gray-500">@{u.username}</p>
                              </div>
                            </div>
                          </td>
                          <td className="px-6 py-4 text-gray-500">{u.email}</td>
                          <td className="px-6 py-4">
                            <span
                              className={`px-2 py-1 text-xs font-medium rounded-full ${
                                u.role === 'ADMIN'
                                  ? 'bg-purple-100 text-purple-800'
                                  : 'bg-blue-100 text-blue-800'
                              }`}
                            >
                              {u.role}
                            </span>
                          </td>
                          <td className="px-6 py-4">
                            <div className="flex items-center gap-2">
                              {u.isLocked ? (
                                <span className="flex items-center gap-1 text-red-600">
                                  <LockClosedIcon className="w-4 h-4" />
                                  Bloqueado
                                </span>
                              ) : u.isActive ? (
                                <span className="flex items-center gap-1 text-green-600">
                                  <CheckCircleIcon className="w-4 h-4" />
                                  Activo
                                </span>
                              ) : (
                                <span className="flex items-center gap-1 text-gray-500">
                                  <XCircleIcon className="w-4 h-4" />
                                  Inactivo
                                </span>
                              )}
                            </div>
                          </td>
                          <td className="px-6 py-4 text-gray-500">
                            {formatDate(u.lastLoginAt)}
                          </td>
                          <td className="px-6 py-4">
                            <div className="flex items-center gap-2">
                              {u.isLocked ? (
                                <button
                                  onClick={() => handleUnlockUser(u.id)}
                                  className="p-1.5 text-green-600 hover:bg-green-50 rounded-lg"
                                  title="Desbloquear"
                                >
                                  <LockOpenIcon className="w-4 h-4" />
                                </button>
                              ) : (
                                <button
                                  onClick={() => handleLockUser(u.id)}
                                  className="p-1.5 text-red-600 hover:bg-red-50 rounded-lg"
                                  title="Bloquear"
                                >
                                  <LockClosedIcon className="w-4 h-4" />
                                </button>
                              )}
                              {u.isActive ? (
                                <button
                                  onClick={() => handleDeactivateUser(u.id)}
                                  className="p-1.5 text-gray-600 hover:bg-gray-100 rounded-lg"
                                  title="Desactivar"
                                >
                                  <XCircleIcon className="w-4 h-4" />
                                </button>
                              ) : (
                                <button
                                  onClick={() => handleActivateUser(u.id)}
                                  className="p-1.5 text-green-600 hover:bg-green-50 rounded-lg"
                                  title="Activar"
                                >
                                  <CheckCircleIcon className="w-4 h-4" />
                                </button>
                              )}
                              <button
                                onClick={() => handleChangeRole(u.id, u.role)}
                                className="p-1.5 text-purple-600 hover:bg-purple-50 rounded-lg"
                                title={u.role === 'ADMIN' ? 'Quitar Admin' : 'Hacer Admin'}
                              >
                                <ShieldCheckIcon className="w-4 h-4" />
                              </button>
                            </div>
                          </td>
                        </motion.tr>
                      ))}
                    </tbody>
                  </table>
                </div>

                {/* Pagination */}
                {userTotalPages > 1 && (
                  <div className="flex items-center justify-between px-6 py-4 border-t bg-gray-50">
                    <div className="text-sm text-gray-500">
                      Página {userPage + 1} de {userTotalPages}
                    </div>
                    <div className="flex gap-2">
                      <button
                        onClick={() => setUserPage(Math.max(0, userPage - 1))}
                        disabled={userPage === 0}
                        className="px-3 py-1 text-sm border rounded-lg disabled:opacity-50 hover:bg-gray-100"
                      >
                        Anterior
                      </button>
                      <button
                        onClick={() => setUserPage(Math.min(userTotalPages - 1, userPage + 1))}
                        disabled={userPage >= userTotalPages - 1}
                        className="px-3 py-1 text-sm border rounded-lg disabled:opacity-50 hover:bg-gray-100"
                      >
                        Siguiente
                      </button>
                    </div>
                  </div>
                )}
              </div>
            )}

            {/* Accounts Tab */}
            {activeTab === 'accounts' && (
              <div className="space-y-4">
                {/* Search */}
                <div className="card">
                  <div className="flex items-center gap-4">
                    <div className="flex-1 relative">
                      <MagnifyingGlassIcon className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                      <input
                        type="text"
                        value={accountSearch}
                        onChange={(e) => setAccountSearch(e.target.value)}
                        onKeyDown={(e) => e.key === 'Enter' && handleSearchAccounts()}
                        placeholder="Buscar por número de cuenta, cliente o documento..."
                        className="w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                      />
                    </div>
                    <button
                      onClick={handleSearchAccounts}
                      className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700"
                    >
                      Buscar
                    </button>
                    {accountSearch && (
                      <button
                        onClick={() => { setAccountSearch(''); setAccountPage(0); loadData(); }}
                        className="p-2 text-gray-500 hover:bg-gray-100 rounded-lg"
                      >
                        <XMarkIcon className="w-5 h-5" />
                      </button>
                    )}
                  </div>
                </div>

                {/* Accounts Table */}
                <div className="card p-0 overflow-hidden">
                  <div className="overflow-x-auto">
                    <table className="w-full">
                      <thead className="bg-gray-50">
                        <tr className="text-left text-sm text-gray-500">
                          <th className="px-6 py-4 font-medium">Cuenta</th>
                          <th className="px-6 py-4 font-medium">Cliente</th>
                          <th className="px-6 py-4 font-medium">Tipo</th>
                          <th className="px-6 py-4 font-medium">Saldo</th>
                          <th className="px-6 py-4 font-medium">Disponible</th>
                          <th className="px-6 py-4 font-medium">Estado</th>
                          <th className="px-6 py-4 font-medium">Acciones</th>
                        </tr>
                      </thead>
                      <tbody className="divide-y">
                        {accounts.map((acc) => (
                          <motion.tr
                            key={acc.id}
                            initial={{ opacity: 0 }}
                            animate={{ opacity: 1 }}
                            className="text-sm hover:bg-gray-50"
                          >
                            <td className="px-6 py-4">
                              <span className="font-mono font-medium text-gray-900">
                                {acc.accountNumber}
                              </span>
                            </td>
                            <td className="px-6 py-4">
                              <div>
                                <p className="font-medium text-gray-900">{acc.customerName}</p>
                                <p className="text-gray-500 text-xs">{acc.documentNumber}</p>
                                <p className="text-gray-400 text-xs">@{acc.username}</p>
                              </div>
                            </td>
                            <td className="px-6 py-4">
                              <span className={`px-2 py-1 text-xs font-medium rounded-full ${
                                acc.accountType === 'SAVINGS'
                                  ? 'bg-green-100 text-green-800'
                                  : 'bg-blue-100 text-blue-800'
                              }`}>
                                {acc.accountType === 'SAVINGS' ? 'Ahorros' : 'Corriente'}
                              </span>
                            </td>
                            <td className="px-6 py-4 font-medium text-gray-900">
                              {formatCurrency(acc.balance)}
                            </td>
                            <td className="px-6 py-4 text-gray-600">
                              {formatCurrency(acc.availableBalance)}
                            </td>
                            <td className="px-6 py-4">
                              <span className={`px-2 py-1 text-xs font-medium rounded-full ${
                                acc.status === 'ACTIVE'
                                  ? 'bg-green-100 text-green-800'
                                  : 'bg-red-100 text-red-800'
                              }`}>
                                {acc.status === 'ACTIVE' ? 'Activa' : 'Inactiva'}
                              </span>
                            </td>
                            <td className="px-6 py-4">
                              <div className="flex items-center gap-2">
                                <button
                                  onClick={() => handleOpenAdjustModal(acc, 'DEPOSIT')}
                                  className="p-1.5 text-green-600 hover:bg-green-50 rounded-lg"
                                  title="Depositar"
                                >
                                  <PlusIcon className="w-4 h-4" />
                                </button>
                                <button
                                  onClick={() => handleOpenAdjustModal(acc, 'WITHDRAW')}
                                  className="p-1.5 text-red-600 hover:bg-red-50 rounded-lg"
                                  title="Retirar"
                                >
                                  <MinusIcon className="w-4 h-4" />
                                </button>
                                <button
                                  onClick={() => handleViewHistory(acc)}
                                  className="p-1.5 text-blue-600 hover:bg-blue-50 rounded-lg"
                                  title="Ver historial"
                                >
                                  <ClockIcon className="w-4 h-4" />
                                </button>
                              </div>
                            </td>
                          </motion.tr>
                        ))}
                      </tbody>
                    </table>
                  </div>

                  {/* Pagination */}
                  {accountTotalPages > 1 && (
                    <div className="flex items-center justify-between px-6 py-4 border-t bg-gray-50">
                      <div className="text-sm text-gray-500">
                        Página {accountPage + 1} de {accountTotalPages}
                      </div>
                      <div className="flex gap-2">
                        <button
                          onClick={() => setAccountPage(Math.max(0, accountPage - 1))}
                          disabled={accountPage === 0}
                          className="px-3 py-1 text-sm border rounded-lg disabled:opacity-50 hover:bg-gray-100"
                        >
                          Anterior
                        </button>
                        <button
                          onClick={() => setAccountPage(Math.min(accountTotalPages - 1, accountPage + 1))}
                          disabled={accountPage >= accountTotalPages - 1}
                          className="px-3 py-1 text-sm border rounded-lg disabled:opacity-50 hover:bg-gray-100"
                        >
                          Siguiente
                        </button>
                      </div>
                    </div>
                  )}
                </div>
              </div>
            )}

            {/* Transfers Tab */}
            {activeTab === 'transfers' && (
              <div className="card p-0 overflow-hidden">
                <div className="overflow-x-auto">
                  <table className="w-full">
                    <thead className="bg-gray-50">
                      <tr className="text-left text-sm text-gray-500">
                        <th className="px-6 py-4 font-medium">Referencia</th>
                        <th className="px-6 py-4 font-medium">Origen</th>
                        <th className="px-6 py-4 font-medium">Destino</th>
                        <th className="px-6 py-4 font-medium">Monto</th>
                        <th className="px-6 py-4 font-medium">Estado</th>
                        <th className="px-6 py-4 font-medium">Iniciado por</th>
                        <th className="px-6 py-4 font-medium">Fecha</th>
                        <th className="px-6 py-4 font-medium">Acciones</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y">
                      {transfers.map((t) => {
                        const status = statusConfig[t.status] || statusConfig.PENDING
                        return (
                          <motion.tr
                            key={t.id}
                            initial={{ opacity: 0 }}
                            animate={{ opacity: 1 }}
                            className="text-sm hover:bg-gray-50"
                          >
                            <td className="px-6 py-4">
                              <span className="font-mono font-medium text-gray-900">
                                {t.referenceNumber}
                              </span>
                            </td>
                            <td className="px-6 py-4 font-mono text-gray-600">
                              {formatAccountNumber(t.sourceAccountNumber)}
                            </td>
                            <td className="px-6 py-4 font-mono text-gray-600">
                              {formatAccountNumber(t.targetAccountNumber)}
                            </td>
                            <td className="px-6 py-4 font-medium text-gray-900">
                              {t.currency} {t.amount.toLocaleString('es-EC', { minimumFractionDigits: 2 })}
                            </td>
                            <td className="px-6 py-4">
                              <span className={`px-2 py-1 text-xs font-medium rounded-full ${status.color}`}>
                                {status.label}
                              </span>
                            </td>
                            <td className="px-6 py-4 text-gray-500">{t.initiatedBy}</td>
                            <td className="px-6 py-4 text-gray-500">{formatDate(t.initiatedAt)}</td>
                            <td className="px-6 py-4">
                              {t.status === 'PENDING' && (
                                <div className="flex items-center gap-2">
                                  <button
                                    onClick={() => handleApproveTransfer(t.id)}
                                    className="p-1.5 text-green-600 hover:bg-green-50 rounded-lg"
                                    title="Aprobar"
                                  >
                                    <CheckCircleIcon className="w-4 h-4" />
                                  </button>
                                  <button
                                    onClick={() => handleRejectTransfer(t.id)}
                                    className="p-1.5 text-red-600 hover:bg-red-50 rounded-lg"
                                    title="Rechazar"
                                  >
                                    <XCircleIcon className="w-4 h-4" />
                                  </button>
                                </div>
                              )}
                            </td>
                          </motion.tr>
                        )
                      })}
                    </tbody>
                  </table>
                </div>

                {/* Pagination */}
                {transferTotalPages > 1 && (
                  <div className="flex items-center justify-between px-6 py-4 border-t bg-gray-50">
                    <div className="text-sm text-gray-500">
                      Página {transferPage + 1} de {transferTotalPages}
                    </div>
                    <div className="flex gap-2">
                      <button
                        onClick={() => setTransferPage(Math.max(0, transferPage - 1))}
                        disabled={transferPage === 0}
                        className="px-3 py-1 text-sm border rounded-lg disabled:opacity-50 hover:bg-gray-100"
                      >
                        Anterior
                      </button>
                      <button
                        onClick={() => setTransferPage(Math.min(transferTotalPages - 1, transferPage + 1))}
                        disabled={transferPage >= transferTotalPages - 1}
                        className="px-3 py-1 text-sm border rounded-lg disabled:opacity-50 hover:bg-gray-100"
                      >
                        Siguiente
                      </button>
                    </div>
                  </div>
                )}
              </div>
            )}
          </motion.div>
        )}
      </AnimatePresence>

      {/* Balance Adjustment Modal */}
      {showAdjustModal && selectedAccount && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="bg-white rounded-2xl shadow-xl max-w-md w-full mx-4"
          >
            <div className="p-6 border-b">
              <div className="flex items-center justify-between">
                <h3 className="text-lg font-semibold text-gray-900">
                  {adjustType === 'DEPOSIT' ? 'Depositar Dinero' : 'Retirar Dinero'}
                </h3>
                <button
                  onClick={() => setShowAdjustModal(false)}
                  className="p-2 hover:bg-gray-100 rounded-lg"
                >
                  <XMarkIcon className="w-5 h-5" />
                </button>
              </div>
            </div>

            <div className="p-6 space-y-4">
              {/* Account Info */}
              <div className="bg-gray-50 rounded-lg p-4">
                <p className="text-sm text-gray-500">Cuenta</p>
                <p className="font-mono font-semibold text-gray-900">{selectedAccount.accountNumber}</p>
                <p className="text-sm text-gray-600">{selectedAccount.customerName}</p>
                <div className="mt-2 pt-2 border-t">
                  <p className="text-sm text-gray-500">Saldo Actual</p>
                  <p className="text-xl font-bold text-gray-900">{formatCurrency(selectedAccount.balance)}</p>
                </div>
              </div>

              {/* Amount Input */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Monto ({adjustType === 'DEPOSIT' ? 'a depositar' : 'a retirar'})
                </label>
                <div className="relative">
                  <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500">$</span>
                  <input
                    type="number"
                    step="0.01"
                    min="0.01"
                    value={adjustAmount}
                    onChange={(e) => setAdjustAmount(e.target.value)}
                    placeholder="0.00"
                    className="w-full pl-8 pr-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                  />
                </div>
              </div>

              {/* Description Input */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Descripción / Motivo
                </label>
                <textarea
                  value={adjustDescription}
                  onChange={(e) => setAdjustDescription(e.target.value)}
                  placeholder="Ej: Depósito en efectivo, Ajuste por promoción..."
                  rows={3}
                  className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                />
              </div>

              {/* Preview */}
              {adjustAmount && parseFloat(adjustAmount) > 0 && (
                <div className={`rounded-lg p-4 ${adjustType === 'DEPOSIT' ? 'bg-green-50' : 'bg-red-50'}`}>
                  <p className="text-sm text-gray-600">Nuevo saldo después del {adjustType === 'DEPOSIT' ? 'depósito' : 'retiro'}:</p>
                  <p className={`text-2xl font-bold ${adjustType === 'DEPOSIT' ? 'text-green-600' : 'text-red-600'}`}>
                    {formatCurrency(
                      adjustType === 'DEPOSIT'
                        ? selectedAccount.balance + parseFloat(adjustAmount)
                        : selectedAccount.balance - parseFloat(adjustAmount)
                    )}
                  </p>
                </div>
              )}
            </div>

            <div className="p-6 border-t bg-gray-50 flex gap-3">
              <button
                onClick={() => setShowAdjustModal(false)}
                className="flex-1 px-4 py-2 border rounded-lg hover:bg-gray-100"
              >
                Cancelar
              </button>
              <button
                onClick={handleAdjustBalance}
                disabled={adjusting || !adjustAmount || !adjustDescription}
                className={`flex-1 px-4 py-2 rounded-lg text-white disabled:opacity-50 ${
                  adjustType === 'DEPOSIT'
                    ? 'bg-green-600 hover:bg-green-700'
                    : 'bg-red-600 hover:bg-red-700'
                }`}
              >
                {adjusting ? 'Procesando...' : adjustType === 'DEPOSIT' ? 'Depositar' : 'Retirar'}
              </button>
            </div>
          </motion.div>
        </div>
      )}

      {/* Account History Modal */}
      {showHistoryModal && selectedAccount && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="bg-white rounded-2xl shadow-xl max-w-3xl w-full mx-4 max-h-[80vh] flex flex-col"
          >
            <div className="p-6 border-b">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="text-lg font-semibold text-gray-900">
                    Historial de Movimientos
                  </h3>
                  <p className="text-sm text-gray-500">
                    Cuenta: <span className="font-mono">{selectedAccount.accountNumber}</span> - {selectedAccount.customerName}
                  </p>
                </div>
                <button
                  onClick={() => setShowHistoryModal(false)}
                  className="p-2 hover:bg-gray-100 rounded-lg"
                >
                  <XMarkIcon className="w-5 h-5" />
                </button>
              </div>
            </div>

            <div className="flex-1 overflow-auto p-6">
              {loadingHistory ? (
                <div className="flex items-center justify-center py-12">
                  <ArrowPathIcon className="w-8 h-8 animate-spin text-primary-500" />
                </div>
              ) : accountHistory.length === 0 ? (
                <div className="text-center py-12 text-gray-500">
                  No hay movimientos registrados
                </div>
              ) : (
                <div className="space-y-3">
                  {accountHistory.map((entry) => (
                    <div key={entry.id} className="border rounded-lg p-4">
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-3">
                          <div className={`p-2 rounded-full ${
                            entry.transactionType.includes('CREDIT') || entry.transactionType === 'ADMIN_DEPOSIT'
                              ? 'bg-green-100'
                              : 'bg-red-100'
                          }`}>
                            {entry.transactionType.includes('CREDIT') || entry.transactionType === 'ADMIN_DEPOSIT' ? (
                              <PlusIcon className="w-4 h-4 text-green-600" />
                            ) : (
                              <MinusIcon className="w-4 h-4 text-red-600" />
                            )}
                          </div>
                          <div>
                            <p className="font-medium text-gray-900">{entry.description}</p>
                            <p className="text-xs text-gray-500">{entry.transactionType} - {entry.referenceId}</p>
                          </div>
                        </div>
                        <div className="text-right">
                          <p className={`font-bold ${
                            entry.transactionType.includes('CREDIT') || entry.transactionType === 'ADMIN_DEPOSIT'
                              ? 'text-green-600'
                              : 'text-red-600'
                          }`}>
                            {entry.transactionType.includes('CREDIT') || entry.transactionType === 'ADMIN_DEPOSIT' ? '+' : '-'}
                            {formatCurrency(entry.amount)}
                          </p>
                          <p className="text-xs text-gray-500">
                            Saldo: {formatCurrency(entry.balanceAfter)}
                          </p>
                        </div>
                      </div>
                      <div className="mt-2 text-xs text-gray-400 flex justify-between">
                        <span>Anterior: {formatCurrency(entry.balanceBefore)}</span>
                        <span>{formatDate(entry.createdAt)}</span>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* History Pagination */}
            {historyTotalPages > 1 && (
              <div className="p-4 border-t bg-gray-50 flex items-center justify-between">
                <div className="text-sm text-gray-500">
                  Página {historyPage + 1} de {historyTotalPages}
                </div>
                <div className="flex gap-2">
                  <button
                    onClick={() => {
                      const newPage = Math.max(0, historyPage - 1)
                      setHistoryPage(newPage)
                      loadAccountHistory(selectedAccount.id, newPage)
                    }}
                    disabled={historyPage === 0}
                    className="px-3 py-1 text-sm border rounded-lg disabled:opacity-50 hover:bg-gray-100"
                  >
                    Anterior
                  </button>
                  <button
                    onClick={() => {
                      const newPage = Math.min(historyTotalPages - 1, historyPage + 1)
                      setHistoryPage(newPage)
                      loadAccountHistory(selectedAccount.id, newPage)
                    }}
                    disabled={historyPage >= historyTotalPages - 1}
                    className="px-3 py-1 text-sm border rounded-lg disabled:opacity-50 hover:bg-gray-100"
                  >
                    Siguiente
                  </button>
                </div>
              </div>
            )}
          </motion.div>
        </div>
      )}
    </div>
  )
}

// Stats Card Component
function StatsCard({
  title,
  value,
  icon: Icon,
  color,
  isText = false,
}: {
  title: string
  value: number | string
  icon: typeof UsersIcon
  color: string
  isText?: boolean
}) {
  const colorClasses: Record<string, string> = {
    blue: 'bg-blue-100 text-blue-600',
    green: 'bg-green-100 text-green-600',
    red: 'bg-red-100 text-red-600',
    purple: 'bg-purple-100 text-purple-600',
    emerald: 'bg-emerald-100 text-emerald-600',
    yellow: 'bg-yellow-100 text-yellow-600',
  }

  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.95 }}
      animate={{ opacity: 1, scale: 1 }}
      className="card"
    >
      <div className="flex items-center gap-4">
        <div className={`p-3 rounded-xl ${colorClasses[color]}`}>
          <Icon className="w-6 h-6" />
        </div>
        <div>
          <p className="text-sm text-gray-500">{title}</p>
          <p className={`font-bold ${isText ? 'text-xl' : 'text-2xl'} text-gray-900`}>
            {value}
          </p>
        </div>
      </div>
    </motion.div>
  )
}
