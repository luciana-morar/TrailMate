package com.trailmate.application.route;

import com.trailmate.application.NotFoundException;
import com.trailmate.application.PageQuery;
import com.trailmate.application.PageResult;
import com.trailmate.domain.model.Difficulty;
import com.trailmate.domain.model.Route;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional
public class RouteService {

    private final RouteRepository repository;

    public RouteService(RouteRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public PageResult<Route> list(Difficulty difficulty, PageQuery query) {
        return repository.findAll(difficulty, query);
    }

    @Transactional(readOnly = true)
    public Route get(UUID id) {
        return find(id);
    }

    public Route create(RouteCommand cmd) {
        Route route = Route.create(cmd.name(), cmd.description(), cmd.difficulty(), cmd.distanceMeters());
        return repository.save(route);
    }

    public Route update(UUID id, RouteCommand cmd) {
        Route route = find(id);
        route.update(cmd.name(), cmd.description(), cmd.difficulty(), cmd.distanceMeters());
        return repository.save(route);
    }

    public void delete(UUID id) {
        find(id);
        repository.delete(id);
    }

    private Route find(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Route " + id + " not found"));
    }
}