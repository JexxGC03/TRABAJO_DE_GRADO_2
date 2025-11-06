import { useCallback, useEffect, useState } from "react"
import {
    getMyProfile,
    updateMyProfile,
    changeMyPassword,
    type UserProfile,
    type UpdateUserProfileRequest,
    type ChangePasswordRequest,
} from "../services/userService"

export function useUser() {
    const [user, setUser] = useState<UserProfile | null>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    const reload = useCallback(async () => {
        try {
            setLoading(true)
            setError(null)
            const me = await getMyProfile()
            setUser(me)
        } catch (e: any) {
            setError(e?.message || "Error cargando usuario")
        } finally {
            setLoading(false)
        }
    }, [])

    useEffect(() => {
        reload()
    }, [reload])

    const updateProfile = useCallback(async (payload: UpdateUserProfileRequest) => {
        const updated = await updateMyProfile(payload)
        setUser(updated)
        return updated
    }, [])

    const changePassword = useCallback(async (payload: ChangePasswordRequest) => {
        await changeMyPassword(payload)
    }, [])

    return { user, loading, error, reload, updateProfile, changePassword }
}