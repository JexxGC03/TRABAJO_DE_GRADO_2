import { User, Home, Bell, LogOut, ChevronLeft, ChevronRight, X } from 'lucide-react'
import { useState } from 'react'

interface SidebarProps {
  currentView: string
  onNavigate: (view: string) => void
  onLogout: () => void
  darkMode?: boolean
  isMobileOpen?: boolean
  onCloseMobile?: () => void
}

export function Sidebar({ currentView, onNavigate, onLogout, darkMode, isMobileOpen, onCloseMobile }: SidebarProps) {
  const [isExpanded, setIsExpanded] = useState(true)

  const menuItems = [
    { id: 'dashboard', label: 'Home', icon: Home },
    { id: 'alertas', label: 'Alertas', icon: Bell },
  ]

  return (
    <>
      {/* Desktop Sidebar */}
      <div className={`hidden md:flex md:flex-col ${isExpanded ? 'w-56' : 'w-20'} ${
        darkMode 
          ? 'bg-[#0A1929]/70 border-[#0089CF]/20' 
          : 'bg-white/70 border-[#0089CF]/15'
      } backdrop-blur-sm border-r p-4 space-y-2 transition-all duration-300 relative`}>
        {/* Toggle Button */}
        <button
          onClick={() => setIsExpanded(!isExpanded)}
          className="absolute -right-3 top-8 w-6 h-6 bg-[#0089CF] rounded-full flex items-center justify-center text-white hover:bg-[#0070A8] transition-colors shadow-md z-10"
        >
          {isExpanded ? <ChevronLeft className="w-4 h-4" /> : <ChevronRight className="w-4 h-4" />}
        </button>

        {/* User Avatar */}
        <div className="flex items-center justify-center mb-6">
          <div className={`${isExpanded ? 'w-16 h-16' : 'w-12 h-12'} bg-gradient-to-br from-[#0089CF] to-[#0070A8] rounded-full flex items-center justify-center shadow-lg transition-all duration-300`}>
            <User className={`${isExpanded ? 'w-8 h-8' : 'w-6 h-6'} text-white transition-all duration-300`} />
          </div>
        </div>

        {/* User Name (only when expanded) */}
        {isExpanded && (
          <div className="text-center mb-4 px-2">
            <p className={`text-sm truncate ${darkMode ? 'text-[#99C7E8]' : 'text-[#0089CF]'}`}>Usuario</p>
          </div>
        )}

        {/* Menu Items */}
        {menuItems.map((item) => {
          const Icon = item.icon
          const isActive = currentView === item.id || 
                          (currentView === 'consumo' && item.id === 'dashboard') ||
                          (currentView === 'contador' && item.id === 'dashboard') ||
                          (currentView === 'proyeccion' && item.id === 'dashboard') ||
                          (currentView === 'datos-tecnicos' && item.id === 'dashboard')
          
          return (
            <button
              key={item.id}
              onClick={() => onNavigate(item.id)}
              className={`w-full flex items-center ${isExpanded ? 'gap-3 px-4' : 'justify-center px-2'} py-3 rounded-xl transition-all ${
                isActive 
                  ? 'bg-[#0089CF] text-white shadow-md' 
                  : darkMode
                    ? 'text-[#99C7E8] hover:bg-[#1E4976]'
                    : 'text-[#0089CF] hover:bg-[#D1EFFF]'
              }`}
              title={!isExpanded ? item.label : ''}
            >
              <Icon className="w-5 h-5 flex-shrink-0" />
              {isExpanded && <span className="truncate">{item.label}</span>}
            </button>
          )
        })}

        {/* Separator */}
        <div className={`my-4 border-t ${darkMode ? 'border-[#0089CF]/20' : 'border-[#0089CF]/15'}`}></div>

        {/* Logout */}
        <button 
          onClick={onLogout}
          className={`w-full flex items-center ${isExpanded ? 'gap-3 px-4' : 'justify-center px-2'} py-3 rounded-xl transition-all ${
            darkMode 
              ? 'text-[#99C7E8] hover:bg-[#1E4976]' 
              : 'text-[#0089CF] hover:bg-[#D1EFFF]'
          }`}
          title={!isExpanded ? 'Cerrar Sesión' : ''}
        >
          <LogOut className="w-5 h-5 flex-shrink-0" />
          {isExpanded && <span className="truncate">Cerrar Sesión</span>}
        </button>
      </div>

      {/* Mobile Sidebar */}
      <div className={`md:hidden fixed inset-y-0 left-0 z-[45] w-64 ${
        darkMode 
          ? 'bg-[#0A1929]/95 border-[#0089CF]/20' 
          : 'bg-white/95 border-[#0089CF]/15'
      } backdrop-blur-md border-r p-4 space-y-2 transition-transform duration-300 ${
        isMobileOpen ? 'translate-x-0' : '-translate-x-full'
      }`}>
        {/* Close Button */}
        <button
          onClick={onCloseMobile}
          className={`absolute top-4 right-4 w-8 h-8 flex items-center justify-center rounded-full transition-colors ${
            darkMode ? 'hover:bg-[#1E4976]' : 'hover:bg-[#D1EFFF]'
          }`}
        >
          <X className={`w-5 h-5 ${darkMode ? 'text-[#99C7E8]' : 'text-[#0089CF]'}`} />
        </button>

        {/* User Avatar */}
        <div className="flex items-center justify-center mb-6 mt-8">
          <div className="w-16 h-16 bg-gradient-to-br from-[#0089CF] to-[#0070A8] rounded-full flex items-center justify-center shadow-lg">
            <User className="w-8 h-8 text-white" />
          </div>
        </div>

        {/* User Name */}
        <div className="text-center mb-4 px-2">
          <p className={`text-sm truncate ${darkMode ? 'text-[#99C7E8]' : 'text-[#0089CF]'}`}>Usuario</p>
        </div>

        {/* Navigation */}
        <nav className="space-y-2">
          {menuItems.map((item) => {
            const Icon = item.icon
            const isActive = currentView === item.id
            return (
              <button
                key={item.id}
                onClick={() => onNavigate(item.id)}
                className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all ${
                  isActive 
                    ? 'bg-[#0089CF] text-white shadow-md' 
                    : darkMode
                      ? 'text-[#99C7E8] hover:bg-[#1E4976]'
                      : 'text-[#0089CF] hover:bg-[#D1EFFF]'
                }`}
              >
                <Icon className="w-5 h-5" />
                <span>{item.label}</span>
              </button>
            )
          })}
        </nav>

        {/* Separator */}
        <div className={`my-4 border-t ${darkMode ? 'border-[#0089CF]/20' : 'border-[#0089CF]/15'}`}></div>

        {/* Logout */}
        <button 
          onClick={onLogout}
          className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all ${
            darkMode 
              ? 'text-[#99C7E8] hover:bg-[#1E4976]' 
              : 'text-[#0089CF] hover:bg-[#D1EFFF]'
          }`}
        >
          <LogOut className="w-5 h-5" />
          <span>Cerrar Sesión</span>
        </button>
      </div>

      {/* Mobile Overlay */}
      {isMobileOpen && (
        <div 
          className="md:hidden fixed inset-0 bg-black/50 z-40"
          onClick={onCloseMobile}
        />
      )}
    </>
  )
}
