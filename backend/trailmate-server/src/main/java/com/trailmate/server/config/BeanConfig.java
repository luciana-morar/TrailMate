package com.trailmate.server.config;

import com.trailmate.application.route.RouteRepository;
import com.trailmate.application.route.RouteService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public RouteService routeService(RouteRepository repository) {
        return new RouteService(repository);
    }
}