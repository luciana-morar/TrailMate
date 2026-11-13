package com.trailmate.infrastructure.persistence;

import com.trailmate.domain.model.Difficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataRouteRepository extends JpaRepository<RouteEntity, UUID> {
    Page<RouteEntity> findByDifficulty(Difficulty difficulty, Pageable pageable);
}