package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.CompositeItem;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.service.CompositeService;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@SuppressWarnings("checkstyle:DesignForExtension")
@RequestMapping("/api/composite")
@RequiredArgsConstructor
public class CompositeController {

    private static final int CONFLICT = 409;

    private final CompositeService compositeService;

    @GetMapping
    public List<CompositeItem> getCompositeList() {
        log.info("Incoming request for all composite items");
        return compositeService.getAll();
    }

    @GetMapping("/deleted")
    @Secured("ROLE_ADMIN")
    public List<CompositeItem> getDeletedCompositeList() {
        log.info("Incoming request for all deleted composite items");
        return compositeService.getAllDeleted();
    }

    @PostMapping
    public ResponseEntity<CompositeItem> createCompositeItem(@Valid @RequestBody final CompositeItem compositeItemRequest) {
        log.info("Incoming request for new composite item: {}", compositeItemRequest);

        final var compositeItem = compositeService.create(compositeItemRequest);

        return compositeItem
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(CONFLICT).build());
    }

    @PutMapping
    public ResponseEntity<CompositeItem> updateCompositeItem(@Valid @RequestBody final CompositeItem compositeItemRequest) {
        log.info("Incoming request to update composite item: {}", compositeItemRequest);

        final var compositeItem = compositeService.update(compositeItemRequest);

        return compositeItem
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping
    @Secured("ROLE_Stúdiós")
    public ResponseEntity<CompositeItem> deleteCompositeItem(@Valid @RequestBody final CompositeItem compositeItemRequest) {
        log.info("Incoming request to delete composite item: {}", compositeItemRequest);

        final var compositeItem = compositeService.delete(compositeItemRequest);

        return compositeItem
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/undelete")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<CompositeItem> undeleteCompositeItem(@Valid @RequestBody final CompositeItem compositeItemRequest) {
        log.info("Incoming request to restore composite item: {}", compositeItemRequest);

        final var compositeItem = compositeService.unDelete(compositeItemRequest);

        return compositeItem
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{compositeId}")
    public ResponseEntity<CompositeItem> getCompositeById(@PathVariable final Long compositeId) {
        log.info("Incoming request for composite item with id {}", compositeId);

        final var compositeItem = compositeService.getById(compositeId);

        return compositeItem
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{compositeId}")
    public ResponseEntity<CompositeItem> addDeviceToCompositeItem(
        @PathVariable final Long compositeId,
        @Valid @RequestBody final Device deviceRequest) {
        log.info("Incoming request to add device {} to composite item with id {}", deviceRequest, compositeId);

        final var compositeItem = compositeService.addDevice(compositeId, deviceRequest);

        return compositeItem
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{compositeId}")
    @Secured("ROLE_Stúdiós")
    public ResponseEntity<CompositeItem> deleteDeviceFromCompositeItem(
        @PathVariable final Long compositeId,
        @Valid @RequestBody final Device deviceRequest) {
        log.info("Incoming request to delete device {} from composite item with id {}", deviceRequest, compositeId);

        final var compositeItem = compositeService.removeDeviceFromComposite(compositeId, deviceRequest);

        return compositeItem
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
