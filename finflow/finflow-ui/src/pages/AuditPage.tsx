import { useState, useEffect } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import toast from 'react-hot-toast'
import { activityService, UserActivity, activityTypeConfig } from '../services/activityService'
import {
  MagnifyingGlassIcon,
  ArrowPathIcon,
  ArrowRightOnRectangleIcon,
  ArrowLeftOnRectangleIcon,
  ExclamationTriangleIcon,
  PaperAirplaneIcon,
  CheckCircleIcon,
  XCircleIcon,
  UserIcon,
  KeyIcon,
  EyeIcon,
  ClockIcon,
  ComputerDesktopIcon,
  ShieldCheckIcon,
} from '@heroicons/react/24/outline'

// Mapeo de nombres de iconos a componentes
const iconMap: Record<string, typeof CheckCircleIcon> = {
  ArrowRightOnRectangleIcon,
  ArrowLeftOnRectangleIcon,
  ExclamationTriangleIcon,
  PaperAirplaneIcon,
  CheckCircleIcon,
  XCircleIcon,
  UserIcon,
  KeyIcon,
  EyeIcon,
}

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  return date.toLocaleString('es-EC', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function formatRelativeTime(dateString: string): string {
  const date = new Date(dateString)
  const now = new Date()
  const diffMs = now.getTime() - date.getTime()
  const diffMinutes = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMs / 3600000)
  const diffDays = Math.floor(diffMs / 86400000)

  if (diffMinutes < 1) return 'Ahora mismo'
  if (diffMinutes < 60) return `Hace ${diffMinutes} min`
  if (diffHours < 24) return `Hace ${diffHours} hora${diffHours > 1 ? 's' : ''}`
  if (diffDays < 7) return `Hace ${diffDays} día${diffDays > 1 ? 's' : ''}`
  return formatDate(dateString)
}

export default function AuditPage() {
  const [activities, setActivities] = useState<UserActivity[]>([])
  const [loading, setLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState('')
  const [filterType, setFilterType] = useState('')
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)

  useEffect(() => {
    loadActivities()
  }, [page])

  const loadActivities = async () => {
    try {
      setLoading(true)
      const response = await activityService.getMyActivity(page, 20)
      setActivities(response.content)
      setTotalPages(response.totalPages)
      setTotalElements(response.totalElements)
    } catch (error) {
      console.error('Error loading activities:', error)
      toast.error('Error al cargar actividad')
    } finally {
      setLoading(false)
    }
  }

  // Filtrar actividades
  const filteredActivities = activities.filter((activity) => {
    const matchesSearch =
      searchTerm === '' ||
      activity.activityType.toLowerCase().includes(searchTerm.toLowerCase()) ||
      activity.description?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      activity.entityId?.toLowerCase().includes(searchTerm.toLowerCase())

    const matchesType = filterType === '' || activity.activityType === filterType

    return matchesSearch && matchesType
  })

  // Obtener tipos únicos para el filtro
  const activityTypes = [...new Set(activities.map((a) => a.activityType))]

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="p-3 bg-primary-100 rounded-xl">
            <ShieldCheckIcon className="w-8 h-8 text-primary-600" />
          </div>
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Mi Actividad</h1>
            <p className="text-sm text-gray-500">
              {totalElements} evento{totalElements !== 1 ? 's' : ''} registrado{totalElements !== 1 ? 's' : ''}
            </p>
          </div>
        </div>
        <motion.button
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          onClick={loadActivities}
          disabled={loading}
          className="btn-secondary flex items-center gap-2"
        >
          <ArrowPathIcon className={`w-5 h-5 ${loading ? 'animate-spin' : ''}`} />
          Actualizar
        </motion.button>
      </div>

      {/* Filtros */}
      <div className="card">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="relative md:col-span-2">
            <MagnifyingGlassIcon className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar por descripción o referencia..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="input pl-10"
            />
          </div>
          <select
            value={filterType}
            onChange={(e) => setFilterType(e.target.value)}
            className="input"
          >
            <option value="">Todos los eventos</option>
            {activityTypes.map((type) => (
              <option key={type} value={type}>
                {activityTypeConfig[type]?.label || type}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Loading */}
      {loading && activities.length === 0 && (
        <div className="card flex items-center justify-center py-12">
          <ArrowPathIcon className="w-8 h-8 animate-spin text-primary-500" />
          <span className="ml-3 text-gray-500">Cargando actividad...</span>
        </div>
      )}

      {/* Empty state */}
      {!loading && activities.length === 0 && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="card flex flex-col items-center justify-center py-12"
        >
          <ClockIcon className="w-16 h-16 text-gray-300 mb-4" />
          <h3 className="text-lg font-medium text-gray-900">Sin actividad</h3>
          <p className="text-gray-500 mt-1">Tu historial de actividad aparecerá aquí</p>
        </motion.div>
      )}

      {/* Timeline de actividades */}
      {activities.length > 0 && (
        <div className="card">
          <div className="space-y-1">
            <AnimatePresence>
              {filteredActivities.map((activity, index) => {
                const config = activityTypeConfig[activity.activityType] || {
                  label: activity.activityType,
                  color: 'bg-gray-100 text-gray-800',
                  icon: 'CheckCircleIcon',
                  description: '',
                }
                const IconComponent = iconMap[config.icon] || CheckCircleIcon

                return (
                  <motion.div
                    key={activity.id}
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: index * 0.05 }}
                    className="flex items-start gap-4 p-4 rounded-xl hover:bg-gray-50 transition-colors"
                  >
                    {/* Icono */}
                    <div className={`p-2.5 rounded-xl ${config.color.split(' ')[0]}`}>
                      <IconComponent className={`w-5 h-5 ${config.color.split(' ')[1]}`} />
                    </div>

                    {/* Contenido */}
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2 flex-wrap">
                        <span className={`inline-flex px-2.5 py-0.5 text-xs font-medium rounded-full ${config.color}`}>
                          {config.label}
                        </span>
                        {activity.entityId && (
                          <span className="text-xs font-mono text-gray-500">
                            {activity.entityId}
                          </span>
                        )}
                      </div>
                      <p className="mt-1 text-sm text-gray-700">{activity.description}</p>
                      <div className="mt-2 flex items-center gap-4 text-xs text-gray-500">
                        <span className="flex items-center gap-1">
                          <ClockIcon className="w-3.5 h-3.5" />
                          {formatRelativeTime(activity.createdAt)}
                        </span>
                        {activity.ipAddress && (
                          <span className="flex items-center gap-1">
                            <ComputerDesktopIcon className="w-3.5 h-3.5" />
                            {activity.ipAddress}
                          </span>
                        )}
                      </div>
                    </div>

                    {/* Fecha completa */}
                    <div className="hidden md:block text-right">
                      <p className="text-sm text-gray-600">{formatDate(activity.createdAt)}</p>
                    </div>
                  </motion.div>
                )
              })}
            </AnimatePresence>
          </div>

          {/* Paginación */}
          {totalPages > 1 && (
            <div className="mt-6 pt-4 border-t flex items-center justify-between">
              <div className="text-sm text-gray-500">
                Página {page + 1} de {totalPages}
              </div>
              <div className="flex gap-2">
                <button
                  onClick={() => setPage(Math.max(0, page - 1))}
                  disabled={page === 0}
                  className="px-3 py-1.5 text-sm border rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-100"
                >
                  Anterior
                </button>
                <button
                  onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
                  disabled={page >= totalPages - 1}
                  className="px-3 py-1.5 text-sm border rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-100"
                >
                  Siguiente
                </button>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
