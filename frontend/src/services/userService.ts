import { api } from "./api" // ajusta la ruta a tu wrapper

/** ===== Tipos que expone el backend ===== */
export type ProfileBackend = {
    id: string
    fullName: string
    email: string
    phone: string
}

/** ===== Tipos para el front ===== */
export type UserProfile = {
    id: string
    firstName: string
    lastName: string
    email: string
    phone: string
}

export type UpdateUserProfileRequest = {
    firstName: string
    lastName: string
    email: string
    phone: string
}

export type ChangePasswordRequest = {
    currentPassword: string
    newPassword: string
}

/** ===== Utilidades de mapeo ===== */
function splitFullName(fullName: string): { firstName: string; lastName: string } {
    const clean = (fullName || "").trim().replace(/\s+/g, " ")
    if (!clean) return { firstName: "", lastName: "" }
    const parts = clean.split(" ")
    if (parts.length === 1) return { firstName: parts[0], lastName: "" }
    const lastName = parts.pop()!
    const firstName = parts.join(" ")
    return { firstName, lastName }
}

function toFront(b: ProfileBackend): UserProfile {
    const { firstName, lastName } = splitFullName(b.fullName)
    return { id: b.id, firstName, lastName, email: b.email, phone: b.phone }
}

function toBackendFullName(p: UpdateUserProfileRequest): { fullName: string; email: string; phone: string } {
    const fullName = [p.firstName, p.lastName].filter(Boolean).join(" ").trim()
    return { fullName, email: p.email, phone: p.phone }
}

/** ===== Endpoints estilo { data, error } ===== */

/** === Mi perfil === GET /api/users/me */
export async function getMyProfile(): Promise<UserProfile> {
    const { data, error } = await api.get<ProfileBackend>("/users/me")
    if (error) throw new Error(error)
    return toFront(data as ProfileBackend)
}

/** === Actualizar perfil === PUT /api/users/me (body: { fullName, email, phone }) */
export async function updateMyProfile(payload: UpdateUserProfileRequest): Promise<UserProfile> {
    const body = toBackendFullName(payload)
    const { data, error } = await api.put<ProfileBackend>("/users/me", body)
    if (error) throw new Error(error)
    return toFront(data as ProfileBackend)
}

/** === Cambiar contraseña === POST /api/users/change-password */
export async function changeMyPassword(payload: ChangePasswordRequest): Promise<void> {
    const { error } = await api.post<void>("/users/change-password", payload)
    if (error) throw new Error(error)
}

/** (Opcional) === Eliminar cuenta === DELETE /api/users/me */
export async function deleteMyAccount(): Promise<void> {
    const { error } = await api.delete<void>("/users/me")
    if (error) throw new Error(error)
}