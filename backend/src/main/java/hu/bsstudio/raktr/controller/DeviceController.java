package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.service.DeviceService;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
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
@RequestMapping("/api/device")
@PropertySource("classpath:userInfo.properties")
@RequiredArgsConstructor
public class DeviceController {

    private static final int CONFLICT = 409;

    private final DeviceService deviceService;

    @PostMapping
    public ResponseEntity<Device> createDevice(@Valid @RequestBody final Device deviceRequest) {
        log.info("Incoming request for new device: {}", deviceRequest);

        final var device = deviceService.create(deviceRequest);

        return device
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(CONFLICT).build());
    }

    @GetMapping
    public List<Device> getDeviceList() {
        log.info("Incoming request for all Devices");
        return deviceService.getAll();
    }

    @GetMapping("/deleted")
    @Secured("ROLE_ADMIN")
    public List<Device> getDeletedDeviceList() {
        log.info("Incoming request for all Devices");
        return deviceService.getAllDeleted();
    }

    @DeleteMapping
    @Secured("ROLE_Admin")
    public ResponseEntity<Device> deleteDevice(@Valid @RequestBody final Device deviceRequest) {
        log.info("Incoming request to delete device: {}", deviceRequest);

        final var device = deviceService.delete(deviceRequest);

        return device
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_Stúdiós")
    public ResponseEntity<Device> deleteDeviceById(@PathVariable final Long id) {
        log.info("Incoming request to delete device with id: {}", id);

        final var device = deviceService.deleteById(id);

        return device
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/undelete")
    @Secured("ROLE_Admin")
    public ResponseEntity<Device> unDeleteDevice(@Valid @RequestBody final Device deviceRequest) {
        log.info("Incoming request to restore device: {}", deviceRequest);

        final var device = deviceService.unDelete(deviceRequest);

        return device
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<Device> updateDevice(@Valid @RequestBody final Device deviceRequest) {
        log.info("Incoming request to update device: {}", deviceRequest);

        final var device = deviceService.update(deviceRequest);

        return device
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable final Long id) {
        log.info("Incoming request for device with id {}", id);
        final var device = deviceService.getById(id);

        return device
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/makers")
    public List<String> getAllMakers() {
        log.info("Incoming request for all makers");

        return deviceService.getAllMakers()
                .stream()
                .filter(maker -> !Objects.equals(maker, ""))
                .collect(Collectors.toList());
    }
}
