import { useState } from 'react'
import { Sidebar } from './components/Sidebar'
import Header from './components/Header'
import { Dashboard } from './components/Dashboard'
import { MiConsumo } from './components/MiConsumo'
import { MisDatos } from './components/MisDatos'
import { MiContador } from './components/MiContador'
import { Proyeccion } from './components/Proyeccion'
import { DatosTecnicos } from './components/DatosTecnicos'
import { Alertas } from './components/Alertas'
import { Configuracion } from './components/Configuracion'
import { Login } from './components/Login'
import { SignUp } from './components/SignUp'
import { LogoutConfirm } from './components/LogoutConfirm'
import { InmuebleProvider } from './components/InmuebleContext'
import { AuthProvider, useAuth } from './components/AuthContext'
import { GestionInmuebles } from './components/GestionInmuebles'


function AppContent() {
  const { isAuthenticated, logout, loading } = useAuth()
  const [showSignUp, setShowSignUp] = useState(false)
  const [showLogoutConfirm, setShowLogoutConfirm] = useState(false)
  const [currentView, setCurrentView] = useState('dashboard')
  const [darkMode, setDarkMode] = useState(false)
  const [isMobileSidebarOpen, setIsMobileSidebarOpen] = useState(false)

  const toggleDarkMode = () => {
    setDarkMode(!darkMode)
  }

  const toggleMobileSidebar = () => {
    setIsMobileSidebarOpen(!isMobileSidebarOpen)
  }

  const closeMobileSidebar = () => {
    setIsMobileSidebarOpen(false)
  }

  const handleLogin = () => {
    setShowSignUp(false)
    setCurrentView('dashboard')
  }

  const handleSignUp = () => {
    setShowSignUp(false)
    setCurrentView('dashboard')
  }

  const handleLogoutRequest = () => {
    setShowLogoutConfirm(true)
  }

  const handleLogoutConfirm = () => {
    logout()
    setShowLogoutConfirm(false)
    setCurrentView('dashboard')
  }

  const handleLogoutCancel = () => {
    setShowLogoutConfirm(false)
    setCurrentView('dashboard')
  }

  const renderCurrentView = () => {
    switch (currentView) {
      case 'perfil':
        return <MisDatos onNavigate={setCurrentView} />
      case 'consumo':
        return <MiConsumo onNavigate={setCurrentView} />
      case 'contador':
        return <MiContador onNavigate={setCurrentView} />
      case 'proyeccion':
        return <Proyeccion onNavigate={setCurrentView} />
      case 'datos-tecnicos':
        return <DatosTecnicos onNavigate={setCurrentView} />
      case 'alertas':
        return <Alertas />
      case 'configuracion':
        return <Configuracion onNavigate={setCurrentView} darkMode={darkMode} onToggleDarkMode={toggleDarkMode} />
      case 'gestion-inmuebles':
        return <GestionInmuebles onNavigate={setCurrentView} />
      default:
        return <Dashboard onNavigate={setCurrentView} />
    }
  }

  // Mostrar loading mientras verifica autenticación
  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-[#E6F7FF] via-[#F0F8FF] to-[#D1EFFF] flex items-center justify-center">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-[#0089CF] border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
          <p className="text-[#0089CF]">Cargando...</p>
        </div>
      </div>
    )
  }

  // Show Login/SignUp screens if not authenticated
  if (!isAuthenticated) {
    if (showSignUp) {
      return <SignUp onSignUp={handleSignUp} onBack={() => setShowSignUp(false)} />
    }
    return <Login onLogin={handleLogin} onSignUp={() => setShowSignUp(true)} />
  }

  // Show logout confirmation
  if (showLogoutConfirm) {
    return <LogoutConfirm onConfirm={handleLogoutConfirm} onCancel={handleLogoutCancel} />
  }

  const handleNavigate = (view: string) => {
    setCurrentView(view)
    closeMobileSidebar()
  }

  return (
    <InmuebleProvider>
      <div className={`min-h-screen transition-colors duration-300 ${
        darkMode 
          ? 'bg-gradient-to-br from-[#0A1929] via-[#0D2137] to-[#132F4C]' 
          : 'bg-gradient-to-br from-[#E6F7FF] via-[#F0F8FF] to-[#D1EFFF]'
      }`}>
        <div className="flex flex-col md:flex-row min-h-screen">
          {/* Mobile Sidebar Overlay */}
          {isMobileSidebarOpen && (
            <div 
              className="fixed inset-0 bg-black/50 z-40 md:hidden"
              onClick={closeMobileSidebar}
            />
          )}
          
          {/* Sidebar */}
          <Sidebar 
            currentView={currentView} 
            onNavigate={handleNavigate} 
            onLogout={handleLogoutRequest} 
            darkMode={darkMode}
            isMobileOpen={isMobileSidebarOpen}
            onCloseMobile={closeMobileSidebar}
          />
          
          <div className="flex-1 flex flex-col min-h-screen">
            <Header 
              onNavigate={setCurrentView} 
              darkMode={darkMode}
              onToggleMobileSidebar={toggleMobileSidebar}
            />
            <main className="flex-1 p-4 md:p-6">
              <div className={darkMode ? 'dark-mode' : ''}>
                {renderCurrentView()}
              </div>
            </main>
          </div>
        </div>
      </div>
    </InmuebleProvider>
  )
}

export default function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  )
}
