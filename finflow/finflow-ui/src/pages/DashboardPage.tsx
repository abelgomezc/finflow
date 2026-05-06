import { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { accountService, Account } from '../services/accountService'
import { transferService, Transfer } from '../services/transferService'
import { useAuthStore } from '../stores/authStore'
import { useAccountStore } from '../stores/accountStore'
import {
  ArrowsRightLeftIcon,
  BanknotesIcon,
  CreditCardIcon,
  PlusIcon,
  ArrowPathIcon,
  ChevronRightIcon,
  ArrowUpIcon,
  ArrowDownIcon,
  BuildingOfficeIcon,
} from '@heroicons/react/24/outline'

const typeLabels: Record<string, string> = {
  SAVINGS: 'Ahorro',
  CHECKING: 'Corriente',
  BUSINESS: 'Empresarial',
}

const typeIcons: Record<string, typeof BanknotesIcon> = {
  SAVINGS: BanknotesIcon,
  CHECKING: CreditCardIcon,
  BUSINESS: BuildingOfficeIcon,
}

const statusColors: Record<string, string> = {
  COMPLETED: 'badge-success',
  FAILED: 'badge-danger',
  PENDING: 'badge-warning',
  PROCESSING: 'badge-accent',
  VALIDATING: 'badge-accent',
  CANCELLED: 'badge',
  REVERSED: 'badge-warning',
}

const statusLabels: Record<string, string> = {
  COMPLETED: 'Completada',
  FAILED: 'Fallida',
  PENDING: 'Pendiente',
  PROCESSING: 'Procesando',
  VALIDATING: 'Validando',
  CANCELLED: 'Cancelada',
  REVERSED: 'Reversada',
}

function formatCurrency(amount: number, currency: string): string {
  return `${currency} ${amount.toLocaleString('es-EC', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

function formatDate(dateString: string): string {
  return new Date(dateString).toLocaleDateString('es-EC', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

export default function DashboardPage() {
  const navigate = useNavigate()
  const { user } = useAuthStore()
  const { accounts, setAccounts, selectAccount } = useAccountStore()
  const [localAccounts, setLocalAccounts] = useState<Account[]>([])
  const [transfers, setTransfers] = useState<Transfer[]>([])
  const [loadingAccounts, setLoadingAccounts] = useState(true)
  const [loadingTransfers, setLoadingTransfers] = useState(true)

  const displayAccounts = accounts.length > 0 ? accounts : localAccounts
  const myAccountIds = new Set(displayAccounts.map(a => a.id))

  useEffect(() => {
    loadAccounts()
    loadTransfers()
  }, [])

  const loadAccounts = async () => {
    try {
      setLoadingAccounts(true)
      const data = await accountService.getMyAccounts()
      setLocalAccounts(data)
      setAccounts(data)
    } catch (error) {
      console.error('Error loading accounts:', error)
    } finally {
      setLoadingAccounts(false)
    }
  }

  const handleSelectAccount = (account: Account) => {
    selectAccount(account)
    navigate(`/accounts/${account.id}`)
  }

  const loadTransfers = async () => {
    try {
      setLoadingTransfers(true)
      const response = await transferService.getMyTransfers(0, 5)
      setTransfers(response.content)
    } catch (error) {
      console.error('Error loading transfers:', error)
    } finally {
      setLoadingTransfers(false)
    }
  }

  const totalBalance = displayAccounts.reduce((sum, acc) => sum + acc.availableBalance, 0)
  const isOutgoing = (transfer: Transfer) => myAccountIds.has(transfer.sourceAccountId)
  const isIncoming = (transfer: Transfer) => myAccountIds.has(transfer.targetAccountId)

  return (
    <div className="space-y-8">
      {/* Header con saludo */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">
            Hola, {user?.fullName?.split(' ')[0] || 'Usuario'}
          </h1>
          <p className="text-gray-500">Bienvenido a tu banca digital</p>
        </div>
        <motion.div whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }}>
          <Link to="/transfers/new" className="btn-accent flex items-center gap-2">
            <PlusIcon className="w-5 h-5" />
            Nueva Transferencia
          </Link>
        </motion.div>
      </div>

      {/* Resumen de saldo total */}
      {!loadingAccounts && displayAccounts.length > 0 && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="card"
          style={{ background: 'linear-gradient(145deg, var(--accent-500), var(--accent-600))' }}
        >
          <p className="text-white/80 text-sm mb-1">Saldo Total Disponible</p>
          <p className="text-4xl font-bold text-white">{formatCurrency(totalBalance, 'USD')}</p>
          <p className="text-white/70 text-sm mt-2">
            En {displayAccounts.length} cuenta{displayAccounts.length !== 1 ? 's' : ''}
          </p>
        </motion.div>
      )}

      {/* Mis Cuentas */}
      <div>
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-semibold text-gray-900">Mis Cuentas</h2>
          <Link to="/accounts" className="text-sm flex items-center gap-1 hover:opacity-80 transition-opacity" style={{ color: 'var(--accent-600)' }}>
            Ver todas
            <ChevronRightIcon className="w-4 h-4" />
          </Link>
        </div>

        {loadingAccounts ? (
          <div className="flex items-center justify-center py-12">
            <div className="icon-container">
              <ArrowPathIcon className="w-6 h-6 animate-spin" style={{ color: 'var(--accent-600)' }} />
            </div>
          </div>
        ) : displayAccounts.length === 0 ? (
          <div className="card text-center py-8">
            <div className="icon-container mx-auto w-fit mb-3">
              <BanknotesIcon className="w-8 h-8 text-gray-400" />
            </div>
            <p className="text-gray-500">No tienes cuentas registradas</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {displayAccounts.map((account, index) => {
              const TypeIcon = typeIcons[account.accountType] || BanknotesIcon

              return (
                <motion.div
                  key={account.id}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: index * 0.1 }}
                  whileHover={{ scale: 1.02 }}
                  onClick={() => handleSelectAccount(account)}
                  className="card cursor-pointer group"
                >
                  <div className="flex items-start justify-between mb-3">
                    <div className="flex items-center gap-3">
                      <div className="icon-container-accent">
                        <TypeIcon className="w-5 h-5" style={{ color: 'var(--accent-600)' }} />
                      </div>
                      <div>
                        <p className="text-xs text-gray-500">
                          {typeLabels[account.accountType] || account.accountType}
                        </p>
                        <p className="font-mono font-medium text-gray-900">
                          {account.accountNumber}
                        </p>
                      </div>
                    </div>
                    <ChevronRightIcon className="w-5 h-5 text-gray-400 group-hover:text-gray-600 transition-colors" />
                  </div>

                  <div className="card-inset">
                    <p className="text-xs text-gray-500">Disponible</p>
                    <p className="text-xl font-bold" style={{ color: 'var(--accent-600)' }}>
                      {formatCurrency(account.availableBalance, account.currency)}
                    </p>
                  </div>
                </motion.div>
              )
            })}
          </div>
        )}
      </div>

      {/* Ultimos Movimientos */}
      <div className="card">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-lg font-semibold text-gray-900">Ultimos Movimientos</h2>
          <Link to="/transfers" className="text-sm flex items-center gap-1 hover:opacity-80 transition-opacity" style={{ color: 'var(--accent-600)' }}>
            Ver todos
            <ChevronRightIcon className="w-4 h-4" />
          </Link>
        </div>

        {loadingTransfers ? (
          <div className="flex items-center justify-center py-12">
            <div className="icon-container">
              <ArrowPathIcon className="w-6 h-6 animate-spin" style={{ color: 'var(--accent-600)' }} />
            </div>
          </div>
        ) : transfers.length === 0 ? (
          <div className="text-center py-8">
            <div className="icon-container mx-auto w-fit mb-3">
              <ArrowsRightLeftIcon className="w-8 h-8 text-gray-400" />
            </div>
            <p className="text-gray-500">No hay movimientos recientes</p>
            <Link
              to="/transfers/new"
              className="mt-2 inline-block hover:opacity-80 transition-opacity"
              style={{ color: 'var(--accent-600)' }}
            >
              Realizar primera transferencia
            </Link>
          </div>
        ) : (
          <div className="space-y-3">
            {transfers.map((transfer) => (
              <motion.div
                key={transfer.id}
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                whileHover={{ scale: 1.01 }}
                onClick={() => navigate('/transfers')}
                className="card-sm cursor-pointer flex items-center justify-between"
              >
                <div className="flex items-center gap-4">
                  {isOutgoing(transfer) ? (
                    <div className="icon-container-sm" style={{ background: 'linear-gradient(145deg, #fee2e2, #fef2f2)' }}>
                      <ArrowUpIcon className="w-5 h-5 text-red-600" />
                    </div>
                  ) : isIncoming(transfer) ? (
                    <div className="icon-container-sm" style={{ background: 'linear-gradient(145deg, #d1fae5, #ecfdf5)' }}>
                      <ArrowDownIcon className="w-5 h-5 text-green-600" />
                    </div>
                  ) : (
                    <div className="icon-container-sm">
                      <ArrowsRightLeftIcon className="w-5 h-5 text-gray-600" />
                    </div>
                  )}
                  <div>
                    <p className="font-medium text-gray-900">
                      {isOutgoing(transfer)
                        ? `Enviado a ${transfer.targetAccountNumber || 'cuenta'}`
                        : isIncoming(transfer)
                        ? `Recibido de ${transfer.sourceAccountNumber || 'cuenta'}`
                        : transfer.referenceNumber}
                    </p>
                    <p className="text-sm text-gray-500">{formatDate(transfer.initiatedAt)}</p>
                  </div>
                </div>

                <div className="text-right">
                  {isOutgoing(transfer) ? (
                    <p className="font-semibold text-red-600">
                      -{formatCurrency(transfer.amount, transfer.currency)}
                    </p>
                  ) : isIncoming(transfer) ? (
                    <p className="font-semibold text-green-600">
                      +{formatCurrency(transfer.amount, transfer.currency)}
                    </p>
                  ) : (
                    <p className="font-semibold text-gray-900">
                      {formatCurrency(transfer.amount, transfer.currency)}
                    </p>
                  )}
                  <span className={statusColors[transfer.status]}>
                    {statusLabels[transfer.status] || transfer.status}
                  </span>
                </div>
              </motion.div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
