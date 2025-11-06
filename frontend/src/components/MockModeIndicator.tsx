import { AlertCircle, X } from 'lucide-react'
import { useState } from 'react'

export function MockModeIndicator() {
  const [isVisible, setIsVisible] = useState(true)

  if (!isVisible) return null

  return (
    <div className="fixed bottom-4 right-4 bg-gradient-to-r from-yellow-500 to-yellow-600 text-white px-4 py-3 rounded-lg shadow-lg z-50 max-w-sm animate-pulse-slow">
      <div className="flex items-start gap-3">
        <AlertCircle className="w-5 h-5 flex-shrink-0 mt-0.5" />
        <div className="flex-1">
          <div className="font-semibold text-sm mb-1">✨ Modo Desarrollo</div>
          <div className="text-xs opacity-90 mb-2">
            Datos de prueba (sin backend necesario)
          </div>
          <div className="text-xs opacity-90 bg-white/20 rounded px-2 py-1">
            💡 Ver /MODO_DESARROLLO.md para detalles
          </div>
        </div>
        <button
          onClick={() => setIsVisible(false)}
          className="text-white/70 hover:text-white transition-colors"
          aria-label="Cerrar notificación"
        >
          <X className="w-4 h-4" />
        </button>
      </div>
    </div>
  )
}
