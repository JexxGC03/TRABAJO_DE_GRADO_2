package com.ucdc.backend.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.tokens")
public class AuthTokensProperties {
    /** ej: 900 (15 min) */
    private int accessTtlSeconds = 900;
    /** ej: 2592000 (30 días) */
    private int refreshTtlSeconds = 2_592_000;

    public int getAccessTtlSeconds() { return accessTtlSeconds; }
    public void setAccessTtlSeconds(int v) { this.accessTtlSeconds = v; }
    public int getRefreshTtlSeconds() { return refreshTtlSeconds; }
    public void setRefreshTtlSeconds(int v) { this.refreshTtlSeconds = v; }
}
