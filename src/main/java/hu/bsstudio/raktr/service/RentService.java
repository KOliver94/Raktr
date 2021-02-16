package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.repository.DeviceRepository;
import hu.bsstudio.raktr.repository.GeneralDataRepository;
import hu.bsstudio.raktr.repository.RentRepository;
import hu.bsstudio.raktr.repository.RentItemDao;
import hu.bsstudio.raktr.exception.NotAvailableQuantityException;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.BackStatus;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.model.GeneralData;
import hu.bsstudio.raktr.model.Rent;
import hu.bsstudio.raktr.model.RentItem;
import hu.bsstudio.raktr.pdfgeneration.RentPdfCreator;
import hu.bsstudio.raktr.pdfgeneration.RentPdfData;
import hu.bsstudio.raktr.pdfgeneration.RentPdfRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RentService {

    private static final String GROUP_NAME_KEY = "groupName";
    private static final String GROUP_LEADER_NAME_KEY = "groupLeader";
    private static final String FIRST_SIGNER_NAME_KEY = "firstSignerName";
    private static final String FIRST_SIGNER_TITLE_KEY = "firstSignerTitle";
    private static final String SECOND_SIGNER_NAME_KEY = "secondSignerName";
    private static final String SECOND_SIGNER_TITLE_KEY = "secondSignerTitle";

    private final RentRepository rentRepository;
    private final RentItemDao rentItemDao;
    private final DeviceRepository deviceRepository;
    private final GeneralDataRepository generalDataRepository;

    @SuppressWarnings("checkstyle:DesignForExtension")
    public boolean checkIfAvailable(final RentItem deviceRentItem, final RentItem rentItemToUpdate) {
        Integer maxAvailableQuantity = deviceRepository.getOne(deviceRentItem.getScannable().getId()).getQuantity();
        List<RentItem> rentItems = rentItemDao.findAll();
        Integer sumOut = 0;

        if (deviceRentItem.getOutQuantity() > maxAvailableQuantity) {
            return false;
        }

        for (RentItem rentItem : rentItems) {
            if (rentItem.getBackStatus().equals(BackStatus.OUT)
                && rentItem.getScannable().getId().equals(deviceRentItem.getScannable().getId())
                && (rentItemToUpdate == null || !rentItem.getId().equals(rentItemToUpdate.getId()))) {
                sumOut += rentItem.getOutQuantity();
            }

            if (sumOut + deviceRentItem.getOutQuantity() > maxAvailableQuantity) {
                return false;
            }
        }

        return true;
    }

    public final Rent create(final Rent rentRequest) {
        Rent saved = rentRepository.save(rentRequest);
        log.info("Rent saved: {}", saved);
        return saved;
    }

    public final Rent updateItem(final Long rentId, final RentItem newRentItem) {
        var rentToUpdate = rentRepository.findById(rentId);
        RentItem savedDeviceItem;
        RentItem rentItemToUpdate;

        if (rentToUpdate.isEmpty()) {
            throw new ObjectNotFoundException();
        }

        rentItemToUpdate = rentToUpdate.get().getRentItemOfScannable(newRentItem.getScannable());

        if (newRentItem.getScannable().getClass() == Device.class && !checkIfAvailable(newRentItem, rentItemToUpdate)) {
            throw new NotAvailableQuantityException();
        }

        if (rentItemToUpdate != null) {
            if (newRentItem.getOutQuantity() == 0) {
                rentToUpdate.get().getRentItems().remove(rentItemToUpdate);
                rentItemDao.delete(rentItemToUpdate);
            } else {
                rentItemToUpdate.setOutQuantity(newRentItem.getOutQuantity());
                rentItemToUpdate.setBackStatus(newRentItem.getBackStatus());

                rentItemDao.save(rentItemToUpdate);
            }
        } else {
            if (newRentItem.getOutQuantity() != 0) {
                savedDeviceItem = rentItemDao.save(newRentItem);
                rentToUpdate.get().getRentItems().add(savedDeviceItem);
            }
        }

        Rent saved = rentRepository.save(rentToUpdate.get());
        log.info("Rent updated: {}", saved);
        return saved;
    }

    public final List<Rent> getAll() {
        List<Rent> all = rentRepository.findAll();
        log.info("Rents fetched from DB: {}", all);
        return all;
    }

    public final Rent update(final Rent rentRequest) {
        var rentToUpdate = rentRepository.findById(rentRequest.getId());

        if (rentToUpdate.isEmpty()) {
            throw new ObjectNotFoundException();
        }

        rentToUpdate.get().setDestination(rentRequest.getDestination());
        rentToUpdate.get().setIssuer(rentRequest.getIssuer());
        rentToUpdate.get().setRenter(rentRequest.getRenter());
        rentToUpdate.get().setOutDate(rentRequest.getOutDate());
        rentToUpdate.get().setExpBackDate(rentRequest.getExpBackDate());
        rentToUpdate.get().setActBackDate(rentRequest.getActBackDate());

        Rent saved = rentRepository.save(rentToUpdate.get());
        log.info("Rent updated: {}", saved);
        return saved;
    }

    public final Rent delete(final Rent rentRequest) {
        rentRepository.delete(rentRequest);
        log.info("Rent deleted: {}", rentRequest);
        return rentRequest;
    }

    public final Rent getById(final Long rentId) {
        var foundRent = rentRepository.findById(rentId);

        if (foundRent.isEmpty()) {
            log.error("Rent not found with ID {}", rentId);
            throw new ObjectNotFoundException();
        }

        log.info("Rent found: {}", foundRent.get());
        return foundRent.get();
    }

    @SuppressWarnings({"checkstyle:InnerAssignment", "checkstyle:AvoidInlineConditionals"})
    public final ResponseEntity<byte[]> getPdf(final Long rentId, final RentPdfRequest rentPdfRequest) throws IOException {
        Rent rentToGenerate = rentRepository.findById(rentId).orElse(null);

        if (rentToGenerate == null) {
            log.error("Rent not found with ID {}", rentId);
            throw new ObjectNotFoundException();
        }

        String fileName = "pdf/rent_" + rentToGenerate.getId();

        Optional<GeneralData> foundData;
        String groupName = (foundData = generalDataRepository.findById(GROUP_NAME_KEY)).isPresent()
            ? foundData.get().getData() : "Budavári Schönherz Stúdió";
        String groupLeaderName = (foundData = generalDataRepository.findById(GROUP_LEADER_NAME_KEY)).isPresent() ? foundData.get().getData() : "";
        String firstSignerName = (foundData = generalDataRepository.findById(FIRST_SIGNER_NAME_KEY)).isPresent() ? foundData.get().getData() : "";
        String firstSignerTitle = (foundData = generalDataRepository.findById(FIRST_SIGNER_TITLE_KEY)).isPresent() ? foundData.get().getData() : "";
        String secondSignerName = (foundData = generalDataRepository.findById(SECOND_SIGNER_NAME_KEY)).isPresent() ? foundData.get().getData() : "";
        String secondSignerTitle = (foundData = generalDataRepository.findById(SECOND_SIGNER_TITLE_KEY)).isPresent() ? foundData.get().getData() : "";

        RentPdfData rentPdfData = RentPdfData.builder()
            .withTeamName(groupName)
            .withTeamLeaderName(groupLeaderName)
            .withFirstSignerName(firstSignerName)
            .withFirstSignerTitle(firstSignerTitle)
            .withSecondSignerName(secondSignerName)
            .withSecondSignerTitle(secondSignerTitle)
            .withOutDate(rentToGenerate.getOutDate())
            .withBackDate(rentToGenerate.getExpBackDate())
            .withFileName(fileName)
            .withRenterName(rentPdfRequest.getRenterFullName())
            .withRenterId(rentPdfRequest.getRenterId())
            .withItems(new HashMap<>())
            .build();

        for (var item : rentToGenerate.getRentItems()) {
            String currentName = item.getScannable().getName();

            if(rentPdfData.getItems().containsKey(currentName)){
                var currAmount = rentPdfData.getItems().get(currentName);
                currAmount += item.getOutQuantity();

                rentPdfData.getItems().replace(
                    currentName,
                    currAmount
                );
            } else {
                rentPdfData.getItems().put(
                    currentName,
                    item.getOutQuantity()
                );
            }
        }

        RentPdfCreator.generatePdf(rentPdfData);

        byte[] pdf = Files.readAllBytes(Paths.get(fileName + ".pdf"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData(fileName + ".pdf", fileName + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
