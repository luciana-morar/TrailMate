package com.trailmate.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Route {

    private final UUID id;
    private String name;
    private String description;
    private Difficulty difficulty;
    private double distanceMeters;
    private final Instant createdAt;
    private Instant updatedAt;

    private Route(UUID id, String name, String description, Difficulty difficulty,
                  double distanceMeters, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.distanceMeters = distanceMeters;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** Creează un traseu nou, respectând regulile de business. */
    public static Route create(String name, String description, Difficulty difficulty, double distanceMeters) {
        validate(name, difficulty, distanceMeters);
        Instant now = Instant.now();
        return new Route(UUID.randomUUID(), name, description, difficulty, distanceMeters, now, now);
    }

    /** Reconstruiește un traseu existent (folosit de infrastructură la citirea din baza de date). */
    public static Route restore(UUID id, String name, String description, Difficulty difficulty,
                                double distanceMeters, Instant createdAt, Instant updatedAt) {
        return new Route(id, name, description, difficulty, distanceMeters, createdAt, updatedAt);
    }

    public void update(String name, String description, Difficulty difficulty, double distanceMeters) {
        validate(name, difficulty, distanceMeters);
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.distanceMeters = distanceMeters;
        this.updatedAt = Instant.now();
    }

    private static void validate(String name, Difficulty difficulty, double distanceMeters) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        if (name.length() > 120) throw new IllegalArgumentException("Name must have at most 120 characters");
        if (difficulty == null) throw new IllegalArgumentException("Difficulty is required");
        if (distanceMeters < 0) throw new IllegalArgumentException("Distance must not be negative");
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Difficulty getDifficulty() { return difficulty; }
    public double getDistanceMeters() { return distanceMeters; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}