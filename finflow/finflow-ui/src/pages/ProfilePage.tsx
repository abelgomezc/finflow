import { useState, useEffect, useRef } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import toast from 'react-hot-toast'
import { useAuthStore } from '../stores/authStore'
import { userService, UserProfile } from '../services/userService'
import {
  UserCircleIcon,
  CameraIcon,
  PencilIcon,
  CheckIcon,
  XMarkIcon,
  IdentificationIcon,
  EnvelopeIcon,
  UserIcon,
  ShieldCheckIcon,
  ArrowPathIcon,
} from '@heroicons/react/24/outline'

export default function ProfilePage() {
  const { user, updateProfilePhoto } = useAuthStore()
  const [profile, setProfile] = useState<UserProfile | null>(null)
  const [loading, setLoading] = useState(true)
  const [uploading, setUploading] = useState(false)
  const [editing, setEditing] = useState(false)
  const [editData, setEditData] = useState({ fullName: '', email: '' })
  const fileInputRef = useRef<HTMLInputElement>(null)

  useEffect(() => {
    loadProfile()
  }, [])

  const loadProfile = async () => {
    try {
      const data = await userService.getMyProfile()
      setProfile(data)
      setEditData({ fullName: data.fullName, email: data.email })
    } catch (error) {
      console.error('Error loading profile:', error)
      toast.error('Error al cargar el perfil')
    } finally {
      setLoading(false)
    }
  }

  const handlePhotoClick = () => {
    fileInputRef.current?.click()
  }

  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (!file) return

    // Validar tipo
    if (!file.type.startsWith('image/')) {
      toast.error('Solo se permiten imágenes')
      return
    }

    // Validar tamaño (5MB)
    if (file.size > 5 * 1024 * 1024) {
      toast.error('La imagen debe ser menor a 5MB')
      return
    }

    setUploading(true)
    try {
      const result = await userService.uploadProfilePhoto(file)
      if (result.success) {
        toast.success('Foto actualizada')
        updateProfilePhoto(result.profilePhotoUrl)
        setProfile((prev) => prev ? { ...prev, profilePhotoUrl: result.profilePhotoUrl } : null)
      }
    } catch (error) {
      console.error('Error uploading photo:', error)
      toast.error('Error al subir la foto')
    } finally {
      setUploading(false)
    }
  }

  const handleSaveProfile = async () => {
    try {
      const result = await userService.updateProfile(editData)
      if (result.success) {
        toast.success('Perfil actualizado')
        setProfile((prev) => prev ? { ...prev, ...editData } : null)
        setEditing(false)
      }
    } catch (error) {
      console.error('Error updating profile:', error)
      toast.error('Error al actualizar el perfil')
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <ArrowPathIcon className="w-8 h-8 animate-spin text-primary-500" />
      </div>
    )
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className="max-w-4xl mx-auto"
    >
      <h1 className="text-2xl font-bold text-gray-900 mb-8">Mi Perfil</h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Foto de perfil */}
        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          className="card flex flex-col items-center py-8"
        >
          <div className="relative group">
            <motion.div
              whileHover={{ scale: 1.05 }}
              className="w-32 h-32 rounded-full overflow-hidden bg-gray-200 cursor-pointer"
              onClick={handlePhotoClick}
            >
              {profile?.profilePhotoUrl ? (
                <img
                  src={profile.profilePhotoUrl}
                  alt="Profile"
                  className="w-full h-full object-cover"
                />
              ) : (
                <UserCircleIcon className="w-full h-full text-gray-400" />
              )}

              {/* Overlay al hacer hover */}
              <div className="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                {uploading ? (
                  <ArrowPathIcon className="w-8 h-8 text-white animate-spin" />
                ) : (
                  <CameraIcon className="w-8 h-8 text-white" />
                )}
              </div>
            </motion.div>

            <input
              ref={fileInputRef}
              type="file"
              accept="image/*"
              onChange={handleFileChange}
              className="hidden"
            />
          </div>

          <h2 className="mt-4 text-xl font-semibold text-gray-900">
            {profile?.fullName || user?.fullName}
          </h2>
          <p className="text-gray-500">@{profile?.username || user?.username}</p>

          <div className="mt-4 px-3 py-1 bg-primary-100 rounded-full">
            <span className="text-sm font-medium text-primary-700">
              {profile?.role || user?.roles[0]}
            </span>
          </div>

          <p className="mt-4 text-xs text-gray-400 text-center">
            Haz clic en la foto para cambiarla
          </p>
        </motion.div>

        {/* Información del usuario */}
        <motion.div
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ delay: 0.1 }}
          className="card md:col-span-2"
        >
          <div className="flex items-center justify-between mb-6">
            <h3 className="text-lg font-semibold text-gray-900">Información Personal</h3>
            {!editing ? (
              <button
                onClick={() => setEditing(true)}
                className="flex items-center gap-2 text-primary-600 hover:text-primary-700"
              >
                <PencilIcon className="w-4 h-4" />
                Editar
              </button>
            ) : (
              <div className="flex gap-2">
                <button
                  onClick={handleSaveProfile}
                  className="flex items-center gap-1 px-3 py-1 bg-green-100 text-green-700 rounded-lg hover:bg-green-200"
                >
                  <CheckIcon className="w-4 h-4" />
                  Guardar
                </button>
                <button
                  onClick={() => {
                    setEditing(false)
                    setEditData({ fullName: profile?.fullName || '', email: profile?.email || '' })
                  }}
                  className="flex items-center gap-1 px-3 py-1 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200"
                >
                  <XMarkIcon className="w-4 h-4" />
                  Cancelar
                </button>
              </div>
            )}
          </div>

          <div className="space-y-4">
            <div className="flex items-center gap-4 p-4 bg-gray-50 rounded-lg">
              <UserIcon className="w-5 h-5 text-gray-400" />
              <div className="flex-1">
                <p className="text-sm text-gray-500">Nombre completo</p>
                <AnimatePresence mode="wait">
                  {editing ? (
                    <motion.input
                      key="edit"
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      exit={{ opacity: 0 }}
                      type="text"
                      value={editData.fullName}
                      onChange={(e) => setEditData({ ...editData, fullName: e.target.value })}
                      className="input mt-1"
                    />
                  ) : (
                    <motion.p
                      key="view"
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      exit={{ opacity: 0 }}
                      className="font-medium text-gray-900"
                    >
                      {profile?.fullName}
                    </motion.p>
                  )}
                </AnimatePresence>
              </div>
            </div>

            <div className="flex items-center gap-4 p-4 bg-gray-50 rounded-lg">
              <EnvelopeIcon className="w-5 h-5 text-gray-400" />
              <div className="flex-1">
                <p className="text-sm text-gray-500">Email</p>
                <AnimatePresence mode="wait">
                  {editing ? (
                    <motion.input
                      key="edit"
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      exit={{ opacity: 0 }}
                      type="email"
                      value={editData.email}
                      onChange={(e) => setEditData({ ...editData, email: e.target.value })}
                      className="input mt-1"
                    />
                  ) : (
                    <motion.p
                      key="view"
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      exit={{ opacity: 0 }}
                      className="font-medium text-gray-900"
                    >
                      {profile?.email}
                    </motion.p>
                  )}
                </AnimatePresence>
              </div>
            </div>

            {profile?.documentType && profile?.documentNumber && (
              <motion.div
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.2 }}
                className="flex items-center gap-4 p-4 bg-blue-50 rounded-lg"
              >
                <IdentificationIcon className="w-5 h-5 text-blue-500" />
                <div>
                  <p className="text-sm text-blue-600">Identificación</p>
                  <p className="font-medium text-blue-900">
                    {profile.documentType}: {profile.documentNumber}
                  </p>
                </div>
              </motion.div>
            )}

            <div className="flex items-center gap-4 p-4 bg-green-50 rounded-lg">
              <ShieldCheckIcon className="w-5 h-5 text-green-500" />
              <div>
                <p className="text-sm text-green-600">Rol</p>
                <p className="font-medium text-green-900">{profile?.role}</p>
              </div>
            </div>
          </div>
        </motion.div>
      </div>
    </motion.div>
  )
}
