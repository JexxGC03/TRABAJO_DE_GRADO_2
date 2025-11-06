import React, { useState } from 'react'
import { MapPin, Bell, Settings, Menu, ChevronDown } from 'lucide-react'
import { useInmueble } from './InmuebleContext'
import { AnimatePresence, motion } from 'motion/react'


type HeaderProps = {
  onNavigate?: (view: string) => void
  darkMode?: boolean
  onToggleMobileSidebar?: () => void
}

export default function Header({ onNavigate, darkMode = false, onToggleMobileSidebar }: HeaderProps) {
  const { meters, meterSeleccionado, setMeterSeleccionado, loading } = useInmueble()
  const [isDropdownOpen, setIsDropdownOpen] = useState(false)

  const hasMeters = meters.length > 0
  const seleccionado = meterSeleccionado

  return (
    <header
      className={`sticky top-0 z-50 backdrop-blur border-b ${
        darkMode ? 'bg-[#132F4C]/50 border-[#0089CF]/20' : 'bg-white/50 border-[#0089CF]/15'
      }`}
    >
      {/* Top bar */}
      <div className="mx-auto max-w-6xl px-4 py-3 flex items-center justify-between gap-2">
        {/* Mobile menu */}
        <button
          onClick={onToggleMobileSidebar}
          className={`md:hidden w-10 h-10 flex items-center justify-center rounded-lg transition-colors ${
            darkMode ? 'hover:bg-[#1E4976]' : 'hover:bg-[#D1EFFF]'
          }`}
          aria-label="Abrir menú"
        >
          <Menu className="w-6 h-6 text-[#0089CF]" />
        </button>

        {/* Left: breadcrumb */}
        <div className="flex items-center gap-3">
          <div className="text-lg font-semibold">Panel</div>
          <span className="text-sm text-gray-500">/ Medidores</span>
        </div>

        {/* Right: meter selector + actions */}
        <div className="flex items-center gap-2 md:gap-4">
          {/* Suministro chip (decorativo, igual al mock) */}
          <div
            className={`hidden md:flex items-center gap-2 px-3 py-2 rounded-lg shadow-sm ${
              darkMode ? 'bg-[#1E4976]/70' : 'bg-white/70'
            }`}
            title="Suministro"
          >
            <MapPin className="w-4 h-4 text-[#0089CF]" />
            <span className={`text-xs ${darkMode ? 'text-[#E6F7FF]' : 'text-gray-700'}`}>Suministro</span>
          </div>

          {/* Meter dropdown */}
          <div className="relative z-[60]">
            {loading ? (
              <div
                className={`px-3 py-2 rounded-lg text-sm ${
                  darkMode ? 'bg-[#1E4976]/70 text-[#99C7E8]' : 'bg-white/70 text-gray-600'
                }`}
              >
                Cargando…
              </div>
            ) : !hasMeters ? (
              <div
                className={`px-3 py-2 rounded-lg text-sm ${
                  darkMode ? 'bg-[#1E4976]/70 text-[#99C7E8]' : 'bg-white/70 text-gray-600'
                }`}
              >
                Sin medidores
              </div>
            ) : (
              <>
                <button
                  onClick={() => setIsDropdownOpen((v) => !v)}
                  className={`flex items-center gap-2 px-3 py-2 rounded-lg transition-colors ${
                    darkMode ? 'bg-[#1E4976]/70 hover:bg-[#1E4976]' : 'bg-white/70 hover:bg-white/90'
                  }`}
                  aria-haspopup="listbox"
                  aria-expanded={isDropdownOpen}
                >
                  <div className="text-right">
                    <div className={`text-xs ${darkMode ? 'text-[#99C7E8]' : 'text-gray-600'}`}>Meter</div>
                    <div className={`font-medium text-sm ${darkMode ? 'text-[#E6F7FF]' : 'text-gray-900'}`}>
                      {seleccionado?.alias ?? 'Selecciona un medidor'}
                    </div>
                  </div>
                  <ChevronDown
                    className={`w-4 h-4 transition-transform ${
                      isDropdownOpen ? 'rotate-180' : ''
                    } ${darkMode ? 'text-[#99C7E8]' : 'text-gray-600'}`}
                  />
                </button>

                {isDropdownOpen && (
                  <>
                    {/* overlay para cerrar */}
                    <div className="fixed inset-0 z-[998]" onClick={() => setIsDropdownOpen(false)} />
                    <div
                      className={`absolute right-0 mt-2 w-80 rounded-lg shadow-xl z-[999] overflow-hidden border ${
                        darkMode ? 'bg-[#132F4C] border-[#0089CF]/30' : 'bg-white border-[#0089CF]/20'
                      }`}
                      role="listbox"
                    >
                      {meters.map((m) => {
                        const active = m.id === seleccionado?.id
                        return (
                          <button
                            key={m.id}
                            onClick={() => {
                              setMeterSeleccionado(m)
                              setIsDropdownOpen(false)
                            }}
                            className={`w-full text-left px-4 py-3 transition-colors first:rounded-t-lg last:rounded-b-lg ${
                              active
                                ? darkMode
                                  ? 'bg-[#0089CF]/20 border-l-4 border-[#0089CF]'
                                  : 'bg-[#D1EFFF] border-l-4 border-[#0089CF]'
                                : darkMode
                                ? 'hover:bg-[#1E4976]'
                                : 'hover:bg-gray-50'
                            }`}
                            role="option"
                            aria-selected={active}
                          >
                            {/* Alias */}
                            <div className={`font-medium text-sm ${darkMode ? 'text-[#E6F7FF]' : 'text-gray-900'}`}>
                              <AnimatePresence mode="wait">
                                <motion.span
                                  key={seleccionado?.id ?? 'none'}
                                  initial={{ opacity: 0, y: 4 }}
                                  animate={{ opacity: 1, y: 0 }}
                                  exit={{ opacity: 0, y: -4 }}
                                  transition={{ duration: 0.18, ease: 'easeOut' }}
                                  className="inline-block"
                                >
                                  {seleccionado?.alias ?? 'Selecciona un medidor'}
                                </motion.span>
                              </AnimatePresence>
                            </div>
                            {/* Dirección */}
                            <div className={`text-sm mt-1 ${darkMode ? 'text-[#99C7E8]' : 'text-gray-600'}`}>
                              {m.installationAddress ?? '—'}
                            </div>
                            {/* Número de servicio */}
                            <div className={`text-xs mt-1 text-[#0089CF]`}>N° Servicio: {m.serialNumber ?? '—'}</div>
                          </button>
                        )
                      })}
                    </div>
                  </>
                )}
              </>
            )}
          </div>

          {/* Botones de acción */}
          <button
            onClick={() => onNavigate?.('alertas')}
            className={`w-9 h-9 md:w-10 md:h-10 flex items-center justify-center rounded-full transition-colors ${
              darkMode ? 'hover:bg-[#1E4976]' : 'hover:bg-[#D1EFFF]'
            }`}
            aria-label="Alertas"
          >
            <Bell className="w-5 h-5 text-[#0089CF]" />
          </button>

          <button
            onClick={() => onNavigate?.('configuracion')}
            className={`w-9 h-9 md:w-10 md:h-10 flex items-center justify-center rounded-full transition-colors ${
              darkMode ? 'hover:bg-[#1E4976]' : 'hover:bg-[#D1EFFF]'
            }`}
            aria-label="Configuración"
          >
            <Settings className="w-5 h-5 text-[#0089CF]" />
          </button>
        </div>
      </div>

      {/* Center pill with alias */}
      {hasMeters && seleccionado && (
        <div className="pb-3">
          <div className="flex items-center justify-center">
            <div className={`px-4 py-1.5 rounded-lg shadow-sm ${
                darkMode ? 'bg-[#1E4976]/70 text-[#E6F7FF]' : 'bg-white/70 text-gray-700'
              }`}
              title={seleccionado.installationAddress || seleccionado.alias}
            >
              <AnimatePresence mode="wait">
                <motion.span
                  key={seleccionado?.id ?? 'none'}
                  initial={{ opacity: 0, y: 6 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: -6 }}
                  transition={{ duration: 0.2, ease: 'easeOut' }}
                  className="inline-block"
                >
                  {seleccionado.alias}
                </motion.span>
              </AnimatePresence>
            </div>
          </div>
        </div>
      )}
    </header>
  )
}
