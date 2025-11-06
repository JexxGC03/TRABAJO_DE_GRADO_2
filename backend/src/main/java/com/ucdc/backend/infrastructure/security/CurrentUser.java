package com.ucdc.backend.infrastructure.security;

import com.ucdc.backend.domain.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class CurrentUser {

    private CurrentUser() {}

    public static UUID id() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof UUID uuid) return uuid;
        if (principal instanceof String s) return UUID.fromString(s);
        if (principal instanceof User u) return u.id();

        throw new IllegalStateException("Cannot resolve current user id from principal: " + principal);
    }
}
