import { useState, useEffect, useMemo } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import toast from 'react-hot-toast'
import { transferService, Transfer } from '../services/transferService'
import { accountService, Account } from '../services/accountService'
import { useAccountStore } from '../stores/accountStore'
import {
  ArrowDownIcon,
  ArrowUpIcon,
  MagnifyingGlassIcon,
  ArrowPathIcon,
  PaperAirplaneIcon,
  CheckCircleIcon,
  XCircleIcon,
  ClockIcon,
  ArrowsRightLeftIcon,
  ExclamationTriangleIcon,
  EyeIcon,
  XMarkIcon,
  ArrowLongRightIcon,
  CalendarIcon,
  DocumentTextIcon,
  BanknotesIcon,
  ChevronDownIcon,
} from '@heroicons/react/24/outline'

const statusConfig: Record<string, { badgeClass: string; icon: typeof CheckCircleIcon; label: string }> = {
  COMPLETED: { badgeClass: 'badge-success', icon: CheckCircleIcon, label: 'Completada' },
  FAILED: { badgeClass: 'badge-danger', icon: XCircleIcon, label: 'Fallida' },
  PENDING: { badgeClass: 'badge-warning', icon: ClockIcon, label: 'Pendiente' },
  PROCESSING: { badgeClass: 'badge-accent', icon: ArrowPathIcon, label: 'Procesando' },
  VALIDATING: { badgeClass: 'badge-accent', icon: ArrowPathIcon, label: 'Validando' },
  REVERSED: { badgeClass: 'badge-warning', icon: ArrowsRightLeftIcon, label: 'Reversada' },
  CANCELLED: { badgeClass: 'badge', icon: XCircleIcon, label: 'Cancelada' },
}

function formatAccountNumber(accountNumber?: string, accountId?: number): string {
  if (accountNumber && accountNumber.length >= 8) {
    const first = accountNumber.slice(0, 4)
    const last = accountNumber.slice(-4)
    return `${first}****${last}`
  }
  if (accountNumber && accountNumber.length > 0) {
    return `****${accountNumber.slice(-4)}`
  }
  return accountId ? `ID: ${accountId}` : '---'
}

function formatFullAccountNumber(accountNumber?: string): string {
  return accountNumber || 'No disponible'
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

function formatDateLong(dateString?: string): string {
  if (!dateString) return '---'
  const date = new Date(dateString)
  return date.toLocaleString('es-EC', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  })
}

function formatCurrency(amount: number | null | undefined, currency: string): string {
  if (amount === null || amount === undefined) return '---'
  return `${currency} ${amount.toLocaleString('es-EC', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

function TransferDetailModal({
  transfer,
  onClose,
  isOutgoing,
  isIncoming,
}: {
  transfer: Transfer
  onClose: () => void
  isOutgoing: boolean
  isIncoming: boolean
}) {
  const status = statusConfig[transfer.status] || statusConfig.PENDING
  const StatusIcon = status.icon
  const relevantBalance = isOutgoing ? transfer.sourceBalanceAfter : isIncoming ? transfer.targetBalanceAfter : undefined

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      className="modal-backdrop"
      onClick={onClose}
    >
      <motion.div
        initial={{ opacity: 0, scale: 0.95, y: 20 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        exit={{ opacity: 0, scale: 0.95, y: 20 }}
        className="modal-content"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="p-6 border-b border-gray-200/50" style={{ background: 'linear-gradient(145deg, var(--accent-500), var(--accent-600))' }}>
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-xl font-bold text-white">Detalle de Transferencia</h2>
              <p className="text-white/80 text-sm mt-1 font-mono">{transfer.referenceNumber}</p>
            </div>
            <button onClick={onClose} className="icon-container-sm bg-white/20 hover:bg-white/30">
              <XMarkIcon className="w-5 h-5 text-white" />
            </button>
          </div>
        </div>

        {/* Status Badge */}
        <div className="px-6 py-4 card-inset mx-6 mt-6">
          <div className="flex items-center justify-between">
            <span className="text-sm text-gray-500">Estado</span>
            <span className={`inline-flex items-center gap-2 ${status.badgeClass}`}>
              <StatusIcon className="w-4 h-4" />
              {status.label}
            </span>
          </div>
        </div>

        {/* Amount */}
        <div className="px-6 py-6 text-center">
          <p className="text-sm text-gray-500 mb-1">
            {isOutgoing ? 'Dinero Enviado' : isIncoming ? 'Dinero Recibido' : 'Monto Transferido'}
          </p>
          {isOutgoing ? (
            <div className="flex items-center justify-center gap-2">
              <ArrowUpIcon className="w-8 h-8 text-red-500" />
              <p className="text-3xl font-bold text-red-600">-{formatCurrency(transfer.amount, transfer.currency)}</p>
            </div>
          ) : isIncoming ? (
            <div className="flex items-center justify-center gap-2">
              <ArrowDownIcon className="w-8 h-8 text-green-500" />
              <p className="text-3xl font-bold text-green-600">+{formatCurrency(transfer.amount, transfer.currency)}</p>
            </div>
          ) : (
            <p className="text-3xl font-bold text-gray-900">{formatCurrency(transfer.amount, transfer.currency)}</p>
          )}
          {transfer.status === 'COMPLETED' && relevantBalance !== undefined && (
            <div className="mt-3 pt-3 divider">
              <p className="text-xs text-gray-400">Saldo despues de transferencia</p>
              <p className="text-lg font-semibold text-gray-700 font-mono">{formatCurrency(relevantBalance, transfer.currency)}</p>
            </div>
          )}
        </div>

        {/* Accounts Flow */}
        <div className="px-6 py-4">
          <div className="flex items-center justify-between gap-4">
            <div className="flex-1 card-inset text-center" style={{ background: 'linear-gradient(145deg, #fee2e2, #fef2f2)' }}>
              <p className="text-xs text-red-600 font-medium mb-1">CUENTA ORIGEN</p>
              <p className="text-lg font-bold text-gray-900 font-mono">{formatFullAccountNumber(transfer.sourceAccountNumber)}</p>
            </div>
            <div className="icon-container-sm">
              <ArrowLongRightIcon className="w-6 h-6 text-gray-400" />
            </div>
            <div className="flex-1 card-inset text-center" style={{ background: 'linear-gradient(145deg, #d1fae5, #ecfdf5)' }}>
              <p className="text-xs text-green-600 font-medium mb-1">CUENTA DESTINO</p>
              <p className="text-lg font-bold text-gray-900 font-mono">{formatFullAccountNumber(transfer.targetAccountNumber)}</p>
            </div>
          </div>
        </div>

        {/* Details */}
        <div className="px-6 py-4 space-y-4">
          {transfer.description && (
            <div className="flex items-start gap-3">
              <div className="icon-container-sm"><DocumentTextIcon className="w-5 h-5 text-gray-400" /></div>
              <div><p className="text-xs text-gray-500">Descripcion</p><p className="text-sm text-gray-900">{transfer.description}</p></div>
            </div>
          )}
          <div className="flex items-start gap-3">
            <div className="icon-container-sm"><CalendarIcon className="w-5 h-5 text-gray-400" /></div>
            <div><p className="text-xs text-gray-500">Fecha de Inicio</p><p className="text-sm text-gray-900">{formatDateLong(transfer.initiatedAt)}</p></div>
          </div>
          {transfer.completedAt && (
            <div className="flex items-start gap-3">
              <div className="icon-container-sm" style={{ background: 'linear-gradient(145deg, #d1fae5, #ecfdf5)' }}><CheckCircleIcon className="w-5 h-5 text-green-500" /></div>
              <div><p className="text-xs text-gray-500">Fecha de Completado</p><p className="text-sm text-gray-900">{formatDateLong(transfer.completedAt)}</p></div>
            </div>
          )}
          {transfer.failureReason && (
            <div className="card-inset" style={{ background: 'linear-gradient(145deg, #fee2e2, #fef2f2)' }}>
              <p className="text-xs text-red-600 font-medium">Motivo del Fallo</p>
              <p className="text-sm text-red-800 mt-1">{transfer.failureReason}</p>
            </div>
          )}
          <div className="flex items-start gap-3">
            <div className="icon-container-sm"><BanknotesIcon className="w-5 h-5 text-gray-400" /></div>
            <div><p className="text-xs text-gray-500">Iniciado por</p><p className="text-sm text-gray-900">{transfer.initiatedBy}</p></div>
          </div>
        </div>

        {/* Footer */}
        <div className="p-6">
          <button onClick={onClose} className="btn-secondary w-full">Cerrar</button>
        </div>
      </motion.div>
    </motion.div>
  )
}

export default function TransfersPage() {
  const navigate = useNavigate()
  const { accounts, selectedAccount, setAccounts, selectAccount, clearSelection } = useAccountStore()
  const [transfers, setTransfers] = useState<Transfer[]>([])
  const [myAccounts, setMyAccounts] = useState<Account[]>([])
  const [loading, setLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState('')
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [selectedTransfer, setSelectedTransfer] = useState<Transfer | null>(null)
  const [showAccountDropdown, setShowAccountDropdown] = useState(false)

  const displayAccounts = accounts.length > 0 ? accounts : myAccounts
  const myAccountIds = useMemo(() => new Set(displayAccounts.map(a => a.id)), [displayAccounts])
  const isOutgoing = (transfer: Transfer) => myAccountIds.has(transfer.sourceAccountId)
  const isIncoming = (transfer: Transfer) => myAccountIds.has(transfer.targetAccountId)
  const getRelevantBalance = (transfer: Transfer) => isOutgoing(transfer) ? transfer.sourceBalanceAfter : isIncoming(transfer) ? transfer.targetBalanceAfter : undefined

  useEffect(() => { loadAccounts() }, [])
  useEffect(() => { loadTransfers() }, [page, selectedAccount?.id])

  const loadAccounts = async () => {
    try {
      const accountsData = await accountService.getMyAccounts()
      setMyAccounts(accountsData)
      setAccounts(accountsData)
    } catch (error) { console.error('Error loading accounts:', error) }
  }

  const loadTransfers = async () => {
    try {
      setLoading(true)
      const response = selectedAccount
        ? await transferService.getAccountTransfers(selectedAccount.id, page, 10)
        : await transferService.getMyTransfers(page, 10)
      setTransfers(response.content)
      setTotalPages(response.totalPages)
      setTotalElements(response.totalElements)
    } catch (error) {
      console.error('Error loading transfers:', error)
      toast.error('Error al cargar transferencias')
    } finally { setLoading(false) }
  }

  const handleSelectAccount = (account: Account | null) => {
    if (account) selectAccount(account)
    else clearSelection()
    setPage(0)
    setShowAccountDropdown(false)
  }

  const filteredTransfers = transfers.filter(t =>
    t.referenceNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
    (t.sourceAccountNumber && t.sourceAccountNumber.includes(searchTerm)) ||
    (t.targetAccountNumber && t.targetAccountNumber.includes(searchTerm))
  )

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">{selectedAccount ? 'Movimientos de Cuenta' : 'Todas las Transferencias'}</h1>
          <p className="text-sm text-gray-500 mt-1">{totalElements} transferencia{totalElements !== 1 ? 's' : ''}{selectedAccount && ` en cuenta ${selectedAccount.accountNumber}`}</p>
        </div>
        <motion.div whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }}>
          <Link to={selectedAccount ? `/transfers/new?from=${selectedAccount.id}` : '/transfers/new'} className="btn-accent flex items-center gap-2">
            <PaperAirplaneIcon className="w-5 h-5" />Nueva Transferencia
          </Link>
        </motion.div>
      </div>

      {/* Account Selector */}
      <div className="card">
        <div className="flex items-center gap-4">
          <div className="relative">
            <button onClick={() => setShowAccountDropdown(!showAccountDropdown)} className="card-sm flex items-center gap-3 min-w-[280px] hover:shadow-neumorphism-hover transition-all">
              {selectedAccount ? (
                <>
                  <div className="icon-container-accent"><BanknotesIcon className="w-5 h-5" style={{ color: 'var(--accent-600)' }} /></div>
                  <div className="text-left flex-1">
                    <p className="font-mono font-medium text-gray-900">{selectedAccount.accountNumber}</p>
                    <p className="text-xs text-gray-500">{selectedAccount.accountType === 'SAVINGS' ? 'Ahorro' : 'Corriente'} - ${selectedAccount.availableBalance.toLocaleString('es-EC', { minimumFractionDigits: 2 })}</p>
                  </div>
                </>
              ) : (
                <>
                  <div className="icon-container"><ArrowsRightLeftIcon className="w-5 h-5 text-gray-600" /></div>
                  <div className="text-left flex-1">
                    <p className="font-medium text-gray-900">Todas las cuentas</p>
                    <p className="text-xs text-gray-500">Ver transferencias de todas tus cuentas</p>
                  </div>
                </>
              )}
              <ChevronDownIcon className={`w-5 h-5 text-gray-400 transition-transform ${showAccountDropdown ? 'rotate-180' : ''}`} />
            </button>

            <AnimatePresence>
              {showAccountDropdown && (
                <motion.div initial={{ opacity: 0, y: -10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -10 }} className="absolute top-full left-0 mt-2 w-full card z-20 p-2">
                  <button onClick={() => handleSelectAccount(null)} className={`w-full card-sm flex items-center gap-3 mb-2 ${!selectedAccount ? 'card-inset' : ''}`}>
                    <div className="icon-container"><ArrowsRightLeftIcon className="w-5 h-5 text-gray-600" /></div>
                    <div className="text-left"><p className="font-medium text-gray-900">Todas las cuentas</p><p className="text-xs text-gray-500">Ver todas las transferencias</p></div>
                  </button>
                  {displayAccounts.map(account => (
                    <button key={account.id} onClick={() => handleSelectAccount(account)} className={`w-full card-sm flex items-center gap-3 mb-2 ${selectedAccount?.id === account.id ? 'card-inset' : ''}`}>
                      <div className="icon-container-accent"><BanknotesIcon className="w-5 h-5" style={{ color: 'var(--accent-600)' }} /></div>
                      <div className="text-left flex-1">
                        <p className="font-mono font-medium text-gray-900">{account.accountNumber}</p>
                        <p className="text-xs text-gray-500">{account.accountType === 'SAVINGS' ? 'Ahorro' : 'Corriente'} - ${account.availableBalance.toLocaleString('es-EC', { minimumFractionDigits: 2 })}</p>
                      </div>
                      {selectedAccount?.id === account.id && <CheckCircleIcon className="w-5 h-5" style={{ color: 'var(--accent-600)' }} />}
                    </button>
                  ))}
                </motion.div>
              )}
            </AnimatePresence>
          </div>
          {selectedAccount && (
            <button onClick={() => navigate(`/accounts/${selectedAccount.id}`)} className="btn-primary">Ver detalle de cuenta</button>
          )}
        </div>
      </div>

      {/* Search */}
      <div className="card">
        <div className="flex gap-4">
          <div className="relative flex-1">
            <MagnifyingGlassIcon className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input type="text" placeholder="Buscar por referencia o cuenta..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} className="input pl-12" />
          </div>
          <motion.button whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }} onClick={loadTransfers} className="btn-secondary flex items-center gap-2" disabled={loading}>
            <ArrowPathIcon className={`w-5 h-5 ${loading ? 'animate-spin' : ''}`} />Actualizar
          </motion.button>
        </div>
      </div>

      {/* Loading */}
      {loading && transfers.length === 0 && (
        <div className="card flex items-center justify-center py-12">
          <div className="icon-container"><ArrowPathIcon className="w-8 h-8 animate-spin" style={{ color: 'var(--accent-600)' }} /></div>
          <span className="ml-3 text-gray-500">Cargando transferencias...</span>
        </div>
      )}

      {/* Empty */}
      {!loading && transfers.length === 0 && (
        <div className="card flex flex-col items-center justify-center py-12">
          <div className="icon-container mb-4"><ExclamationTriangleIcon className="w-12 h-12 text-gray-400" /></div>
          <h3 className="text-lg font-medium text-gray-900">Sin transferencias</h3>
          <p className="text-gray-500 mt-1">Aun no has realizado ninguna transferencia</p>
          <Link to="/transfers/new" className="btn-accent mt-4">Realizar primera transferencia</Link>
        </div>
      )}

      {/* Table */}
      {transfers.length > 0 && (
        <div className="table-neumorphism">
          <table className="w-full">
            <thead>
              <tr>
                <th>Referencia</th>
                <th>Origen</th>
                <th>Destino</th>
                <th>Monto</th>
                <th>Saldo</th>
                <th>Estado</th>
                <th>Fecha</th>
                <th className="text-center">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {filteredTransfers.map((transfer) => {
                const status = statusConfig[transfer.status] || statusConfig.PENDING
                const StatusIcon = status.icon
                return (
                  <tr key={transfer.id}>
                    <td><span className="font-mono font-medium text-gray-900">{transfer.referenceNumber}</span></td>
                    <td><span className="font-mono">{formatAccountNumber(transfer.sourceAccountNumber, transfer.sourceAccountId)}</span></td>
                    <td><span className="font-mono">{formatAccountNumber(transfer.targetAccountNumber, transfer.targetAccountId)}</span></td>
                    <td>
                      {isOutgoing(transfer) ? (
                        <span className="inline-flex items-center gap-1 font-semibold text-red-600"><ArrowUpIcon className="w-4 h-4" />-{formatCurrency(transfer.amount, transfer.currency)}</span>
                      ) : isIncoming(transfer) ? (
                        <span className="inline-flex items-center gap-1 font-semibold text-green-600"><ArrowDownIcon className="w-4 h-4" />+{formatCurrency(transfer.amount, transfer.currency)}</span>
                      ) : (
                        <span className="font-medium text-gray-900">{formatCurrency(transfer.amount, transfer.currency)}</span>
                      )}
                    </td>
                    <td>{transfer.status === 'COMPLETED' && getRelevantBalance(transfer) !== undefined ? <span className="font-mono text-sm">{formatCurrency(getRelevantBalance(transfer)!, transfer.currency)}</span> : <span className="text-gray-400">---</span>}</td>
                    <td><span className={`inline-flex items-center gap-1.5 ${status.badgeClass}`}><StatusIcon className="w-3.5 h-3.5" />{status.label}</span></td>
                    <td className="text-gray-500">{formatDate(transfer.initiatedAt)}</td>
                    <td className="text-center">
                      <motion.button whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }} onClick={() => setSelectedTransfer(transfer)} className="btn-primary py-1.5 px-3 text-xs flex items-center gap-1">
                        <EyeIcon className="w-4 h-4" />Ver
                      </motion.button>
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
          {totalPages > 1 && (
            <div className="flex items-center justify-between px-6 py-4 border-t border-gray-200/50">
              <div className="text-sm text-gray-500">Pagina {page + 1} de {totalPages}</div>
              <div className="flex gap-2">
                <button onClick={() => setPage(Math.max(0, page - 1))} disabled={page === 0} className="btn-secondary py-1.5 px-3 text-sm">Anterior</button>
                <button onClick={() => setPage(Math.min(totalPages - 1, page + 1))} disabled={page >= totalPages - 1} className="btn-secondary py-1.5 px-3 text-sm">Siguiente</button>
              </div>
            </div>
          )}
        </div>
      )}

      <AnimatePresence>
        {selectedTransfer && <TransferDetailModal transfer={selectedTransfer} onClose={() => setSelectedTransfer(null)} isOutgoing={isOutgoing(selectedTransfer)} isIncoming={isIncoming(selectedTransfer)} />}
      </AnimatePresence>
    </div>
  )
}
