package com.ucdc.backend.infrastructure.web.dto.auth;

import java.io.Serializable;

public record TokenPairResult(
        String accessToken,
        String refreshToken,
        long expiresInSeconds
) implements Serializable {
    public static TokenPairResult of(String accessToken, String refreshToken, long expiresInSeconds) {
        return new TokenPairResult(accessToken, refreshToken, expiresInSeconds);
    }
}
