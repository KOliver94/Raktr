package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.exception.ObjectConflictException;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.Category;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.model.Location;
import hu.bsstudio.raktr.repository.CategoryRepository;
import hu.bsstudio.raktr.repository.DeviceRepository;
import hu.bsstudio.raktr.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public final class DeviceService {

    private final OwnerService ownerService;
    private final DeviceRepository deviceRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    public Device create(final Device deviceRequest) {
        if (deviceRequest.getId() != null && deviceRepository.existsById(deviceRequest.getId())) {
            log.info("Device failed to create, id conflict: {}", deviceRequest);
            throw new ObjectConflictException();
        }

        checkCategoryAndLocation(deviceRequest);
        deviceRequest.setIsDeleted(false);

        if (deviceRequest.getOwner() != null) {
            ownerService.create(deviceRequest.getOwner())
                    .ifPresent(deviceRequest::setOwner);
        }

        var saved = deviceRepository.save(deviceRequest);
        log.info("Device created: {}", saved);
        return saved;
    }

    public List<Device> getAll() {
        var fetched = deviceRepository.findAll();
        log.info("Devices fetched from DB: {}", fetched);
        return fetched;
    }

    public List<Device> getAllDeleted() {
        var fetched = deviceRepository.findAllDeleted();
        log.info("Deleted devices fetched from DB: {}", fetched);
        return fetched;
    }

    public Optional<Device> delete(final Device deviceRequest) {
        Optional<Device> foundDevice = deviceRepository.findById(deviceRequest.getId());
        foundDevice.ifPresent(device -> {
            device.setDeletedData();
            deviceRepository.save(device);
            log.info("Deleted device from DB: {}", device);
        });
        if (foundDevice.isEmpty()) {
            log.info("Device to delete not found in DB: {}", deviceRequest);
        }
        return foundDevice;
    }

    public Optional<Device> unDelete(final Device deviceRequest) {
        Optional<Device> foundDevice = deviceRepository.findById(deviceRequest.getId());

        foundDevice.ifPresent(device -> {
            device.setUndeletedData();

            Optional<Device> byBarcode = deviceRepository.findByBarcode(device.getBarcode());
            Optional<Device> byTextIdentifier = deviceRepository.findByTextIdentifier(device.getTextIdentifier());

            if (byBarcode.isPresent() && !byBarcode.get().getId().equals(device.getId()) ||
                    byTextIdentifier.isPresent() && !byTextIdentifier.get().getId().equals(device.getId())) {
                log.warn("Original device barcode ({}) or textID ({}) taken",
                        device.getBarcode(), device.getTextIdentifier());
                throw new ObjectConflictException();
            }

            deviceRepository.save(device);
            log.info("Restored device in DB: {}", device);
        });

        if (foundDevice.isEmpty()) {
            log.warn("Device to restore not found in DB: {}", deviceRequest);
        }

        return foundDevice;
    }

    public Device update(final Device deviceRequest) {
        checkCategoryAndLocation(deviceRequest);

        Device deviceToUpdate = deviceRepository.findById(deviceRequest.getId())
                .orElseThrow(() -> {
                    log.warn("Device not found in db to update: {}", deviceRequest);
                    return new ObjectNotFoundException();
                });

        if (deviceRequest.getOwner() != null) {
            ownerService.create(deviceRequest.getOwner())
                    .ifPresent(deviceToUpdate::setOwner);
        }

        deviceToUpdate.setBarcode(deviceRequest.getBarcode());
        deviceToUpdate.setIsPublicRentable(deviceRequest.getIsPublicRentable());
        deviceToUpdate.setTextIdentifier(deviceRequest.getTextIdentifier());
        deviceToUpdate.setName(deviceRequest.getName());
        deviceToUpdate.setMaker(deviceRequest.getMaker());
        deviceToUpdate.setType(deviceRequest.getType());
        deviceToUpdate.setSerial(deviceRequest.getSerial());
        deviceToUpdate.setStatus(deviceRequest.getStatus());
        deviceToUpdate.setValue(deviceRequest.getValue());
        deviceToUpdate.setWeight(deviceRequest.getWeight());
        deviceToUpdate.setQuantity(deviceRequest.getQuantity());
        deviceToUpdate.setCategory(deviceRequest.getCategory());
        deviceToUpdate.setLocation(deviceRequest.getLocation());
        deviceToUpdate.setAquiredFrom(deviceRequest.getAquiredFrom());
        deviceToUpdate.setComment(deviceRequest.getComment());
        deviceToUpdate.setDateOfAcquisition(deviceRequest.getDateOfAcquisition());
        deviceToUpdate.setEndOfWarranty(deviceRequest.getEndOfWarranty());

        Device saved = deviceRepository.save(deviceToUpdate);
        log.info("Device updated in DB: {}", saved);
        return saved;
    }

    public Device getById(final Long id) {
        Device foundDevice = deviceRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Device not found with id: {}", id);
                    return new ObjectNotFoundException();
                });

        log.info("Device with id {} found: {}", id, foundDevice);
        return foundDevice;
    }

    private void checkCategoryAndLocation(final Device deviceRequest) {
        var category = categoryRepository.findByName(deviceRequest.getCategory().getName())
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name(deviceRequest.getCategory().getName())
                        .build()));
        deviceRequest.setCategory(category);

        var location = locationRepository.findByName(deviceRequest.getLocation().getName())
                .orElseGet(() -> locationRepository.save(Location.builder()
                        .name(deviceRequest.getLocation().getName())
                        .build()));
        deviceRequest.setLocation(location);
    }

    public Optional<Device> deleteById(final Long id) {
        Optional<Device> deviceToDelete = deviceRepository.findById(id);
        deviceToDelete.ifPresentOrElse(
                device -> {
                    deviceRepository.deleteById(id);
                    log.info("Device by id deleted: {}", device);
                },
                () -> log.info("Device not found to delete with id: {}", id)
        );
        return deviceToDelete;
    }

    public List<String> getAllMakers() {
        var fetched = deviceRepository.findAllMakers();
        log.info("Makers fetched from DB: {}", fetched);
        return fetched;
    }
}
