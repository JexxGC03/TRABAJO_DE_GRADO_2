package com.ucdc.backend.application.dto.user;

public record GetMyProfileResult(
        String fullName,
        String email,
        String phone
) {
}
