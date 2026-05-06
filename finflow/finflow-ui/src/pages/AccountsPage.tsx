import { useState, useEffect } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import toast from 'react-hot-toast'
import { accountService, Account, CreateAccountRequest } from '../services/accountService'
import {
  ClipboardDocumentIcon,
  ShareIcon,
  XMarkIcon,
  CheckIcon,
  BanknotesIcon,
  CreditCardIcon,
  ArrowPathIcon,
  PlusIcon,
  BuildingOfficeIcon,
} from '@heroicons/react/24/outline'

const statusColors: Record<string, string> = {
  ACTIVE: 'badge-success',
  BLOCKED: 'badge-danger',
  CLOSED: 'badge',
}

const statusLabels: Record<string, string> = {
  ACTIVE: 'Activa',
  BLOCKED: 'Bloqueada',
  CLOSED: 'Cerrada',
}

const typeLabels: Record<string, string> = {
  SAVINGS: 'Cuenta de Ahorro',
  CHECKING: 'Cuenta Corriente',
  BUSINESS: 'Cuenta Empresarial',
}

const typeIcons: Record<string, typeof BanknotesIcon> = {
  SAVINGS: BanknotesIcon,
  CHECKING: CreditCardIcon,
  BUSINESS: BuildingOfficeIcon,
}

// Modal para crear nueva cuenta
function CreateAccountModal({
  onClose,
  onSuccess,
  customerId,
}: {
  onClose: () => void
  onSuccess: () => void
  customerId: number
}) {
  const [loading, setLoading] = useState(false)
  const [formData, setFormData] = useState({
    accountType: 'SAVINGS' as 'CHECKING' | 'SAVINGS' | 'BUSINESS',
    initialBalance: 0,
    dailyTransferLimit: 10000,
    perTransferLimit: 5000,
  })

  const accountTypes = [
    { value: 'SAVINGS', label: 'Cuenta de Ahorro', description: 'Ideal para ahorrar con beneficios', icon: BanknotesIcon },
    { value: 'CHECKING', label: 'Cuenta Corriente', description: 'Para transacciones diarias', icon: CreditCardIcon },
    { value: 'BUSINESS', label: 'Cuenta Empresarial', description: 'Para negocios y empresas', icon: BuildingOfficeIcon },
  ]

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)

    try {
      const request: CreateAccountRequest = {
        customerId,
        accountType: formData.accountType,
        currency: 'USD',
        initialBalance: formData.initialBalance,
        dailyTransferLimit: formData.dailyTransferLimit,
        perTransferLimit: formData.perTransferLimit,
      }
      await accountService.createAccount(request)
      toast.success('Cuenta creada exitosamente')
      onSuccess()
      onClose()
    } catch (error) {
      console.error('Error creating account:', error)
      toast.error('Error al crear la cuenta')
    } finally {
      setLoading(false)
    }
  }

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      className="modal-backdrop"
      onClick={onClose}
    >
      <motion.div
        initial={{ scale: 0.95, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        exit={{ scale: 0.95, opacity: 0 }}
        className="modal-content"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="p-6 border-b border-gray-200/50">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="icon-container-accent">
                <PlusIcon className="w-6 h-6" style={{ color: 'var(--accent-600)' }} />
              </div>
              <div>
                <h3 className="text-lg font-semibold text-gray-900">Nueva Cuenta</h3>
                <p className="text-sm text-gray-500">Crea una cuenta bancaria nueva</p>
              </div>
            </div>
            <button
              onClick={onClose}
              className="icon-container-sm hover:opacity-80 transition-opacity"
            >
              <XMarkIcon className="w-5 h-5 text-gray-500" />
            </button>
          </div>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-6 space-y-6">
          {/* Tipo de cuenta */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-3">
              Tipo de Cuenta
            </label>
            <div className="space-y-3">
              {accountTypes.map((type) => {
                const Icon = type.icon
                const isSelected = formData.accountType === type.value
                return (
                  <label
                    key={type.value}
                    className={`block cursor-pointer transition-all duration-200 rounded-2xl p-4 ${
                      isSelected ? 'card-inset' : 'card-sm'
                    }`}
                  >
                    <input
                      type="radio"
                      name="accountType"
                      value={type.value}
                      checked={isSelected}
                      onChange={(e) => setFormData({ ...formData, accountType: e.target.value as typeof formData.accountType })}
                      className="sr-only"
                    />
                    <div className="flex items-center gap-4">
                      <div className={`icon-container-sm ${isSelected ? 'icon-container-accent' : ''}`}>
                        <Icon className="w-5 h-5" style={{ color: isSelected ? 'var(--accent-600)' : '#6b7280' }} />
                      </div>
                      <div className="flex-1">
                        <p className={`font-medium ${isSelected ? 'text-gray-900' : 'text-gray-700'}`}>
                          {type.label}
                        </p>
                        <p className="text-xs text-gray-500">{type.description}</p>
                      </div>
                      {isSelected && (
                        <div className="icon-container-accent">
                          <CheckIcon className="w-4 h-4" style={{ color: 'var(--accent-600)' }} />
                        </div>
                      )}
                    </div>
                  </label>
                )
              })}
            </div>
          </div>

          {/* Balance inicial */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Balance Inicial (USD)
            </label>
            <div className="relative">
              <span className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-500 font-medium">$</span>
              <input
                type="number"
                min="0"
                step="0.01"
                value={formData.initialBalance}
                onChange={(e) => setFormData({ ...formData, initialBalance: parseFloat(e.target.value) || 0 })}
                className="input pl-8"
                placeholder="0.00"
              />
            </div>
          </div>

          {/* Limites */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Limite Diario
              </label>
              <div className="relative">
                <span className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-500 font-medium">$</span>
                <input
                  type="number"
                  min="100"
                  step="100"
                  value={formData.dailyTransferLimit}
                  onChange={(e) => setFormData({ ...formData, dailyTransferLimit: parseFloat(e.target.value) || 0 })}
                  className="input pl-8"
                />
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Limite por Transf.
              </label>
              <div className="relative">
                <span className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-500 font-medium">$</span>
                <input
                  type="number"
                  min="100"
                  step="100"
                  value={formData.perTransferLimit}
                  onChange={(e) => setFormData({ ...formData, perTransferLimit: parseFloat(e.target.value) || 0 })}
                  className="input pl-8"
                />
              </div>
            </div>
          </div>

          {/* Botones */}
          <div className="flex gap-4 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="btn-secondary flex-1"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={loading}
              className="btn-accent flex-1 flex items-center justify-center gap-2"
            >
              {loading ? (
                <>
                  <ArrowPathIcon className="w-5 h-5 animate-spin" />
                  Creando...
                </>
              ) : (
                <>
                  <PlusIcon className="w-5 h-5" />
                  Crear Cuenta
                </>
              )}
            </button>
          </div>
        </form>
      </motion.div>
    </motion.div>
  )
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
Numero de Cuenta: ${account.accountNumber}
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

  const handleShare = async () => {
    if (navigator.share) {
      try {
        await navigator.share({
          title: 'Datos de Cuenta FinFlow',
          text: accountData,
        })
      } catch (err) {
        if ((err as Error).name !== 'AbortError') {
          toast.error('Error al compartir')
        }
      }
    } else {
      handleCopy()
    }
  }

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      className="modal-backdrop"
      onClick={onClose}
    >
      <motion.div
        initial={{ scale: 0.95, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        exit={{ scale: 0.95, opacity: 0 }}
        className="modal-content"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="p-6 border-b border-gray-200/50">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="icon-container-accent">
                <ShareIcon className="w-6 h-6" style={{ color: 'var(--accent-600)' }} />
              </div>
              <h3 className="text-lg font-semibold text-gray-900">Compartir Datos de Cuenta</h3>
            </div>
            <button
              onClick={onClose}
              className="icon-container-sm hover:opacity-80 transition-opacity"
            >
              <XMarkIcon className="w-5 h-5 text-gray-500" />
            </button>
          </div>
        </div>

        {/* Content */}
        <div className="p-6">
          <p className="text-sm text-gray-500 mb-4">
            Comparte estos datos con quien necesite enviarte dinero:
          </p>

          {/* Account Data Display */}
          <div className="card-inset space-y-3">
            <div className="flex justify-between items-center">
              <span className="text-gray-500">Titular:</span>
              <span className="font-semibold text-gray-900">{account.customerName}</span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-gray-500">Tipo:</span>
              <span className="font-semibold text-gray-900">
                {typeLabels[account.accountType] || account.accountType}
              </span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-gray-500">Numero:</span>
              <span className="font-bold text-lg tracking-wider" style={{ color: 'var(--accent-600)' }}>
                {account.accountNumber}
              </span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-gray-500">Moneda:</span>
              <span className="font-semibold text-gray-900">{account.currency}</span>
            </div>
          </div>

          {/* Action Buttons */}
          <div className="mt-6 flex gap-3">
            <motion.button
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              onClick={handleCopy}
              className={`flex-1 flex items-center justify-center gap-2 py-3 px-4 rounded-2xl font-medium transition-all ${
                copied ? 'btn-accent' : 'btn-secondary'
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
                  Copiar
                </>
              )}
            </motion.button>

            {'share' in navigator && (
              <motion.button
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
                onClick={handleShare}
                className="btn-accent flex-1 flex items-center justify-center gap-2"
              >
                <ShareIcon className="w-5 h-5" />
                Compartir
              </motion.button>
            )}
          </div>
        </div>
      </motion.div>
    </motion.div>
  )
}

export default function AccountsPage() {
  const [accounts, setAccounts] = useState<Account[]>([])
  const [loading, setLoading] = useState(true)
  const [selectedAccount, setSelectedAccount] = useState<Account | null>(null)
  const [showCreateModal, setShowCreateModal] = useState(false)

  useEffect(() => {
    loadAccounts()
  }, [])

  const loadAccounts = async () => {
    try {
      setLoading(true)
      const data = await accountService.getMyAccounts()
      setAccounts(data)
    } catch (error) {
      console.error('Error loading accounts:', error)
      toast.error('Error al cargar las cuentas')
    } finally {
      setLoading(false)
    }
  }

  // Obtener customerId de la primera cuenta o usar un valor por defecto
  const customerId = accounts.length > 0 ? accounts[0].customerId : 0

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="icon-container">
          <ArrowPathIcon className="w-8 h-8 animate-spin" style={{ color: 'var(--accent-600)' }} />
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Mis Cuentas</h1>
          <p className="text-sm text-gray-500 mt-1">
            {accounts.length} cuenta{accounts.length !== 1 ? 's' : ''} registrada{accounts.length !== 1 ? 's' : ''}
          </p>
        </div>
        <motion.button
          whileHover={{ scale: 1.02 }}
          whileTap={{ scale: 0.98 }}
          onClick={() => setShowCreateModal(true)}
          className="btn-accent flex items-center gap-2"
        >
          <PlusIcon className="w-5 h-5" />
          Nueva Cuenta
        </motion.button>
      </div>

      {accounts.length === 0 ? (
        <div className="card text-center py-12">
          <div className="icon-container mx-auto w-fit mb-4">
            <BanknotesIcon className="w-12 h-12 text-gray-400" />
          </div>
          <p className="text-gray-500 mb-4">No tienes cuentas registradas</p>
          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            onClick={() => setShowCreateModal(true)}
            className="btn-accent inline-flex items-center gap-2"
          >
            <PlusIcon className="w-5 h-5" />
            Crear mi primera cuenta
          </motion.button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {accounts.map((account, index) => {
            const TypeIcon = typeIcons[account.accountType] || BanknotesIcon

            return (
              <motion.div
                key={account.id}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.1 }}
                className="card"
              >
                {/* Header */}
                <div className="flex items-center justify-between mb-4">
                  <div className="flex items-center gap-3">
                    <div className="icon-container-accent">
                      <TypeIcon className="w-5 h-5" style={{ color: 'var(--accent-600)' }} />
                    </div>
                    <div>
                      <p className="text-xs text-gray-500">
                        {typeLabels[account.accountType] || account.accountType}
                      </p>
                      <p className="font-mono font-semibold text-gray-900">
                        {account.accountNumber}
                      </p>
                    </div>
                  </div>
                  <span className={statusColors[account.status]}>
                    {statusLabels[account.status] || account.status}
                  </span>
                </div>

                {/* Balances */}
                <div className="card-inset space-y-3 mb-4">
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-500">Titular</span>
                    <span className="font-medium text-gray-900 truncate ml-2 max-w-[150px]">
                      {account.customerName}
                    </span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-500">Moneda</span>
                    <span className="font-medium text-gray-900">{account.currency}</span>
                  </div>
                  <div className="divider" />
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-500">Saldo Total</span>
                    <span className="font-medium text-gray-900">
                      {account.currency} {account.balance.toLocaleString('es-EC', { minimumFractionDigits: 2 })}
                    </span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-500 text-sm">Disponible</span>
                    <span className="font-bold text-lg" style={{ color: 'var(--accent-600)' }}>
                      {account.currency} {account.availableBalance.toLocaleString('es-EC', { minimumFractionDigits: 2 })}
                    </span>
                  </div>
                </div>

                {/* Actions */}
                <motion.button
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                  onClick={() => setSelectedAccount(account)}
                  className="btn-primary w-full flex items-center justify-center gap-2"
                >
                  <ShareIcon className="w-4 h-4" />
                  Compartir datos
                </motion.button>
              </motion.div>
            )
          })}
        </div>
      )}

      {/* Share Modal */}
      <AnimatePresence>
        {selectedAccount && (
          <ShareAccountModal
            account={selectedAccount}
            onClose={() => setSelectedAccount(null)}
          />
        )}
      </AnimatePresence>

      {/* Create Account Modal */}
      <AnimatePresence>
        {showCreateModal && customerId > 0 && (
          <CreateAccountModal
            onClose={() => setShowCreateModal(false)}
            onSuccess={loadAccounts}
            customerId={customerId}
          />
        )}
      </AnimatePresence>
    </div>
  )
}
