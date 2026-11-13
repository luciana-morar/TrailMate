package com.trailmate.server.web;

import com.trailmate.application.route.RouteCommand;
import com.trailmate.domain.model.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record RouteRequest(
        @NotBlank @Size(max = 120) String name,
        @Size(max = 2000) String description,
        @NotNull Difficulty difficulty,
        @PositiveOrZero double distanceMeters) {

    RouteCommand toCommand() {
        return new RouteCommand(name, description, difficulty, distanceMeters);
    }
}