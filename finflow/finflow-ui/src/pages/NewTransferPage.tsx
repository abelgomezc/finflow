import { useState, useEffect, useRef } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import toast from 'react-hot-toast'
import { motion, AnimatePresence } from 'framer-motion'
import { transferService } from '../services/transferService'
import { accountService, Account } from '../services/accountService'
import { useAccountStore } from '../stores/accountStore'
import {
  ArrowRightIcon,
  BanknotesIcon,
  CheckCircleIcon,
  ExclamationCircleIcon,
  ArrowPathIcon,
  PaperAirplaneIcon,
  UserCircleIcon,
  IdentificationIcon,
  CreditCardIcon,
} from '@heroicons/react/24/outline'

interface TransferFormData {
  sourceAccountId: string
  targetAccountNumber: string
  amount: number
  currency: string
  description?: string
}

// Componente de input de monto con formato automático
function AmountInput({
  value,
  onChange,
  error,
  maxAmount,
}: {
  value: string
  onChange: (value: string, numericValue: number) => void
  error?: string
  maxAmount?: number
}) {
  const inputRef = useRef<HTMLInputElement>(null)
  const [displayValue, setDisplayValue] = useState(value)

  // Formatear número para mostrar
  const formatDisplay = (val: string): string => {
    if (!val) return ''

    // Remover caracteres no válidos excepto números y punto
    let cleaned = val.replace(/[^\d.]/g, '')

    // Solo permitir un punto decimal
    const parts = cleaned.split('.')
    if (parts.length > 2) {
      cleaned = parts[0] + '.' + parts.slice(1).join('')
    }

    // Limitar a 2 decimales
    if (parts.length === 2 && parts[1].length > 2) {
      cleaned = parts[0] + '.' + parts[1].slice(0, 2)
    }

    return cleaned
  }

  // Formatear para mostrar con separadores de miles
  const formatForDisplay = (val: string): string => {
    if (!val) return ''

    const parts = val.split('.')
    const integerPart = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ',')

    if (parts.length === 2) {
      return `${integerPart}.${parts[1]}`
    }
    return integerPart
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const rawValue = e.target.value.replace(/,/g, '') // Quitar comas
    const formatted = formatDisplay(rawValue)
    const numericValue = parseFloat(formatted) || 0

    setDisplayValue(formatForDisplay(formatted))
    onChange(formatted, numericValue)
  }

  const handleBlur = () => {
    // Al salir del campo, asegurar formato correcto
    if (displayValue) {
      const numericValue = parseFloat(displayValue.replace(/,/g, '')) || 0
      const formatted = numericValue.toFixed(2)
      setDisplayValue(formatForDisplay(formatted))
      onChange(formatted, numericValue)
    }
  }

  return (
    <div>
      <label className="block text-sm font-medium text-gray-700 mb-2">Monto</label>
      <div className="relative">
        <span className="absolute left-3 top-3 text-gray-500 text-lg font-medium">$</span>
        <input
          ref={inputRef}
          type="text"
          inputMode="decimal"
          value={displayValue}
          onChange={handleChange}
          onBlur={handleBlur}
          className="input pl-8 text-lg font-semibold"
          placeholder="0.00"
        />
      </div>
      {maxAmount && displayValue && (
        <p className="mt-1 text-xs text-gray-500">
          Máximo disponible: ${maxAmount.toLocaleString('es-EC', { minimumFractionDigits: 2 })}
        </p>
      )}
      {error && (
        <p className="mt-1 text-sm text-red-600">{error}</p>
      )}
    </div>
  )
}

export default function NewTransferPage() {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const preselectedAccountId = searchParams.get('from') // ?from=accountId
  const { selectedAccount: storeSelectedAccount } = useAccountStore()

  const [isLoading, setIsLoading] = useState(false)
  const [myAccounts, setMyAccounts] = useState<Account[]>([])
  const [loadingAccounts, setLoadingAccounts] = useState(true)
  const [selectedSourceAccount, setSelectedSourceAccount] = useState<Account | null>(null)
  const [targetAccount, setTargetAccount] = useState<Account | null>(null)
  const [validatingTarget, setValidatingTarget] = useState(false)
  const [transferSuccess, setTransferSuccess] = useState(false)
  const [amountValue, setAmountValue] = useState('')
  const [amountNumeric, setAmountNumeric] = useState(0)
  const [amountError, setAmountError] = useState('')

  const {
    register,
    handleSubmit,
    formState: { errors },
    setError,
    clearErrors,
    watch,
    setValue,
  } = useForm<TransferFormData>({
    defaultValues: {
      currency: 'USD',
    },
  })

  const watchSourceAccountId = watch('sourceAccountId')

  // Cargar cuentas del usuario al montar
  useEffect(() => {
    const loadMyAccounts = async () => {
      try {
        console.log('[NewTransferPage] Loading user accounts...')
        const accounts = await accountService.getMyAccounts()
        console.log('[NewTransferPage] User accounts loaded:', accounts)
        const activeAccounts = accounts.filter(a => a.status === 'ACTIVE')
        setMyAccounts(activeAccounts)

        // Pre-seleccionar cuenta: primero URL, luego store
        if (preselectedAccountId) {
          const preselected = activeAccounts.find(a => a.id.toString() === preselectedAccountId)
          if (preselected) {
            setValue('sourceAccountId', preselected.id.toString())
            setSelectedSourceAccount(preselected)
          }
        } else if (storeSelectedAccount) {
          // Si no hay URL param, usar la cuenta seleccionada del store
          const preselected = activeAccounts.find(a => a.id === storeSelectedAccount.id)
          if (preselected) {
            setValue('sourceAccountId', preselected.id.toString())
            setSelectedSourceAccount(preselected)
          }
        }
      } catch (error) {
        console.error('[NewTransferPage] Error loading accounts:', error)
        toast.error('Error al cargar tus cuentas')
      } finally {
        setLoadingAccounts(false)
      }
    }
    loadMyAccounts()
  }, [preselectedAccountId, storeSelectedAccount, setValue])

  // Actualizar cuenta origen seleccionada
  useEffect(() => {
    if (watchSourceAccountId) {
      const account = myAccounts.find(a => a.id.toString() === watchSourceAccountId)
      setSelectedSourceAccount(account || null)
    } else {
      setSelectedSourceAccount(null)
    }
  }, [watchSourceAccountId, myAccounts])

  const validateTargetAccount = async (accountNumber: string): Promise<Account | null> => {
    if (!accountNumber || accountNumber.length < 10) {
      return null
    }

    setValidatingTarget(true)
    try {
      const account = await accountService.getByNumber(accountNumber)
      setTargetAccount(account)
      clearErrors('targetAccountNumber')

      if (account.status !== 'ACTIVE') {
        setError('targetAccountNumber', {
          message: `Cuenta ${account.status === 'BLOCKED' ? 'bloqueada' : 'inactiva'}`,
        })
        return null
      }

      // Verificar que no sea la misma cuenta
      if (selectedSourceAccount && account.id === selectedSourceAccount.id) {
        setError('targetAccountNumber', {
          message: 'No puedes transferir a la misma cuenta',
        })
        return null
      }

      return account
    } catch {
      setTargetAccount(null)
      setError('targetAccountNumber', {
        message: 'Cuenta no encontrada',
      })
      return null
    } finally {
      setValidatingTarget(false)
    }
  }

  const onSubmit = async (data: TransferFormData) => {
    setIsLoading(true)

    try {
      if (!selectedSourceAccount) {
        toast.error('Selecciona una cuenta de origen')
        setIsLoading(false)
        return
      }

      if (!targetAccount) {
        toast.error('Valida la cuenta de destino')
        setIsLoading(false)
        return
      }

      if (amountNumeric <= 0) {
        toast.error('Ingresa un monto válido')
        setAmountError('Ingresa un monto válido')
        setIsLoading(false)
        return
      }

      if (amountNumeric > selectedSourceAccount.availableBalance) {
        toast.error(`Saldo insuficiente. Disponible: $${selectedSourceAccount.availableBalance.toFixed(2)}`)
        setAmountError(`Saldo insuficiente`)
        setIsLoading(false)
        return
      }

      const transferRequest = {
        sourceAccountId: selectedSourceAccount.id,
        targetAccountId: targetAccount.id,
        amount: amountNumeric,
        currency: data.currency,
        description: data.description,
      }

      const transfer = await transferService.initiate(transferRequest)

      // Mostrar animación de éxito
      setTransferSuccess(true)
      toast.success(`Transferencia ${transfer.referenceNumber} completada`)

      // Esperar animación y navegar
      setTimeout(() => {
        navigate('/transfers')
      }, 2000)
    } catch (error: unknown) {
      const err = error as { response?: { data?: { message?: string } }, message?: string }
      toast.error(err.response?.data?.message || 'Error al procesar transferencia')
    } finally {
      setIsLoading(false)
    }
  }

  // Animación de éxito
  if (transferSuccess) {
    return (
      <motion.div
        initial={{ opacity: 0, scale: 0.8 }}
        animate={{ opacity: 1, scale: 1 }}
        className="max-w-2xl mx-auto flex flex-col items-center justify-center min-h-[400px]"
      >
        <motion.div
          initial={{ scale: 0 }}
          animate={{ scale: 1 }}
          transition={{ delay: 0.2, type: 'spring', stiffness: 200 }}
        >
          <CheckCircleIcon className="w-24 h-24 text-green-500" />
        </motion.div>
        <motion.h2
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
          className="mt-6 text-2xl font-bold text-gray-900"
        >
          ¡Transferencia Exitosa!
        </motion.h2>
        <motion.p
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.6 }}
          className="mt-2 text-gray-600"
        >
          Redirigiendo...
        </motion.p>
      </motion.div>
    )
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className="max-w-2xl mx-auto"
    >
      <div className="flex items-center gap-3 mb-8">
        <div className="p-3 bg-primary-100 rounded-xl">
          <PaperAirplaneIcon className="w-8 h-8 text-primary-600" />
        </div>
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Nueva Transferencia</h1>
          <p className="text-gray-500">Transfiere dinero de forma segura</p>
        </div>
      </div>

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.1 }}
        className="card"
      >
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          {/* Cuenta Origen - Dropdown con MIS cuentas */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              <span className="flex items-center gap-2">
                <BanknotesIcon className="w-4 h-4" />
                Cuenta Origen (Tu cuenta)
              </span>
            </label>

            {loadingAccounts ? (
              <div className="flex items-center gap-2 p-4 bg-gray-50 rounded-lg">
                <ArrowPathIcon className="w-5 h-5 animate-spin text-gray-400" />
                <span className="text-gray-500">Cargando tus cuentas...</span>
              </div>
            ) : myAccounts.length === 0 ? (
              <div className="p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
                <p className="text-yellow-700">No tienes cuentas activas asociadas</p>
              </div>
            ) : (
              <select
                {...register('sourceAccountId', { required: 'Selecciona una cuenta de origen' })}
                className="input"
              >
                <option value="">Selecciona tu cuenta</option>
                {myAccounts.map((account) => (
                  <option key={account.id} value={account.id}>
                    {account.accountNumber} - {account.accountType} - ${account.availableBalance.toFixed(2)} disponible
                  </option>
                ))}
              </select>
            )}

            {errors.sourceAccountId && (
              <p className="mt-1 text-sm text-red-600">{errors.sourceAccountId.message}</p>
            )}

            <AnimatePresence>
              {selectedSourceAccount && (
                <motion.div
                  initial={{ opacity: 0, height: 0 }}
                  animate={{ opacity: 1, height: 'auto' }}
                  exit={{ opacity: 0, height: 0 }}
                  className="mt-3 p-4 bg-gradient-to-r from-green-50 to-emerald-50 border border-green-200 rounded-xl"
                >
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm text-green-700 font-medium">Saldo disponible</p>
                      <p className="text-2xl font-bold text-green-800">
                        ${selectedSourceAccount.availableBalance.toFixed(2)}
                      </p>
                    </div>
                    <CheckCircleIcon className="w-8 h-8 text-green-500" />
                  </div>
                </motion.div>
              )}
            </AnimatePresence>
          </div>

          {/* Flecha animada */}
          <div className="flex justify-center">
            <motion.div
              animate={{ y: [0, 5, 0] }}
              transition={{ repeat: Infinity, duration: 1.5 }}
              className="p-2 bg-gray-100 rounded-full"
            >
              <ArrowRightIcon className="w-6 h-6 text-gray-400 rotate-90" />
            </motion.div>
          </div>

          {/* Cuenta Destino - Input para cualquier cuenta */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              <span className="flex items-center gap-2">
                <BanknotesIcon className="w-4 h-4" />
                Cuenta Destino
              </span>
            </label>
            <div className="relative">
              <input
                type="text"
                maxLength={10}
                {...register('targetAccountNumber', {
                  required: 'Cuenta destino requerida',
                  pattern: {
                    value: /^\d{10}$/,
                    message: 'Debe ser un número de 10 dígitos',
                  },
                })}
                onBlur={(e) => validateTargetAccount(e.target.value)}
                className="input"
                placeholder="Ingresa el número de cuenta destino"
              />
              {validatingTarget && (
                <span className="absolute right-3 top-3">
                  <ArrowPathIcon className="w-5 h-5 animate-spin text-gray-400" />
                </span>
              )}
            </div>
            {errors.targetAccountNumber && (
              <motion.p
                initial={{ opacity: 0, x: -10 }}
                animate={{ opacity: 1, x: 0 }}
                className="mt-1 text-sm text-red-600 flex items-center gap-1"
              >
                <ExclamationCircleIcon className="w-4 h-4" />
                {errors.targetAccountNumber.message}
              </motion.p>
            )}

            <AnimatePresence>
              {targetAccount && targetAccount.status === 'ACTIVE' && (
                <motion.div
                  initial={{ opacity: 0, height: 0, y: -10 }}
                  animate={{ opacity: 1, height: 'auto', y: 0 }}
                  exit={{ opacity: 0, height: 0, y: -10 }}
                  transition={{ type: 'spring', stiffness: 300, damping: 25 }}
                  className="mt-3 p-4 bg-gradient-to-r from-blue-50 to-indigo-50 border border-blue-200 rounded-xl overflow-hidden"
                >
                  <div className="flex items-start gap-4">
                    <motion.div
                      initial={{ scale: 0 }}
                      animate={{ scale: 1 }}
                      transition={{ delay: 0.1, type: 'spring', stiffness: 400 }}
                      className="p-3 bg-blue-100 rounded-full"
                    >
                      <UserCircleIcon className="w-8 h-8 text-blue-600" />
                    </motion.div>
                    <div className="flex-1 space-y-2">
                      <motion.div
                        initial={{ opacity: 0, x: -10 }}
                        animate={{ opacity: 1, x: 0 }}
                        transition={{ delay: 0.15 }}
                        className="flex items-center gap-2"
                      >
                        <CheckCircleIcon className="w-5 h-5 text-green-500" />
                        <span className="text-sm font-semibold text-green-700">Destinatario verificado</span>
                      </motion.div>

                      <motion.div
                        initial={{ opacity: 0, x: -10 }}
                        animate={{ opacity: 1, x: 0 }}
                        transition={{ delay: 0.2 }}
                      >
                        <p className="text-lg font-bold text-gray-900">{targetAccount.customerName}</p>
                      </motion.div>

                      <motion.div
                        initial={{ opacity: 0, x: -10 }}
                        animate={{ opacity: 1, x: 0 }}
                        transition={{ delay: 0.25 }}
                        className="flex items-center gap-2 text-sm text-gray-600"
                      >
                        <IdentificationIcon className="w-4 h-4" />
                        <span>{targetAccount.customerDocumentType}: {targetAccount.customerDocumentNumber}</span>
                      </motion.div>

                      <motion.div
                        initial={{ opacity: 0, x: -10 }}
                        animate={{ opacity: 1, x: 0 }}
                        transition={{ delay: 0.3 }}
                        className="flex items-center gap-4 pt-2 border-t border-blue-200 mt-2"
                      >
                        <div className="flex items-center gap-2 text-sm">
                          <CreditCardIcon className="w-4 h-4 text-blue-500" />
                          <span className="text-gray-600">Cuenta:</span>
                          <span className="font-mono font-medium text-gray-900">{targetAccount.accountNumber}</span>
                        </div>
                        <div className="px-2 py-1 bg-blue-100 rounded-full">
                          <span className="text-xs font-medium text-blue-700">{targetAccount.accountType}</span>
                        </div>
                      </motion.div>
                    </div>
                  </div>
                </motion.div>
              )}
            </AnimatePresence>
          </div>

          {/* Monto y Moneda */}
          <div className="grid grid-cols-2 gap-6">
            <AmountInput
              value={amountValue}
              onChange={(val, num) => {
                setAmountValue(val)
                setAmountNumeric(num)
                // Validar monto
                if (num <= 0) {
                  setAmountError('Ingresa un monto válido')
                } else if (num > 50000) {
                  setAmountError('Monto máximo es $50,000')
                } else if (selectedSourceAccount && num > selectedSourceAccount.availableBalance) {
                  setAmountError(`Saldo insuficiente. Disponible: $${selectedSourceAccount.availableBalance.toFixed(2)}`)
                } else {
                  setAmountError('')
                }
              }}
              error={amountError}
              maxAmount={selectedSourceAccount?.availableBalance}
            />

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Moneda</label>
              <select {...register('currency')} className="input">
                <option value="USD">USD - Dólar Americano</option>
              </select>
            </div>
          </div>

          {/* Descripción */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Descripción (opcional)
            </label>
            <textarea
              {...register('description', {
                maxLength: { value: 200, message: 'Máximo 200 caracteres' },
              })}
              rows={3}
              className="input"
              placeholder="Agrega una nota a tu transferencia..."
            />
            {errors.description && (
              <p className="mt-1 text-sm text-red-600">{errors.description.message}</p>
            )}
          </div>

          {/* Botones */}
          <div className="flex gap-4 pt-4">
            <motion.button
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              type="submit"
              className="btn-primary flex-1 flex items-center justify-center gap-2"
              disabled={isLoading || loadingAccounts}
            >
              {isLoading ? (
                <>
                  <ArrowPathIcon className="w-5 h-5 animate-spin" />
                  Procesando...
                </>
              ) : (
                <>
                  <PaperAirplaneIcon className="w-5 h-5" />
                  Realizar Transferencia
                </>
              )}
            </motion.button>
            <motion.button
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              type="button"
              onClick={() => navigate('/transfers')}
              className="btn-secondary"
            >
              Cancelar
            </motion.button>
          </div>
        </form>
      </motion.div>
    </motion.div>
  )
}
