import { LogOut, Home } from 'lucide-react'

interface LogoutConfirmProps {
  onConfirm: () => void
  onCancel: () => void
}

export function LogoutConfirm({ onConfirm, onCancel }: LogoutConfirmProps) {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-[#E6F7FF] via-[#F0F8FF] to-[#D1EFFF]">
      <div className="w-full max-w-md bg-white/80 backdrop-blur-md rounded-3xl shadow-2xl p-12">
        {/* User Icon with Logout Arrow */}
        <div className="flex justify-center mb-8">
          <div className="relative">
            <div className="w-24 h-24 bg-gradient-to-br from-[#0089CF] to-[#0070A8] rounded-full flex items-center justify-center">
              <svg viewBox="0 0 100 100" className="w-16 h-16">
                {/* User icon */}
                <circle cx="50" cy="35" r="15" fill="white" />
                <path d="M 30 70 Q 30 55, 50 55 Q 70 55, 70 70" fill="white" />
                
                {/* Logout arrow */}
                <g transform="translate(65, 15)">
                  <path d="M 0 0 L 15 0 L 15 -5 L 25 5 L 15 15 L 15 10 L 0 10 Z" fill="#E63329" />
                </g>
              </svg>
            </div>
          </div>
        </div>

        {/* Text */}
        <h2 className="text-4xl text-[#0089CF]/90 text-center mb-8">
          ¿Quieres cerrar sesión?
        </h2>

        {/* Buttons */}
        <div className="flex gap-4">
          <button
            onClick={onConfirm}
            className="flex-1 bg-[#D1EFFF] hover:bg-[#B3D9F2] text-[#0089CF] py-3 px-6 rounded-xl transition-colors shadow-md flex items-center justify-center gap-2"
          >
            <LogOut className="w-5 h-5" />
            Cerrar
          </button>
          <button
            onClick={onCancel}
            className="flex-1 bg-slate-200 hover:bg-slate-300 text-slate-700 py-3 px-6 rounded-xl transition-colors shadow-md flex items-center justify-center gap-2"
          >
            <Home className="w-5 h-5" />
            Volver al home
          </button>
        </div>
      </div>
    </div>
  )
}
