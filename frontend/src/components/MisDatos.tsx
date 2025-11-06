"use client"

import { useEffect, useMemo, useState } from "react"
import { motion } from "motion/react"
import { User, Mail, Phone, Lock, Edit, Save, ArrowLeft, Eye, EyeOff } from "lucide-react"
import { useUser } from "../hooks/useUser"
import { Breadcrumbs } from "./Breadcrumbs"

interface MisDatosProps {
    onNavigate?: (view: string) => void
}

export default function MisDatos({ onNavigate }: MisDatosProps) {
    const { user, loading, error, updateProfile, changePassword } = useUser()

    // Edición de perfil
    const [isEditing, setIsEditing] = useState(false)
    const [formData, setFormData] = useState({
        nombre: "",
        apellido: "",
        email: "",
        telefono: "",
    })

    // Cambio de contraseña
    const [isEditingPassword, setIsEditingPassword] = useState(false)
    const [passwordData, setPasswordData] = useState({
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
    })
    const [showCurrentPassword, setShowCurrentPassword] = useState(false)
    const [showNewPassword, setShowNewPassword] = useState(false)
    const [showConfirmPassword, setShowConfirmPassword] = useState(false)

    // Estados UI simples de feedback (puedes reemplazar con tu sistema de toasts)
    const [saving, setSaving] = useState(false)
    const [savingPwd, setSavingPwd] = useState(false)
    const [message, setMessage] = useState<string | null>(null)
    const [messageType, setMessageType] = useState<"success" | "error" | null>(null)

    // Carga inicial de datos al llegar el usuario
    useEffect(() => {
        if (user) {
            setFormData({
                nombre: user.firstName ?? "",
                apellido: user.lastName ?? "",
                email: user.email ?? "",
                telefono: user.phone ?? "",
            })
        }
    }, [user])

    const canSave = useMemo(() => {
        if (!isEditing) return false
        const { nombre, apellido, email } = formData
        return !!nombre.trim() && !!apellido.trim() && !!email.trim()
    }, [isEditing, formData])

    const canSavePassword = useMemo(() => {
        const { currentPassword, newPassword, confirmPassword } = passwordData
        if (!currentPassword || !newPassword || !confirmPassword) return false
        if (newPassword !== confirmPassword) return false
        if (newPassword.length < 8) return false // validación simple
        return true
    }, [passwordData])

    const handleSave = async () => {
        if (!canSave) return
        try {
            setSaving(true)
            await updateProfile({
                firstName: formData.nombre.trim(),
                lastName: formData.apellido.trim(),
                email: formData.email.trim(),
                phone: formData.telefono.trim(),
            })
            setIsEditing(false)
            setMessage("Perfil actualizado correctamente.")
            setMessageType("success")
        } catch (e: any) {
            setMessage(e?.message || "No se pudo actualizar el perfil.")
            setMessageType("error")
        } finally {
            setSaving(false)
            setTimeout(() => {
                setMessage(null)
                setMessageType(null)
            }, 3000)
        }
    }

    const handleSavePassword = async () => {
        if (!canSavePassword) return
        try {
            setSavingPwd(true)
            await changePassword({
                currentPassword: passwordData.currentPassword,
                newPassword: passwordData.newPassword,
            })
            setIsEditingPassword(false)
            setPasswordData({ currentPassword: "", newPassword: "", confirmPassword: "" })
            setMessage("Contraseña actualizada correctamente.")
            setMessageType("success")
        } catch (e: any) {
            setMessage(e?.message || "No se pudo actualizar la contraseña.")
            setMessageType("error")
        } finally {
            setSavingPwd(false)
            setTimeout(() => {
                setMessage(null)
                setMessageType(null)
            }, 3000)
        }
    }

    if (loading) {
        return (
            <div className="max-w-4xl mx-auto p-8 text-gray-600">
                Cargando tu información…
            </div>
        )
    }

    if (error) {
        return (
            <div className="max-w-4xl mx-auto p-8 text-red-700">
                {error}
            </div>
        )
    }

    return (
        <motion.div
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.28 }}
            className="max-w-4xl mx-auto space-y-4"
        >
            {/* Breadcrumbs opcionales */}
            {onNavigate && (
                <Breadcrumbs
                    items={[
                        { label: "Configuración", onClick: () => onNavigate("configuracion") },
                        { label: "Privacidad y seguridad" },
                        { label: "Mis Datos" },
                    ]}
                />
            )}

            {/* Header */}
            <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
                <div className="flex items-center gap-2 md:gap-3">
                    <div className="w-8 h-8 md:w-10 md:h-10 bg-primary rounded-full flex items-center justify-center">
                        <User className="w-4 h-4 md:w-5 md:h-5 text-white" />
                    </div>
                </div>

                {onNavigate && (
                    <button
                        onClick={() => onNavigate("configuracion")}
                        className="flex items-center gap-2 px-3 py-2 md:px-4 md:py-2 bg-secondary hover:bg-accent text-primary rounded-lg transition-colors text-sm md:text-base"
                    >
                        <ArrowLeft className="w-4 h-4" />
                        Volver
                    </button>
                )}
            </div>

            {/* Mensajes simples */}
            {message && (
                <div
                    className={`rounded-xl px-4 py-3 text-sm ${
                        messageType === "success"
                            ? "bg-green-50 text-green-700 border border-green-200"
                            : "bg-red-50 text-red-700 border border-red-200"
                    }`}
                >
                    {message}
                </div>
            )}

            {/* Card de Perfil */}
            <div className="bg-white/60 backdrop-blur-sm rounded-2xl p-4 md:p-8 shadow-md">
                <div className="flex flex-col sm:flex-row items-center sm:items-start justify-between gap-4 md:gap-6 mb-6 md:mb-8 pb-4 md:pb-6 border-b border-primary/20">
                    <div className="flex flex-col sm:flex-row items-center gap-4 md:gap-6 w-full sm:w-auto">
                        <div className="relative">
                            <div className="w-20 h-20 md:w-24 md:h-24 bg-gradient-to-br from-primary to-primary/80 rounded-full flex items-center justify-center shadow-lg">
                                <User className="w-10 h-10 md:w-12 md:h-12 text-white" />
                            </div>
                        </div>
                        <div className="text-center sm:text-left">
                            <h1 className="text-2xl md:text-3xl text-primary">
                                {formData.nombre} {formData.apellido}
                            </h1>
                            <p className="text-sm md:text-base text-gray-600">{formData.email}</p>
                        </div>
                    </div>

                    {!isEditing && (
                        <button
                            onClick={() => setIsEditing(true)}
                            className="flex items-center gap-2 px-3 py-2 md:px-4 md:py-2 bg-primary text-white rounded-lg hover:opacity-90 transition-colors text-sm md:text-base w-full sm:w-auto justify-center"
                        >
                            <Edit className="w-4 h-4" />
                            Cambiar Datos
                        </button>
                    )}
                </div>

                <div className="space-y-6">
                    <h3 className="text-xl text-primary mb-4">Información Personal</h3>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        {/* Nombre */}
                        <div className="space-y-2">
                            <label className="flex items-center gap-2 text-sm text-gray-700">
                                <User className="w-4 h-4 text-primary" />
                                Nombre
                            </label>
                            <input
                                type="text"
                                value={formData.nombre}
                                onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
                                disabled={!isEditing}
                                className={`w-full px-4 py-3 bg-accent border border-primary/20 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary ${
                                    !isEditing ? "opacity-60 cursor-not-allowed" : ""
                                }`}
                            />
                        </div>

                        {/* Apellido */}
                        <div className="space-y-2">
                            <label className="flex items-center gap-2 text-sm text-gray-700">
                                <User className="w-4 h-4 text-primary" />
                                Apellido
                            </label>
                            <input
                                type="text"
                                value={formData.apellido}
                                onChange={(e) => setFormData({ ...formData, apellido: e.target.value })}
                                disabled={!isEditing}
                                className={`w-full px-4 py-3 bg-accent border border-primary/20 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary ${
                                    !isEditing ? "opacity-60 cursor-not-allowed" : ""
                                }`}
                            />
                        </div>

                        {/* Email */}
                        <div className="space-y-2">
                            <label className="flex items-center gap-2 text-sm text-gray-700">
                                <Mail className="w-4 h-4 text-primary" />
                                Correo electrónico
                            </label>
                            <input
                                type="email"
                                value={formData.email}
                                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                                disabled={!isEditing}
                                className={`w-full px-4 py-3 bg-accent border border-primary/20 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary ${
                                    !isEditing ? "opacity-60 cursor-not-allowed" : ""
                                }`}
                            />
                        </div>

                        {/* Teléfono */}
                        <div className="space-y-2">
                            <label className="flex items-center gap-2 text-sm text-gray-700">
                                <Phone className="w-4 h-4 text-primary" />
                                Teléfono
                            </label>
                            <input
                                type="tel"
                                value={formData.telefono}
                                onChange={(e) => setFormData({ ...formData, telefono: e.target.value })}
                                disabled={!isEditing}
                                className={`w-full px-4 py-3 bg-accent border border-primary/20 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary ${
                                    !isEditing ? "opacity-60 cursor-not-allowed" : ""
                                }`}
                            />
                        </div>
                    </div>

                    {isEditing && (
                        <div className="flex flex-col sm:flex-row gap-3 md:gap-4 pt-4">
                            <button
                                onClick={handleSave}
                                disabled={!canSave || saving}
                                className="flex-1 flex items-center justify-center gap-2 px-4 py-2 md:px-6 md:py-3 bg-primary text-white rounded-lg hover:opacity-90 transition-colors shadow-md disabled:opacity-50"
                            >
                                <Save className="w-4 h-4 md:w-5 md:h-5" />
                                {saving ? "Guardando..." : "Guardar Cambios"}
                            </button>
                            <button
                                onClick={() => {
                                    // restaurar valores originales
                                    if (user) {
                                        setFormData({
                                            nombre: user.firstName ?? "",
                                            apellido: user.lastName ?? "",
                                            email: user.email ?? "",
                                            telefono: user.phone ?? "",
                                        })
                                    }
                                    setIsEditing(false)
                                }}
                                className="px-4 py-2 md:px-6 md:py-3 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
                            >
                                Cancelar
                            </button>
                        </div>
                    )}
                </div>
            </div>

            {/* Seguridad / Cambio de contraseña */}
            <div className="bg-white/60 backdrop-blur-sm rounded-2xl p-4 md:p-8 shadow-md">
                <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 mb-6 pb-4 border-b border-primary/20">
                    <div className="flex items-center gap-3">
                        <div className="w-8 h-8 md:w-10 md:h-10 bg-secondary rounded-full flex items-center justify-center">
                            <Lock className="w-4 h-4 md:w-5 md:h-5 text-primary" />
                        </div>
                        <div>
                            <h3 className="text-lg md:text-xl text-primary">Seguridad</h3>
                            <p className="text-xs md:text-sm text-gray-600">Cambiar contraseña</p>
                        </div>
                    </div>
                    {!isEditingPassword && (
                        <button
                            onClick={() => setIsEditingPassword(true)}
                            className="flex items-center gap-2 px-3 py-2 md:px-4 md:py-2 bg-primary text-white rounded-lg hover:opacity-90 transition-colors text-sm md:text-base w-full sm:w-auto justify-center"
                        >
                            <Lock className="w-4 h-4" />
                            Cambiar Contraseña
                        </button>
                    )}
                </div>

                {isEditingPassword ? (
                    <div className="space-y-6">
                        {/* Contraseña actual */}
                        <div className="space-y-2">
                            <label className="flex items-center gap-2 text-sm text-gray-700">
                                <Lock className="w-4 h-4 text-primary" />
                                Contraseña Actual
                            </label>
                            <div className="relative">
                                <input
                                    type={showCurrentPassword ? "text" : "password"}
                                    value={passwordData.currentPassword}
                                    onChange={(e) =>
                                        setPasswordData({ ...passwordData, currentPassword: e.target.value })
                                    }
                                    className="w-full px-4 py-3 pr-10 bg-accent border border-primary/20 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
                                />
                                <button
                                    type="button"
                                    className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
                                    onClick={() => setShowCurrentPassword((v) => !v)}
                                >
                                    {showCurrentPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                </button>
                            </div>
                        </div>

                        {/* Nueva contraseña */}
                        <div className="space-y-2">
                            <label className="flex items-center gap-2 text-sm text-gray-700">
                                <Lock className="w-4 h-4 text-primary" />
                                Nueva Contraseña
                            </label>
                            <div className="relative">
                                <input
                                    type={showNewPassword ? "text" : "password"}
                                    value={passwordData.newPassword}
                                    onChange={(e) =>
                                        setPasswordData({ ...passwordData, newPassword: e.target.value })
                                    }
                                    className="w-full px-4 py-3 pr-10 bg-accent border border-primary/20 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
                                />
                                <button
                                    type="button"
                                    className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
                                    onClick={() => setShowNewPassword((v) => !v)}
                                >
                                    {showNewPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                </button>
                            </div>
                            <p className="text-xs text-gray-500">Mínimo 8 caracteres.</p>
                        </div>

                        {/* Confirmación */}
                        <div className="space-y-2">
                            <label className="flex items-center gap-2 text-sm text-gray-700">
                                <Lock className="w-4 h-4 text-primary" />
                                Confirmar Nueva Contraseña
                            </label>
                            <div className="relative">
                                <input
                                    type={showConfirmPassword ? "text" : "password"}
                                    value={passwordData.confirmPassword}
                                    onChange={(e) =>
                                        setPasswordData({ ...passwordData, confirmPassword: e.target.value })
                                    }
                                    className="w-full px-4 py-3 pr-10 bg-accent border border-primary/20 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
                                />
                                <button
                                    type="button"
                                    className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
                                    onClick={() => setShowConfirmPassword((v) => !v)}
                                >
                                    {showConfirmPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                </button>
                            </div>
                            {passwordData.confirmPassword &&
                                passwordData.newPassword !== passwordData.confirmPassword && (
                                    <p className="text-xs text-red-600">Las contraseñas no coinciden.</p>
                                )}
                        </div>

                        <div className="flex flex-col sm:flex-row gap-3 md:gap-4 pt-2">
                            <button
                                onClick={handleSavePassword}
                                disabled={!canSavePassword || savingPwd}
                                className="flex-1 flex items-center justify-center gap-2 px-4 py-2 md:px-6 md:py-3 bg-primary text-white rounded-lg hover:opacity-90 transition-colors shadow-md disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                <Save className="w-4 h-4 md:w-5 md:h-5" />
                                {savingPwd ? "Actualizando..." : "Actualizar Contraseña"}
                            </button>
                            <button
                                onClick={() => {
                                    setIsEditingPassword(false)
                                    setPasswordData({ currentPassword: "", newPassword: "", confirmPassword: "" })
                                }}
                                className="px-4 py-2 md:px-6 md:py-3 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
                            >
                                Cancelar
                            </button>
                        </div>
                    </div>
                ) : (
                    <p className="text-sm text-gray-600">
                        Para mayor seguridad, te recomendamos cambiar tu contraseña periódicamente.
                    </p>
                )}
            </div>
        </motion.div>
    )
}
