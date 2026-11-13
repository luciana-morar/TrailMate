package com.trailmate.server.web;

import com.trailmate.application.PageQuery;
import com.trailmate.application.PageResult;
import com.trailmate.application.route.RouteService;
import com.trailmate.domain.model.Difficulty;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService service;

    public RouteController(RouteService service) {
        this.service = service;
    }

    @GetMapping
    public PageResult<RouteResponse> list(
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.list(difficulty, new PageQuery(page, size)).map(RouteResponse::from);
    }

    @GetMapping("/{id}")
    public RouteResponse get(@PathVariable UUID id) {
        return RouteResponse.from(service.get(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RouteResponse create(@Valid @RequestBody RouteRequest request) {
        return RouteResponse.from(service.create(request.toCommand()));
    }

    @PutMapping("/{id}")
    public RouteResponse update(@PathVariable UUID id, @Valid @RequestBody RouteRequest request) {
        return RouteResponse.from(service.update(id, request.toCommand()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}