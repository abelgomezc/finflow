import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import toast from 'react-hot-toast'
import { accountService, Account } from '../services/accountService'
import { transferService, Transfer } from '../services/transferService'
import {
  ArrowLeftIcon,
  ArrowUpIcon,
  ArrowDownIcon,
  PaperAirplaneIcon,
  ShareIcon,
  ClipboardDocumentIcon,
  CheckIcon,
  XMarkIcon,
  BanknotesIcon,
  CreditCardIcon,
  ArrowPathIcon,
  CheckCircleIcon,
  XCircleIcon,
  ClockIcon,
  ExclamationTriangleIcon,
} from '@heroicons/react/24/outline'

const typeLabels: Record<string, string> = {
  SAVINGS: 'Cuenta de Ahorro',
  CHECKING: 'Cuenta Corriente',
}

const typeIcons: Record<string, typeof BanknotesIcon> = {
  SAVINGS: BanknotesIcon,
  CHECKING: CreditCardIcon,
}

const statusConfig: Record<string, { label: string; color: string; icon: typeof CheckCircleIcon }> = {
  PENDING: { label: 'Pendiente', color: 'bg-yellow-100 text-yellow-800', icon: ClockIcon },
  VALIDATING: { label: 'Validando', color: 'bg-blue-100 text-blue-800', icon: ArrowPathIcon },
  PROCESSING: { label: 'Procesando', color: 'bg-blue-100 text-blue-800', icon: ArrowPathIcon },
  COMPLETED: { label: 'Completada', color: 'bg-green-100 text-green-800', icon: CheckCircleIcon },
  FAILED: { label: 'Fallida', color: 'bg-red-100 text-red-800', icon: XCircleIcon },
  CANCELLED: { label: 'Cancelada', color: 'bg-gray-100 text-gray-800', icon: XCircleIcon },
  REVERSED: { label: 'Reversada', color: 'bg-orange-100 text-orange-800', icon: ExclamationTriangleIcon },
}

function formatCurrency(amount: number | null | undefined, currency: string): string {
  if (amount === null || amount === undefined) return '---'
  return `${currency} ${amount.toLocaleString('es-EC', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

function formatDate(dateString: string): string {
  return new Date(dateString).toLocaleDateString('es-EC', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

// Modal para compartir datos de cuenta
function ShareAccountModal({
  account,
  onClose,
}: {
  account: Account
  onClose: () => void
}) {
  const [copied, setCopied] = useState(false)

  const accountData = `
━━━━━━━━━━━━━━━━━━━━━━━━━
   DATOS DE CUENTA FINFLOW
━━━━━━━━━━━━━━━━━━━━━━━━━

Titular: ${account.customerName}
Tipo: ${typeLabels[account.accountType] || account.accountType}
Número de Cuenta: ${account.accountNumber}
Moneda: ${account.currency}

━━━━━━━━━━━━━━━━━━━━━━━━━
`.trim()

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(accountData)
      setCopied(true)
      toast.success('Datos copiados al portapapeles')
      setTimeout(() => setCopied(false), 2000)
    } catch {
      toast.error('Error al copiar')
    }
  }

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50"
      onClick={onClose}
    >
      <motion.div
        initial={{ scale: 0.95, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        exit={{ scale: 0.95, opacity: 0 }}
        className="bg-white rounded-2xl shadow-xl max-w-md w-full overflow-hidden"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="px-6 py-4 border-b flex items-center justify-between bg-gradient-to-r from-primary-600 to-primary-700">
          <div className="flex items-center gap-3">
            <ShareIcon className="w-6 h-6 text-white" />
            <h3 className="text-lg font-semibold text-white">Compartir Datos</h3>
          </div>
          <button onClick={onClose} className="p-1 rounded-full hover:bg-white/20">
            <XMarkIcon className="w-5 h-5 text-white" />
          </button>
        </div>

        <div className="p-6">
          <p className="text-sm text-gray-500 mb-4">
            Comparte estos datos con quien necesite enviarte dinero:
          </p>

          <div className="bg-gray-50 rounded-xl p-4 font-mono text-sm space-y-3 border-2 border-dashed border-gray-200">
            <div className="flex justify-between">
              <span className="text-gray-500">Titular:</span>
              <span className="font-semibold text-gray-900">{account.customerName}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-500">Tipo:</span>
              <span className="font-semibold text-gray-900">
                {typeLabels[account.accountType] || account.accountType}
              </span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-500">Número:</span>
              <span className="font-bold text-primary-600 text-lg tracking-wider">
                {account.accountNumber}
              </span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-500">Moneda:</span>
              <span className="font-semibold text-gray-900">{account.currency}</span>
            </div>
          </div>

          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            onClick={handleCopy}
            className={`w-full mt-6 flex items-center justify-center gap-2 py-3 px-4 rounded-xl font-medium transition-all ${
              copied
                ? 'bg-green-500 text-white'
                : 'bg-primary-600 text-white hover:bg-primary-700'
            }`}
          >
            {copied ? (
              <>
                <CheckIcon className="w-5 h-5" />
                Copiado!
              </>
            ) : (
              <>
                <ClipboardDocumentIcon className="w-5 h-5" />
                Copiar Datos
              </>
            )}
          </motion.button>
        </div>
      </motion.div>
    </motion.div>
  )
}

export default function AccountDetailPage() {
  const { accountId } = useParams<{ accountId: string }>()
  const navigate = useNavigate()
  const [account, setAccount] = useState<Account | null>(null)
  const [transfers, setTransfers] = useState<Transfer[]>([])
  const [loading, setLoading] = useState(true)
  const [loadingTransfers, setLoadingTransfers] = useState(true)
  const [showShareModal, setShowShareModal] = useState(false)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  useEffect(() => {
    if (accountId) {
      loadAccount()
      loadTransfers()
    }
  }, [accountId, page])

  const loadAccount = async () => {
    try {
      setLoading(true)
      const data = await accountService.getById(Number(accountId))
      setAccount(data)
    } catch (error) {
      console.error('Error loading account:', error)
      toast.error('Error al cargar la cuenta')
      navigate('/accounts')
    } finally {
      setLoading(false)
    }
  }

  const loadTransfers = async () => {
    try {
      setLoadingTransfers(true)
      const response = await transferService.getAccountTransfers(Number(accountId), page, 10)
      setTransfers(response.content)
      setTotalPages(response.totalPages)
    } catch (error) {
      console.error('Error loading transfers:', error)
    } finally {
      setLoadingTransfers(false)
    }
  }

  // Determina si una transferencia es saliente (dinero que sale de esta cuenta)
  const isOutgoing = (transfer: Transfer) => transfer.sourceAccountId === Number(accountId)

  // Determina si una transferencia es entrante (dinero que entra a esta cuenta)
  const isIncoming = (transfer: Transfer) => transfer.targetAccountId === Number(accountId)

  // Obtener el saldo después de la transferencia para esta cuenta
  const getBalanceAfter = (transfer: Transfer): number | undefined => {
    if (isOutgoing(transfer)) return transfer.sourceBalanceAfter
    if (isIncoming(transfer)) return transfer.targetBalanceAfter
    return undefined
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <ArrowPathIcon className="w-8 h-8 text-primary-600 animate-spin" />
      </div>
    )
  }

  if (!account) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-500">Cuenta no encontrada</p>
        <Link to="/accounts" className="text-primary-600 hover:underline mt-2 inline-block">
          Volver a mis cuentas
        </Link>
      </div>
    )
  }

  const TypeIcon = typeIcons[account.accountType] || BanknotesIcon

  return (
    <div className="space-y-6">
      {/* Header con navegación */}
      <div className="flex items-center gap-4">
        <button
          onClick={() => navigate(-1)}
          className="p-2 rounded-lg hover:bg-gray-100 transition-colors"
        >
          <ArrowLeftIcon className="w-5 h-5 text-gray-600" />
        </button>
        <div className="flex-1">
          <p className="text-sm text-gray-500">
            {typeLabels[account.accountType] || account.accountType}
          </p>
          <h1 className="text-2xl font-bold text-gray-900 font-mono">
            {account.accountNumber}
          </h1>
        </div>
      </div>

      {/* Card principal de la cuenta */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-gradient-to-br from-primary-600 to-primary-800 rounded-2xl p-6 text-white shadow-xl"
      >
        <div className="flex items-start justify-between mb-6">
          <div className="flex items-center gap-3">
            <div className="p-3 bg-white/20 rounded-xl">
              <TypeIcon className="w-8 h-8" />
            </div>
            <div>
              <p className="text-primary-100 text-sm">Titular</p>
              <p className="font-semibold text-lg">{account.customerName}</p>
            </div>
          </div>
          <span className="px-3 py-1 bg-green-400/20 text-green-100 rounded-full text-sm font-medium">
            {account.status === 'ACTIVE' ? 'Activa' : account.status}
          </span>
        </div>

        <div className="space-y-4">
          <div>
            <p className="text-primary-200 text-sm">Saldo Disponible</p>
            <p className="text-4xl font-bold tracking-tight">
              {formatCurrency(account.availableBalance, account.currency)}
            </p>
          </div>
          {account.balance !== account.availableBalance && (
            <div>
              <p className="text-primary-200 text-sm">Saldo Total</p>
              <p className="text-xl font-semibold text-primary-100">
                {formatCurrency(account.balance, account.currency)}
              </p>
            </div>
          )}
        </div>

        {/* Acciones rápidas */}
        <div className="mt-6 pt-6 border-t border-white/20 flex gap-3">
          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            onClick={() => navigate(`/transfers/new?from=${account.id}`)}
            className="flex-1 flex items-center justify-center gap-2 py-3 px-4 bg-white text-primary-700 rounded-xl font-medium hover:bg-primary-50 transition-colors"
          >
            <PaperAirplaneIcon className="w-5 h-5" />
            Transferir
          </motion.button>
          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            onClick={() => setShowShareModal(true)}
            className="flex items-center justify-center gap-2 py-3 px-4 bg-white/20 text-white rounded-xl font-medium hover:bg-white/30 transition-colors"
          >
            <ShareIcon className="w-5 h-5" />
            Compartir
          </motion.button>
        </div>
      </motion.div>

      {/* Historial de transferencias de esta cuenta */}
      <div className="card">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-lg font-semibold text-gray-900">
            Movimientos de esta cuenta
          </h2>
          <span className="text-sm text-gray-500">
            {transfers.length > 0 ? `${transfers.length} movimientos` : ''}
          </span>
        </div>

        {loadingTransfers ? (
          <div className="flex items-center justify-center py-12">
            <ArrowPathIcon className="w-6 h-6 text-primary-600 animate-spin" />
          </div>
        ) : transfers.length === 0 ? (
          <div className="text-center py-12">
            <ArrowPathIcon className="w-12 h-12 text-gray-300 mx-auto mb-3" />
            <p className="text-gray-500">No hay movimientos en esta cuenta</p>
            <Link
              to={`/transfers/new?from=${account.id}`}
              className="text-primary-600 hover:underline mt-2 inline-block"
            >
              Realizar primera transferencia
            </Link>
          </div>
        ) : (
          <>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50">
                  <tr className="text-left text-sm text-gray-500">
                    <th className="px-4 py-3 font-medium">Fecha</th>
                    <th className="px-4 py-3 font-medium">Descripción</th>
                    <th className="px-4 py-3 font-medium text-right">Monto</th>
                    <th className="px-4 py-3 font-medium text-right">Saldo</th>
                    <th className="px-4 py-3 font-medium text-center">Estado</th>
                  </tr>
                </thead>
                <tbody className="divide-y">
                  {transfers.map((transfer) => {
                    const status = statusConfig[transfer.status] || statusConfig.PENDING
                    const StatusIcon = status.icon
                    const balanceAfter = getBalanceAfter(transfer)

                    return (
                      <motion.tr
                        key={transfer.id}
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        className="hover:bg-gray-50 cursor-pointer"
                        onClick={() => navigate(`/transfers?ref=${transfer.referenceNumber}`)}
                      >
                        <td className="px-4 py-4 text-sm text-gray-600">
                          {formatDate(transfer.initiatedAt)}
                        </td>
                        <td className="px-4 py-4">
                          <div className="flex items-center gap-3">
                            {isOutgoing(transfer) ? (
                              <div className="p-1.5 bg-red-100 rounded-full">
                                <ArrowUpIcon className="w-4 h-4 text-red-600" />
                              </div>
                            ) : (
                              <div className="p-1.5 bg-green-100 rounded-full">
                                <ArrowDownIcon className="w-4 h-4 text-green-600" />
                              </div>
                            )}
                            <div>
                              <p className="font-medium text-gray-900 text-sm">
                                {isOutgoing(transfer)
                                  ? `Transferencia a ${transfer.targetAccountNumber || 'cuenta'}`
                                  : `Recibido de ${transfer.sourceAccountNumber || 'cuenta'}`}
                              </p>
                              <p className="text-xs text-gray-500">{transfer.referenceNumber}</p>
                            </div>
                          </div>
                        </td>
                        <td className="px-4 py-4 text-right">
                          {isOutgoing(transfer) ? (
                            <span className="font-semibold text-red-600">
                              -{formatCurrency(transfer.amount, transfer.currency)}
                            </span>
                          ) : (
                            <span className="font-semibold text-green-600">
                              +{formatCurrency(transfer.amount, transfer.currency)}
                            </span>
                          )}
                        </td>
                        <td className="px-4 py-4 text-right">
                          {transfer.status === 'COMPLETED' && balanceAfter !== undefined ? (
                            <span className="font-mono text-sm text-gray-600">
                              {formatCurrency(balanceAfter, transfer.currency)}
                            </span>
                          ) : (
                            <span className="text-gray-400 text-sm">---</span>
                          )}
                        </td>
                        <td className="px-4 py-4 text-center">
                          <span
                            className={`inline-flex items-center gap-1 px-2 py-1 text-xs font-medium rounded-full ${status.color}`}
                          >
                            <StatusIcon className="w-3 h-3" />
                            {status.label}
                          </span>
                        </td>
                      </motion.tr>
                    )
                  })}
                </tbody>
              </table>
            </div>

            {/* Paginación */}
            {totalPages > 1 && (
              <div className="mt-4 flex items-center justify-center gap-2">
                <button
                  onClick={() => setPage((p) => Math.max(0, p - 1))}
                  disabled={page === 0}
                  className="px-3 py-1 rounded-lg border disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                >
                  Anterior
                </button>
                <span className="text-sm text-gray-600">
                  Página {page + 1} de {totalPages}
                </span>
                <button
                  onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                  disabled={page >= totalPages - 1}
                  className="px-3 py-1 rounded-lg border disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                >
                  Siguiente
                </button>
              </div>
            )}
          </>
        )}
      </div>

      {/* Modal de compartir */}
      <AnimatePresence>
        {showShareModal && account && (
          <ShareAccountModal account={account} onClose={() => setShowShareModal(false)} />
        )}
      </AnimatePresence>
    </div>
  )
}
