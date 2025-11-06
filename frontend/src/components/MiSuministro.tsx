import { BarChart3, TrendingUp, Lightbulb, Calendar, BarChart, Settings } from 'lucide-react'

interface MiSuministroProps {
  onNavigate: (view: string) => void
}

export function MiSuministro({ onNavigate }: MiSuministroProps) {
  const features = [
    {
      id: 'consumo',
      icon: BarChart3,
      title: 'Consulta tu gasto energético actual y pasado.',
      description: 'Mi Consumo',
      bgColor: 'bg-secondary'
    },
    {
      id: 'proyeccion',
      icon: TrendingUp,
      title: 'Visualiza el consumo estimado según tus hábitos.',
      description: 'Proyección',
      bgColor: 'bg-secondary'
    },
    {
      id: 'datos-tecnicos',
      icon: Lightbulb,
      title: 'Accede al detalle de tu suministro.',
      description: 'Datos técnicos',
      bgColor: 'bg-secondary'
    }
  ]

  return (
    <div className="space-y-8">
      {/* Title */}
      <div className="text-center">
        <h1 className="text-4xl text-primary mb-8">Mi Suministro</h1>
      </div>

      {/* Features Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {features.map((feature) => {
          const Icon = feature.icon
          return (
            <div 
              key={feature.id} 
              onClick={() => onNavigate(feature.id)}
              className={`${feature.bgColor} backdrop-blur-sm rounded-2xl p-6 text-center hover:bg-accent transition-all cursor-pointer`}
            >
              {/* Icon */}
              <div className="flex justify-center mb-4">
                <div className="w-16 h-16 flex items-center justify-center">
                  {feature.id === 'consumo' && (
                    <div className="relative">
                      {/* Chart with bars and line */}
                      <svg viewBox="0 0 80 60" className="w-16 h-12">
                        {/* Bars */}
                        <rect x="10" y="40" width="8" height="15" fill="#10B981" />
                        <rect x="22" y="35" width="8" height="20" fill="#F59E0B" />
                        <rect x="34" y="25" width="8" height="30" fill="#EF4444" />
                        <rect x="46" y="30" width="8" height="25" fill="#8B5CF6" />
                        
                        {/* Line with dots */}
                        <circle cx="14" cy="35" r="2" fill="#1F2937" />
                        <circle cx="26" cy="30" r="2" fill="#1F2937" />
                        <circle cx="38" cy="20" r="2" fill="#1F2937" />
                        <circle cx="50" cy="25" r="2" fill="#1F2937" />
                        
                        <polyline
                          points="14,35 26,30 38,20 50,25"
                          fill="none"
                          stroke="#1F2937"
                          strokeWidth="2"
                        />
                        
                        {/* Arrow up */}
                        <path d="M60 15 L65 25 L55 25 Z" fill="#1F2937" />
                        
                        {/* Lightning bolt */}
                        <circle cx="68" cy="18" r="8" fill="#0089CF" />
                        <path d="M65 15 L68 12 L66 18 L71 18 L68 24 L70 18 L65 18 Z" fill="white" />
                      </svg>
                    </div>
                  )}
                  
                  {feature.id === 'proyeccion' && (
                    <div className="relative">
                      {/* Projection chart */}
                      <svg viewBox="0 0 80 60" className="w-16 h-12">
                        {/* Bars */}
                        <rect x="8" y="45" width="6" height="10" fill="#10B981" />
                        <rect x="16" y="40" width="6" height="15" fill="#22C55E" />
                        <rect x="24" y="35" width="6" height="20" fill="#34D399" />
                        <rect x="32" y="30" width="6" height="25" fill="#EF4444" />
                        <rect x="40" y="25" width="6" height="30" fill="#F87171" />
                        
                        {/* Arrow up trend */}
                        <path d="M50 35 L60 25" stroke="#F59E0B" strokeWidth="2" fill="none" />
                        <path d="M57 22 L63 25 L60 31 Z" fill="#F59E0B" />
                        
                        {/* Lightning indicator */}
                        <circle cx="65" cy="15" r="6" fill="#F59E0B" />
                        <path d="M63 12 L65 10 L64 15 L67 15 L65 20 L66 15 L63 15 Z" fill="white" />
                      </svg>
                    </div>
                  )}
                  
                  {feature.id === 'datos' && (
                    <Icon className="w-12 h-12 text-yellow-500" strokeWidth={1.5} />
                  )}
                </div>
              </div>

              {/* Content */}
              <div className="space-y-3">
                <p className="text-primary text-sm italic">{feature.title}</p>
                
                <button className="bg-primary text-white px-4 py-2 rounded-lg hover:opacity-90 transition-colors flex items-center justify-center gap-2 w-full">
                  {feature.id === 'consumo' && <Calendar className="w-4 h-4" />}
                  {feature.id === 'proyeccion' && <BarChart className="w-4 h-4" />}
                  {feature.id === 'datos-tecnicos' && <Settings className="w-4 h-4" />}
                  <span>{feature.description}</span>
                </button>
              </div>
            </div>
          )
        })}
      </div>
    </div>
  )
}
