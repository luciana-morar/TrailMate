package com.trailmate.application.route;

import com.trailmate.application.NotFoundException;
import com.trailmate.domain.model.Difficulty;
import com.trailmate.domain.model.Route;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RouteServiceTest {

    private final RouteRepository repository = Mockito.mock(RouteRepository.class);
    private final RouteService service = new RouteService(repository);

    @Test
    void create_savesAndReturnsRoute() {
        when(repository.save(any(Route.class))).thenAnswer(inv -> inv.getArgument(0));

        Route result = service.create(new RouteCommand("Piatra Craiului", "Creasta", Difficulty.HARD, 12000));

        assertEquals("Piatra Craiului", result.getName());
        assertEquals(Difficulty.HARD, result.getDifficulty());
    }

    @Test
    void create_rejectsBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create(new RouteCommand(" ", null, Difficulty.EASY, 100)));
    }

    @Test
    void get_throwsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.get(id));
    }
}