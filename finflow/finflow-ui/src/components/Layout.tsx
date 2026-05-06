import { Outlet, Link, useLocation } from 'react-router-dom'
import { useAuthStore } from '../stores/authStore'
import { useThemeStore } from '../stores/themeStore'
import {
  HomeIcon,
  ArrowsRightLeftIcon,
  CreditCardIcon,
  DocumentMagnifyingGlassIcon,
  ArrowRightOnRectangleIcon,
  UserCircleIcon,
  UsersIcon,
  ChartBarIcon,
  SunIcon,
  MoonIcon,
} from '@heroicons/react/24/outline'

// Navegacion para usuarios regulares
const userNavigation = [
  { name: 'Dashboard', href: '/dashboard', icon: HomeIcon },
  { name: 'Transferencias', href: '/transfers', icon: ArrowsRightLeftIcon },
  { name: 'Cuentas', href: '/accounts', icon: CreditCardIcon },
  { name: 'Mi Actividad', href: '/audit', icon: DocumentMagnifyingGlassIcon },
  { name: 'Mi Perfil', href: '/profile', icon: UserCircleIcon },
]

// Navegacion para administradores del sistema
const adminNavigation = [
  { name: 'Estadisticas', href: '/admin', icon: ChartBarIcon },
  { name: 'Gestion Usuarios', href: '/admin?tab=users', icon: UsersIcon },
  { name: 'Gestion Cuentas', href: '/admin?tab=accounts', icon: CreditCardIcon },
  { name: 'Gestion Transferencias', href: '/admin?tab=transfers', icon: ArrowsRightLeftIcon },
  { name: 'Auditoria Sistema', href: '/audit', icon: DocumentMagnifyingGlassIcon },
  { name: 'Mi Perfil', href: '/profile', icon: UserCircleIcon },
]

export default function Layout() {
  const location = useLocation()
  const { user, logout } = useAuthStore()
  const { isDark, toggleTheme } = useThemeStore()

  const isAdmin = user?.roles.includes('ADMIN')
  const navigation = isAdmin ? adminNavigation : userNavigation

  return (
    <div className={`min-h-screen ${isAdmin ? 'theme-admin' : ''}`} style={{ backgroundColor: 'var(--neu-bg)' }}>
      {/* Sidebar */}
      <div className="sidebar">
        <div className="flex flex-col h-full">
          {/* Logo */}
          <div className="card-sm mb-6">
            <div className="flex items-center">
              <span className="text-2xl font-bold" style={{ color: 'var(--accent-600)' }}>FinFlow</span>
              {isAdmin && (
                <span className="badge-accent ml-2 text-xs">
                  Admin
                </span>
              )}
            </div>
          </div>

          {/* Navigation */}
          <nav className="flex-1 space-y-1">
            {navigation.map((item) => {
              const isActive = item.href.includes('?')
                ? location.pathname + location.search === item.href
                : location.pathname.startsWith(item.href) && !item.href.includes('?')

              return (
                <Link
                  key={item.name}
                  to={item.href}
                  className={`sidebar-item ${isActive ? 'active' : ''}`}
                >
                  <item.icon className="w-5 h-5 mr-3" style={{ color: isActive ? 'var(--accent-600)' : undefined }} />
                  {item.name}
                </Link>
              )
            })}
          </nav>

          {/* Theme toggle & User info */}
          <div className="mt-auto">
            {/* Theme Toggle */}
            <button
              onClick={toggleTheme}
              className="theme-toggle mb-4"
              title={isDark ? 'Cambiar a modo claro' : 'Cambiar a modo oscuro'}
            >
              {isDark ? (
                <SunIcon className="w-5 h-5" />
              ) : (
                <MoonIcon className="w-5 h-5" />
              )}
              <span>{isDark ? 'Modo Claro' : 'Modo Oscuro'}</span>
            </button>

            <div className="card-sm mb-3">
              <Link to="/profile" className="flex items-center gap-3 transition-opacity hover:opacity-80">
                {user?.profilePhotoUrl ? (
                  <div className="avatar-container">
                    <img
                      src={user.profilePhotoUrl}
                      alt="Profile"
                      className="w-10 h-10 rounded-full object-cover"
                    />
                  </div>
                ) : (
                  <div className="icon-container-accent">
                    <UserCircleIcon className="w-6 h-6" style={{ color: 'var(--accent-600)' }} />
                  </div>
                )}
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-900 truncate">{user?.fullName || user?.username}</p>
                  <p className="text-xs text-gray-500 truncate">
                    {isAdmin ? 'Administrador' : '@' + user?.username}
                  </p>
                </div>
              </Link>
            </div>
            <button
              onClick={logout}
              className="btn-danger w-full flex items-center justify-center gap-2 py-2.5"
            >
              <ArrowRightOnRectangleIcon className="w-5 h-5" />
              Cerrar sesion
            </button>
          </div>
        </div>
      </div>

      {/* Main content */}
      <div className="pl-64">
        <main className="p-8">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
