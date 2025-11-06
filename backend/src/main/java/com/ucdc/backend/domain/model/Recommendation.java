package com.ucdc.backend.domain.model;

import com.ucdc.backend.domain.enums.RecommendationStatus;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public final class Recommendation {

    private final UUID id;
    private final UUID userId;

    private String message; // 1..n (valida no blank y longitud razonable)
    private RecommendationStatus status;

    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private Recommendation(UUID id,
                           UUID userId,
                           String message,
                           RecommendationStatus status,
                           OffsetDateTime createdAt,
                           OffsetDateTime updatedAt) {
        this.id = requireNonNull(id, "id");
        this.userId = requireNonNull(userId, "userId");
        this.message = requireMessage(message);
        this.status = requireNonNull(status, "status");
        this.createdAt = createdAt != null ? createdAt : OffsetDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
    }

    public static Recommendation create(UUID id, UUID userId, String message) {
        return new Recommendation(id, userId, message, RecommendationStatus.ACTIVE, null, null);
    }

    public static Recommendation rehydrate(UUID id, UUID userId, String message,
                                           RecommendationStatus status,
                                           OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        return new Recommendation(id, userId, message, status, createdAt, updatedAt);
    }

    /* Reglas */

    public void updateMessage(String newMessage) {
        this.message = requireMessage(newMessage);
        touch();
    }

    public void archive() {
        if (this.status == RecommendationStatus.ARCHIVED) return;
        this.status = RecommendationStatus.ARCHIVED;
        touch();
    }

    public void activate() {
        if (this.status == RecommendationStatus.ACTIVE) return;
        this.status = RecommendationStatus.ACTIVE;
        touch();
    }

    /* Getters */

    public UUID id() { return id; }
    public UUID userId() { return userId; }
    public String message() { return message; }
    public RecommendationStatus status() { return status; }
    public OffsetDateTime createdAt() { return createdAt; }
    public OffsetDateTime updatedAt() { return updatedAt; }

    /* Utils */

    private void touch() { this.updatedAt = OffsetDateTime.now(); }

    private static <T> T requireNonNull(T v, String name) {
        return Objects.requireNonNull(v, name + " is required");
    }
    private static String requireMessage(String s) {
        if (s == null) throw new NullPointerException("message is required");
        String t = s.trim();
        if (t.isEmpty()) throw new IllegalArgumentException("message cannot be blank");
        if (t.length() > 500) throw new IllegalArgumentException("message too long (max 500)");
        return t;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recommendation other)) return false;
        return id.equals(other.id);
    }
    @Override public int hashCode() { return id.hashCode(); }
    @Override public String toString() {
        return "Recommendation{id=%s, userId=%s, status=%s}".formatted(id, userId, status);
    }
}
