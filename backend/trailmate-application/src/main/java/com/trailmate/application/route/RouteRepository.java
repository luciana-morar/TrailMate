package com.trailmate.application.route;

import com.trailmate.application.PageQuery;
import com.trailmate.application.PageResult;
import com.trailmate.domain.model.Difficulty;
import com.trailmate.domain.model.Route;

import java.util.Optional;
import java.util.UUID;

public interface RouteRepository {
    Route save(Route route);
    Optional<Route> findById(UUID id);
    PageResult<Route> findAll(Difficulty difficulty, PageQuery query);
    void delete(UUID id);
}