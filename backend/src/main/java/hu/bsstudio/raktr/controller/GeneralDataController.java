package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.GeneralData;
import hu.bsstudio.raktr.service.GeneralDataService;
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
@RequestMapping("/api/generaldata")
@RequiredArgsConstructor
public class GeneralDataController {

    private static final int CONFLICT = 409;

    private final GeneralDataService generalDataService;

    @PostMapping
    public ResponseEntity<GeneralData> createGeneralData(@Valid @RequestBody final GeneralData dataToAdd) {
        log.info("Incoming request to add General data: {}", dataToAdd);

        final var generalData = generalDataService.create(dataToAdd);
        return generalData
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(CONFLICT).build());
    }

    @GetMapping
    public ResponseEntity<List<GeneralData>> getAllGeneralData() {
        log.info("Incoming request for all general data");
        return ResponseEntity.ok(generalDataService.getAll());
    }

    @GetMapping("/{key}")
    public ResponseEntity<GeneralData> getByKey(@PathVariable final String key) {
        log.info("Incoming request for General data with key: {}", key);

        final var generalData = generalDataService.getByKey(key);

        return generalData
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<GeneralData> updateData(@Valid @RequestBody final GeneralData dataToUpdate) {
        log.info("Incoming request to update data: {}", dataToUpdate);
        return ResponseEntity.ok(generalDataService.update(dataToUpdate));
    }

    @DeleteMapping
    @Secured("ROLE_Stúdiós")
    public ResponseEntity<GeneralData> deleteData(@Valid @RequestBody final GeneralData dataToDelete) {
        log.info("Incoming request to delete general data: {}", dataToDelete);

        final var generalData = generalDataService.delete(dataToDelete);

        return generalData
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
