package com.trailmate.application.route;

import com.trailmate.domain.model.Difficulty;

public record RouteCommand(String name, String description, Difficulty difficulty, double distanceMeters) {}