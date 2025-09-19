package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.Owner;
import hu.bsstudio.raktr.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@SuppressWarnings("checkstyle:DesignForExtension")
@RequiredArgsConstructor
@RequestMapping("/api/owner")
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping
    public ResponseEntity<List<Owner>> ownerList() {
        log.info("Incoming request for all owners");
        return ResponseEntity
                .ok(ownerService.getAll());
    }

    @PostMapping
    public ResponseEntity<Owner> addOwner(final @Valid @RequestBody Owner owner) {
        log.info("Incoming to add owner: {}", owner);

        Optional<Owner> updated = ownerService.create(owner);

        return updated
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<Owner> updateOwner(final @Valid @RequestBody Owner owner) {
        log.info("Incoming to update owner: {}", owner);

        Optional<Owner> updated = ownerService.update(owner);

        return updated
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{ownerId}")
    public ResponseEntity<Owner> deleteOwner(final @PathVariable Long ownerId) {
        log.info("Incoming to remove owner with id: {}", ownerId);

        Optional<Owner> deleted = ownerService.deleteOwner(ownerId);

        return deleted
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
