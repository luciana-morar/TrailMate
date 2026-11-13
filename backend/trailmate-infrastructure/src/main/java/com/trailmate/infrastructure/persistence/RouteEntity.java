package com.trailmate.infrastructure.persistence;

import com.trailmate.domain.model.Difficulty;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "routes")
public class RouteEntity {

    @Id
    UUID id;

    @Column(nullable = false)
    String name;

    @Column(length = 2000)
    String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Difficulty difficulty;

    double distanceMeters;

    @Column(nullable = false)
    Instant createdAt;

    @Column(nullable = false)
    Instant updatedAt;

    protected RouteEntity() {} // cerut de JPA
}