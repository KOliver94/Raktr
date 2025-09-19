package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.dto.RentItemWithRentData;
import hu.bsstudio.raktr.model.CompositeItem;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.model.Scannable;
import hu.bsstudio.raktr.repository.CompositeItemRepository;
import hu.bsstudio.raktr.repository.DeviceRepository;
import hu.bsstudio.raktr.repository.RentItemRepository;
import hu.bsstudio.raktr.repository.RentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScannableService {

    private final DeviceRepository deviceRepository;
    private final CompositeItemRepository compositeItemRepository;
    private final RentItemRepository rentItemRepository;
    private final RentRepository rentRepository;

    public final Optional<? extends Scannable> getByBarcode(final String barcode) {
        var foundDevice = deviceRepository.findByBarcode(barcode);

        if (foundDevice.isEmpty()) {
            var foundCompositeItem = compositeItemRepository.findByBarcode(barcode);
            if (foundCompositeItem.isEmpty()) {
                return Optional.empty();
            } else {
                log.info("Composite Item found by barcode: {}", foundCompositeItem.get());
                return foundCompositeItem;
            }
        } else {
            log.info("Device found by barcode: {}", foundDevice.get());
            return foundDevice;
        }
    }

    public final Optional<? extends Scannable> getByTextIdentifier(final String textIdentifier) {
        var foundDevice = deviceRepository.findByTextIdentifier(textIdentifier);

        if (foundDevice.isEmpty()) {
            var foundCompositeItem = compositeItemRepository.findByTextIdentifier(textIdentifier);
            if (foundCompositeItem.isEmpty()) {
                return Optional.empty();
            } else {
                log.info("Composite Item found by textIdentifier: {}", foundCompositeItem.get());
                return foundCompositeItem;
            }
        } else {
            log.info("Device found by textIdentifier: {}", foundDevice.get());
            return foundDevice;
        }
    }

    public final Optional<List<RentItemWithRentData>> getRentItemsOfScannable(final Long id) {
        var rentItems = rentItemRepository.findRentItemsByScannable_Id(id);

        if (rentItems.isEmpty())
            return Optional.empty();

        var rentItemsWithRent = new ArrayList<RentItemWithRentData>();

        for (int i = 0; i < rentItems.get().size(); i++) {
            var item = rentItems.get().get(i);

            var rent = rentRepository.findByRentItemsId(item.getId());

            rentItemsWithRent.add(new RentItemWithRentData(item, rent.get()));
        }

        return Optional.of(rentItemsWithRent);
    }

    public final Long getScannableCount() {
        return deviceRepository.count() + compositeItemRepository.count();
    }

    public final Long getNextId() {
        Long nextId = 1L;

        try {
            nextId = deviceRepository.getMaxId() + 1;
        } catch (Exception e) {

        }

        log.info("Found next id: {}", nextId);
        return nextId;
    }

    public final Long textIdTaken(final String textId) {
        Optional<Device> device = deviceRepository.findByTextIdentifier(textId);

        if (device.isPresent()) {
            return device.get().getId();
        }

        Optional<CompositeItem> compositeItem = compositeItemRepository.findByTextIdentifier(textId);

        if (compositeItem.isPresent()) {
            return compositeItem.get().getId();
        }

        return -1L;
    }

    public final Long barcodeTaken(final String barcode) {
        Optional<Device> device = deviceRepository.findByBarcode(barcode);

        if (device.isPresent()) {
            return device.get().getId();
        }

        Optional<CompositeItem> compositeItem = compositeItemRepository.findByBarcode(barcode);

        if (compositeItem.isPresent()) {
            return compositeItem.get().getId();
        }

        return -1L;
    }
}
