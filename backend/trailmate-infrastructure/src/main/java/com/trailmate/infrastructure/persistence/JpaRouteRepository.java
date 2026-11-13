package com.trailmate.infrastructure.persistence;

import com.trailmate.application.PageQuery;
import com.trailmate.application.PageResult;
import com.trailmate.application.route.RouteRepository;
import com.trailmate.domain.model.Difficulty;
import com.trailmate.domain.model.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRouteRepository implements RouteRepository {

    private final SpringDataRouteRepository jpa;

    public JpaRouteRepository(SpringDataRouteRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Route save(Route route) {
        return toDomain(jpa.save(toEntity(route)));
    }

    @Override
    public Optional<Route> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public PageResult<Route> findAll(Difficulty difficulty, PageQuery query) {
        Pageable pageable = PageRequest.of(query.page(), query.size(), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<RouteEntity> page = (difficulty == null)
                ? jpa.findAll(pageable)
                : jpa.findByDifficulty(difficulty, pageable);
        return new PageResult<>(
                page.getContent().stream().map(this::toDomain).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Override
    public void delete(UUID id) {
        jpa.deleteById(id);
    }

    private RouteEntity toEntity(Route r) {
        RouteEntity e = new RouteEntity();
        e.id = r.getId();
        e.name = r.getName();
        e.description = r.getDescription();
        e.difficulty = r.getDifficulty();
        e.distanceMeters = r.getDistanceMeters();
        e.createdAt = r.getCreatedAt();
        e.updatedAt = r.getUpdatedAt();
        return e;
    }

    private Route toDomain(RouteEntity e) {
        return Route.restore(e.id, e.name, e.description, e.difficulty,
                e.distanceMeters, e.createdAt, e.updatedAt);
    }
}