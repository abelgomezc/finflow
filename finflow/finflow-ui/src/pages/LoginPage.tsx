import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { motion } from 'framer-motion'
import toast from 'react-hot-toast'
import { useAuthStore } from '../stores/authStore'
import { authService, LoginRequest } from '../services/authService'
import { BanknotesIcon, ArrowPathIcon } from '@heroicons/react/24/outline'
import AnimatedBackground from '../components/AnimatedBackground'

export default function LoginPage() {
  const navigate = useNavigate()
  const login = useAuthStore((state) => state.login)
  const [isLoading, setIsLoading] = useState(false)

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginRequest>()

  const onSubmit = async (data: LoginRequest) => {
    setIsLoading(true)
    try {
      const response = await authService.login(data)
      if (response.success) {
        login(response.token, {
          userId: response.userId,
          username: response.username,
          fullName: response.fullName || response.username,
          roles: response.roles,
          profilePhotoUrl: response.profilePhotoUrl,
        })
        toast.success('Bienvenido a FinFlow')
        navigate('/dashboard')
      } else {
        toast.error(response.message || 'Credenciales invalidas')
      }
    } catch {
      toast.error('Error al iniciar sesion')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center p-4 relative overflow-hidden">
      <AnimatedBackground />
      <div className="w-full max-w-md relative z-10">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
          className="card"
        >
          {/* Logo */}
          <div className="text-center mb-8">
            <div className="icon-container mx-auto w-fit mb-4">
              <BanknotesIcon className="w-12 h-12" style={{ color: 'var(--accent-600)' }} />
            </div>
            <h1 className="text-3xl font-bold text-gray-900">FinFlow</h1>
            <p className="text-gray-500 mt-2">Sistema de Transferencias Bancarias</p>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            <div>
              <label htmlFor="username" className="block text-sm font-medium text-gray-700 mb-2">
                Usuario
              </label>
              <input
                type="text"
                id="username"
                {...register('username', { required: 'Usuario requerido' })}
                className="input"
                placeholder="Ingrese su usuario"
              />
              {errors.username && (
                <p className="mt-2 text-sm text-red-600">{errors.username.message}</p>
              )}
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-2">
                Contrasena
              </label>
              <input
                type="password"
                id="password"
                {...register('password', { required: 'Contrasena requerida' })}
                className="input"
                placeholder="Ingrese su contrasena"
              />
              {errors.password && (
                <p className="mt-2 text-sm text-red-600">{errors.password.message}</p>
              )}
            </div>

            <motion.button
              type="submit"
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              className="btn-accent w-full flex items-center justify-center gap-2"
              disabled={isLoading}
            >
              {isLoading ? (
                <>
                  <ArrowPathIcon className="w-5 h-5 animate-spin" />
                  Iniciando sesion...
                </>
              ) : (
                'Iniciar Sesion'
              )}
            </motion.button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-gray-600">
              No tienes cuenta?{' '}
              <Link to="/register" className="font-medium hover:opacity-80 transition-opacity" style={{ color: 'var(--accent-600)' }}>
                Registrate aqui
              </Link>
            </p>
          </div>

          <div className="mt-6 card-inset">
            <p className="text-xs text-gray-500 text-center">
              <strong>Usuarios de prueba:</strong><br />
              admin / finflow123 (Administrador)<br />
              asgomez / finflow123 (Usuario)
            </p>
          </div>
        </motion.div>

        {/* Footer */}
        <p className="text-center text-white/50 text-sm mt-6">
          © 2026 Abel Gomez. Todos los derechos reservados.
        </p>
      </div>
    </div>
  )
}
