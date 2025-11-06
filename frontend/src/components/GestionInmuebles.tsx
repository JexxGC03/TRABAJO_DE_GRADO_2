import { Zap, Plus, Edit, Trash2, ArrowLeft, Save, X } from 'lucide-react'
import { motion } from 'motion/react'
import { Breadcrumbs } from './Breadcrumbs'
import { useState } from 'react'
import { useInmueble } from './InmuebleContext'
import { createMeter, updateMeter, deleteMeter } from '../services/meterService'
import type { Meter } from './InmuebleContext'

interface GestionInmueblesProps {
  onNavigate?: (view: string) => void
}

export function GestionInmuebles({ onNavigate }: GestionInmueblesProps) {
  const { meters, agregarMeter, eliminarMeter, actualizarMeter, recargarMeters } = useInmueble()
  const [mostrarFormulario, setMostrarFormulario] = useState(false)
  const [meterEditando, setMeterEditando] = useState<Meter | null>(null)
  const [guardando, setGuardando] = useState(false)
  const [formulario, setFormulario] = useState({
    serialNumber: '',
    provider: 'ENEL',
    serviceNumber: '',
    installationAddress: '',
    alias: ''
  })

  const handleAgregarNuevo = () => {
    setFormulario({
      serialNumber: '',
      provider: 'ENEL',
      serviceNumber: '',
      installationAddress: '',
      alias: ''
    })
    setMeterEditando(null)
    setMostrarFormulario(true)
  }

  const handleEditar = (meter: Meter) => {
    setFormulario({
      serialNumber: meter.serialNumber,
      provider: meter.provider,
      serviceNumber: meter.serviceNumber,
      installationAddress: meter.installationAddress,
      alias: meter.alias
    })
    setMeterEditando(meter)
    setMostrarFormulario(true)
  }

  const handleGuardar = async () => {
    setGuardando(true)
    
    try {
      // Datos completos para la API (incluye status y type por defecto)
      const datosCompletos = {
        ...formulario,
        status: meterEditando ? meterEditando.status : ('ACTIVE' as const),
        type: meterEditando ? meterEditando.type : ('SMART' as const)
      }

      if (meterEditando) {
        // Actualizar contador existente - llamada al API
        const actualizado = await updateMeter(meterEditando.id, datosCompletos)
        
        if (actualizado) {
          // Actualizar en el contexto local
          actualizarMeter(meterEditando.id, datosCompletos)
          console.log('✅ Contador actualizado:', actualizado)
        }
      } else {
        // Agregar nuevo contador - llamada al API (POST)
        const nuevo = await createMeter(datosCompletos)
        
        if (nuevo) {
          console.log('✅ Contador creado:', nuevo)
          // Recargar todos los meters desde el backend
          await recargarMeters()
        }
      }
      
      setMostrarFormulario(false)
      setMeterEditando(null)
    } catch (error) {
      console.error('❌ Error guardando contador:', error)
      alert('Error al guardar el contador. Por favor intenta nuevamente.')
    } finally {
      setGuardando(false)
    }
  }

  const handleEliminar = async (id: string) => {
    if (meters.length <= 1) {
      alert('No puedes eliminar el último contador')
      return
    }
    
    if (!confirm('¿Estás seguro de eliminar este contador?')) {
      return
    }
    
    try {
      // Eliminar en el API
      await deleteMeter(id)
      
      console.log('✅ Contador eliminado')
      
      // Recargar todos los meters desde el backend
      await recargarMeters()
    } catch (error) {
      console.error('❌ Error eliminando contador:', error)
      alert('Error al eliminar el contador. Por favor intenta nuevamente.')
    }
  }

  const handleCancelar = () => {
    setMostrarFormulario(false)
    setMeterEditando(null)
  }

  return (
    <motion.div 
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
      className="max-w-4xl mx-auto space-y-4"
    >
      {/* Breadcrumbs */}
      {onNavigate && (
        <Breadcrumbs 
          items={[
            { label: 'Configuración', onClick: () => onNavigate('configuracion') },
            { label: 'Gestión de Contadores' }
          ]} 
        />
      )}

      {/* Header with Back Button */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
        <div className="flex items-center gap-2 md:gap-3">
          <div className="w-8 h-8 md:w-10 md:h-10 bg-primary rounded-full flex items-center justify-center">
            <Zap className="w-4 h-4 md:w-5 md:h-5 text-white" />
          </div>
          <h1 className="text-2xl md:text-3xl text-primary">Gestión de Contadores</h1>
        </div>
        {onNavigate && (
          <button
            onClick={() => onNavigate('configuracion')}
            className="flex items-center gap-2 px-3 py-2 md:px-4 md:py-2 bg-secondary hover:bg-accent text-primary rounded-lg transition-colors text-sm md:text-base"
          >
            <ArrowLeft className="w-4 h-4" />
            Volver
          </button>
        )}
      </div>

      {/* Formulario de Agregar/Editar */}
      {mostrarFormulario && (
        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          className="bg-white/60 backdrop-blur-sm rounded-2xl p-4 md:p-6 shadow-md"
        >
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-xl text-primary">
              {meterEditando ? 'Editar Contador' : 'Agregar Nuevo Contador'}
            </h2>
            <button
              onClick={handleCancelar}
              className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
            >
              <X className="w-5 h-5 text-gray-500" />
            </button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Alias */}
            <div className="space-y-2">
              <label className="text-sm text-gray-700">Alias *</label>
              <input
                type="text"
                value={formulario.alias}
                onChange={(e) => setFormulario({...formulario, alias: e.target.value})}
                placeholder="Ej: Apartamento Centro"
                className="w-full px-4 py-3 bg-accent border border-primary/20 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
              />
            </div>

            {/* Serial Number */}
            <div className="space-y-2">
              <label className="text-sm text-gray-700">Número de Serie *</label>
              <input
                type="text"
                value={formulario.serialNumber}
                onChange={(e) => setFormulario({...formulario, serialNumber: e.target.value})}
                placeholder="Ej: SNR-ENE-00123"
                className="w-full px-4 py-3 bg-accent border border-primary/20 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
              />
            </div>

            {/* Service Number */}
            <div className="space-y-2">
              <label className="text-sm text-gray-700">Número de Servicio *</label>
              <input
                type="text"
                value={formulario.serviceNumber}
                onChange={(e) => setFormulario({...formulario, serviceNumber: e.target.value})}
                placeholder="Ej: 1234565"
                className="w-full px-4 py-3 bg-accent border border-primary/20 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
              />
            </div>

            {/* Provider */}
            <div className="space-y-2">
              <label className="text-sm text-gray-700">Proveedor *</label>
              <select
                value={formulario.provider}
                onChange={(e) => setFormulario({...formulario, provider: e.target.value})}
                className="w-full px-4 py-3 bg-accent border border-primary/20 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
              >
                <option value="ENEL">ENEL</option>
                <option value="EPM">EPM</option>
                <option value="CODENSA">CODENSA</option>
                <option value="OTRO">Otro</option>
              </select>
            </div>

            {/* Installation Address */}
            <div className="space-y-2 md:col-span-2">
              <label className="text-sm text-gray-700">Dirección de Instalación *</label>
              <input
                type="text"
                value={formulario.installationAddress}
                onChange={(e) => setFormulario({...formulario, installationAddress: e.target.value})}
                placeholder="Ej: Cr 72 I n 42 f 83 sur conjunto Alejandra 1"
                className="w-full px-4 py-3 bg-accent border border-primary/20 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
              />
            </div>
          </div>

          {/* Botones de Acción */}
          <div className="flex flex-col sm:flex-row gap-3 mt-6">
            <button
              onClick={handleGuardar}
              disabled={
                guardando || 
                !formulario.alias || 
                !formulario.serialNumber || 
                !formulario.serviceNumber || 
                !formulario.installationAddress ||
                !formulario.provider
              }
              className="flex-1 flex items-center justify-center gap-2 px-4 py-3 bg-primary text-white rounded-lg hover:opacity-90 transition-colors shadow-md disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <Save className="w-5 h-5" />
              {guardando ? 'Guardando...' : (meterEditando ? 'Actualizar' : 'Guardar')}
            </button>
            <button
              onClick={handleCancelar}
              disabled={guardando}
              className="px-4 py-3 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors disabled:opacity-50"
            >
              Cancelar
            </button>
          </div>
        </motion.div>
      )}

      {/* Botón Agregar Nuevo */}
      {!mostrarFormulario && (
        <button
          onClick={handleAgregarNuevo}
          className="w-full flex items-center justify-center gap-2 px-4 py-3 bg-primary text-white rounded-lg hover:opacity-90 transition-colors shadow-md"
        >
          <Plus className="w-5 h-5" />
          Agregar Nuevo Contador
        </button>
      )}

      {/* Lista de Contadores */}
      <div className="space-y-3">
        <h2 className="text-xl text-primary">Mis Contadores ({meters.length})</h2>
        
        {meters.map((meter) => (
          <motion.div
            key={meter.id}
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            className="bg-white/60 backdrop-blur-sm rounded-2xl p-4 md:p-5 shadow-md"
          >
            <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
              <div className="flex items-start gap-3 flex-1">
                <div className="w-12 h-12 bg-secondary rounded-full flex items-center justify-center flex-shrink-0">
                  <Zap className="w-6 h-6 text-primary" />
                </div>
                <div className="flex-1 min-w-0">
                  <h3 className="text-lg text-gray-900 truncate">{meter.alias}</h3>
                  <p className="text-sm text-gray-600 break-words">{meter.installationAddress}</p>
                  <div className="flex flex-wrap gap-2 mt-2">
                    <span className="text-xs px-2 py-1 bg-secondary text-primary rounded-full">
                      N° Servicio: {meter.serviceNumber}
                    </span>
                    <span className="text-xs px-2 py-1 bg-secondary text-primary rounded-full">
                      Serial: {meter.serialNumber}
                    </span>
                    <span className="text-xs px-2 py-1 bg-secondary text-primary rounded-full">
                      {meter.provider}
                    </span>
                    <span className={`text-xs px-2 py-1 rounded-full ${
                      meter.status === 'ACTIVE' 
                        ? 'bg-green-100 text-green-700' 
                        : meter.status === 'INACTIVE'
                        ? 'bg-gray-100 text-gray-700'
                        : 'bg-yellow-100 text-yellow-700'
                    }`}>
                      {meter.status === 'ACTIVE' ? 'Activo' : meter.status === 'INACTIVE' ? 'Inactivo' : 'Mantenimiento'}
                    </span>
                    <span className="text-xs px-2 py-1 bg-blue-100 text-blue-700 rounded-full">
                      {meter.type === 'SMART' ? 'Inteligente' : meter.type === 'DIGITAL' ? 'Digital' : 'Análogo'}
                    </span>
                  </div>
                </div>
              </div>

              {/* Botones de Acción */}
              <div className="flex gap-2 w-full sm:w-auto">
                <button
                  onClick={() => handleEditar(meter)}
                  className="flex-1 sm:flex-initial flex items-center justify-center gap-2 px-3 py-2 bg-secondary hover:bg-accent text-primary rounded-lg transition-colors"
                >
                  <Edit className="w-4 h-4" />
                  <span className="sm:hidden">Editar</span>
                </button>
                <button
                  onClick={() => handleEliminar(meter.id)}
                  disabled={meters.length === 1}
                  className="flex-1 sm:flex-initial flex items-center justify-center gap-2 px-3 py-2 bg-red-100 hover:bg-red-200 text-red-700 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <Trash2 className="w-4 h-4" />
                  <span className="sm:hidden">Eliminar</span>
                </button>
              </div>
            </div>
          </motion.div>
        ))}
      </div>

      {/* Info */}
      <div className="bg-accent border border-primary/30 rounded-lg p-4">
        <p className="text-sm text-primary">
          <strong>Nota:</strong> Al menos un contador debe permanecer en la cuenta. Los datos de consumo y alertas se calcularán automáticamente para cada contador.
        </p>
      </div>
    </motion.div>
  )
}
