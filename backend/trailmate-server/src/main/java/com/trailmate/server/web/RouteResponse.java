package com.trailmate.server.web;

import com.trailmate.domain.model.Difficulty;
import com.trailmate.domain.model.Route;

import java.time.Instant;
import java.util.UUID;

public record RouteResponse(UUID id, String name, String description, Difficulty difficulty,
                            double distanceMeters, Instant createdAt, Instant updatedAt) {

    static RouteResponse from(Route r) {
        return new RouteResponse(r.getId(), r.getName(), r.getDescription(), r.getDifficulty(),
                r.getDistanceMeters(), r.getCreatedAt(), r.getUpdatedAt());
    }
}