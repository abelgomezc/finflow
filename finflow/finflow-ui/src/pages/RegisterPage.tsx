import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { motion } from 'framer-motion'
import toast from 'react-hot-toast'
import { authService, RegisterRequest } from '../services/authService'
import { UserPlusIcon, ArrowPathIcon } from '@heroicons/react/24/outline'
import AnimatedBackground from '../components/AnimatedBackground'

export default function RegisterPage() {
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<RegisterRequest & { confirmPassword: string }>()

  const password = watch('password')

  const onSubmit = async (data: RegisterRequest & { confirmPassword: string }) => {
    setIsLoading(true)
    try {
      const response = await authService.register({
        username: data.username,
        password: data.password,
        email: data.email,
        fullName: data.fullName,
      })

      if (response.success) {
        toast.success('Cuenta creada exitosamente')
        navigate('/login')
      } else {
        toast.error(response.message || 'Error al crear cuenta')
      }
    } catch (error: unknown) {
      const err = error as { response?: { data?: { message?: string } } }
      toast.error(err.response?.data?.message || 'Error al registrar usuario')
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
              <UserPlusIcon className="w-12 h-12" style={{ color: 'var(--accent-600)' }} />
            </div>
            <h1 className="text-3xl font-bold text-gray-900">FinFlow</h1>
            <p className="text-gray-500 mt-2">Crear Nueva Cuenta</p>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
            <div>
              <label htmlFor="fullName" className="block text-sm font-medium text-gray-700 mb-2">
                Nombre Completo
              </label>
              <input
                type="text"
                id="fullName"
                {...register('fullName', {
                  required: 'Nombre completo es requerido',
                  minLength: { value: 2, message: 'Minimo 2 caracteres' },
                })}
                className="input"
                placeholder="Juan Perez"
              />
              {errors.fullName && (
                <p className="mt-2 text-sm text-red-600">{errors.fullName.message}</p>
              )}
            </div>

            <div>
              <label htmlFor="username" className="block text-sm font-medium text-gray-700 mb-2">
                Usuario
              </label>
              <input
                type="text"
                id="username"
                {...register('username', {
                  required: 'Usuario es requerido',
                  minLength: { value: 3, message: 'Minimo 3 caracteres' },
                  maxLength: { value: 50, message: 'Maximo 50 caracteres' },
                  pattern: {
                    value: /^[a-zA-Z0-9_]+$/,
                    message: 'Solo letras, numeros y guion bajo',
                  },
                })}
                className="input"
                placeholder="juanperez"
              />
              {errors.username && (
                <p className="mt-2 text-sm text-red-600">{errors.username.message}</p>
              )}
            </div>

            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-2">
                Correo Electronico
              </label>
              <input
                type="email"
                id="email"
                {...register('email', {
                  required: 'Email es requerido',
                  pattern: {
                    value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                    message: 'Email invalido',
                  },
                })}
                className="input"
                placeholder="juan@ejemplo.com"
              />
              {errors.email && (
                <p className="mt-2 text-sm text-red-600">{errors.email.message}</p>
              )}
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-2">
                Contrasena
              </label>
              <input
                type="password"
                id="password"
                {...register('password', {
                  required: 'Contrasena es requerida',
                  minLength: { value: 6, message: 'Minimo 6 caracteres' },
                })}
                className="input"
                placeholder="Minimo 6 caracteres"
              />
              {errors.password && (
                <p className="mt-2 text-sm text-red-600">{errors.password.message}</p>
              )}
            </div>

            <div>
              <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700 mb-2">
                Confirmar Contrasena
              </label>
              <input
                type="password"
                id="confirmPassword"
                {...register('confirmPassword', {
                  required: 'Confirme su contrasena',
                  validate: (value) => value === password || 'Las contrasenas no coinciden',
                })}
                className="input"
                placeholder="Repita su contrasena"
              />
              {errors.confirmPassword && (
                <p className="mt-2 text-sm text-red-600">{errors.confirmPassword.message}</p>
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
                  Creando cuenta...
                </>
              ) : (
                'Crear Cuenta'
              )}
            </motion.button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-gray-600">
              Ya tienes cuenta?{' '}
              <Link to="/login" className="font-medium hover:opacity-80 transition-opacity" style={{ color: 'var(--accent-600)' }}>
                Iniciar Sesion
              </Link>
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
