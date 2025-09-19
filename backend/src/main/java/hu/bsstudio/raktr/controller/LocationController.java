package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.Location;
import hu.bsstudio.raktr.service.LocationService;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@SuppressWarnings("checkstyle:DesignForExtension")
@RequiredArgsConstructor
@RequestMapping("/api/location")
public class LocationController {

    private static final int CONFLICT = 409;

    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<Location> createLocation(@Valid @RequestBody final Location locationRequest) {
        log.info("Incoming request for new location: {}", locationRequest);

        final var location = locationService.create(locationRequest);

        return location
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(CONFLICT).build());
    }

    @GetMapping
    public ResponseEntity<List<Location>> locationList() {
        log.info("Incoming request for all locations");
        return ResponseEntity
            .ok(locationService.getAll());
    }

    @PutMapping
    public ResponseEntity<Location> updateLocation(@Valid @RequestBody final Location locationRequest) {
        log.info("Incoming request for updating a location: {}", locationRequest);

        final var location = locationService.update(locationRequest);

        return location
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping
    @Secured("ROLE_Stúdiós")
    public ResponseEntity<Location> deleteLocation(@Valid @RequestBody final Location locationRequest) {
        log.info("Incoming request to delete location: {}", locationRequest);

        final var location = locationService.delete(locationRequest);

        return location
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
