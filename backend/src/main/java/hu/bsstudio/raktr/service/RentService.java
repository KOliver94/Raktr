package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.config.GeneralDataProperties;
import hu.bsstudio.raktr.exception.NotAvailableQuantityException;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.Comment;
import hu.bsstudio.raktr.model.Device;
import hu.bsstudio.raktr.model.GeneralData;
import hu.bsstudio.raktr.model.Rent;
import hu.bsstudio.raktr.model.RentItem;
import hu.bsstudio.raktr.pdfgeneration.RentPdfCreator;
import hu.bsstudio.raktr.pdfgeneration.RentPdfData;
import hu.bsstudio.raktr.pdfgeneration.RentPdfRequest;
import hu.bsstudio.raktr.repository.CommentRepository;
import hu.bsstudio.raktr.repository.DeviceRepository;
import hu.bsstudio.raktr.repository.GeneralDataRepository;
import hu.bsstudio.raktr.repository.RentItemRepository;
import hu.bsstudio.raktr.repository.RentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class RentService {

    private final RentRepository rentRepository;
    private final RentItemRepository rentItemRepository;
    private final DeviceRepository deviceRepository;
    private final GeneralDataRepository generalDataRepository;
    private final CommentRepository commentRepository;
    private final GeneralDataProperties generalDataProperties;

    @SuppressWarnings("checkstyle:DesignForExtension")
    public boolean checkIfAvailable(final RentItem deviceRentItem, final RentItem rentItemToUpdate) {
        Integer maxAvailableQuantity = deviceRepository.getOne(deviceRentItem.getScannable().getId()).getQuantity();
        List<RentItem> rentItems = rentItemRepository.findAll();
        Integer sumOut = 0;

        return deviceRentItem.getOutQuantity() <= maxAvailableQuantity;
    }

    public final Rent create(final Rent rentRequest) {
        rentRequest.setIsDeleted(false);
        Rent saved = rentRepository.save(rentRequest);
        log.info("Rent saved: {}", saved);
        return saved;
    }

    public final Rent updateItem(final Long rentId, final RentItem newRentItem) {
        var rentToUpdate = rentRepository.findById(rentId)
            .orElseThrow(ObjectNotFoundException::new);
        RentItem savedDeviceItem;
        RentItem rentItemToUpdate;

        rentItemToUpdate = rentToUpdate.getRentItemOfScannable(newRentItem.getScannable());

        if (newRentItem.getScannable().getClass() == Device.class && !checkIfAvailable(newRentItem, rentItemToUpdate)) {
            throw new NotAvailableQuantityException();
        }

        if (rentItemToUpdate != null) {
            if (newRentItem.getOutQuantity() == 0) {
                rentToUpdate.getRentItems().remove(rentItemToUpdate);
                rentItemRepository.delete(rentItemToUpdate);
            } else {
                rentItemToUpdate.setOutQuantity(newRentItem.getOutQuantity());
                rentItemToUpdate.setBackStatus(newRentItem.getBackStatus());

                rentItemRepository.save(rentItemToUpdate);
            }
        } else { //New item
            if (newRentItem.getOutQuantity() != 0) {
                newRentItem.setAddedAt(new Date());

                savedDeviceItem = rentItemRepository.save(newRentItem);
                rentToUpdate.getRentItems().add(savedDeviceItem);
            }
        }

        Rent saved = rentRepository.save(rentToUpdate);
        log.info("Rent updated: {}", saved);
        return saved;
    }

    public final List<Rent> getAll() {
        List<Rent> all = rentRepository.findAll();
        log.info("Rents fetched from DB: {}", all);
        return all;
    }

    public final Rent update(final Rent rentRequest) {
        var rentToUpdate = rentRepository.findById(rentRequest.getId())
            .orElseThrow(ObjectNotFoundException::new);

        rentToUpdate.setType(rentRequest.getType());
        rentToUpdate.setDestination(rentRequest.getDestination());
        rentToUpdate.setOutDate(rentRequest.getOutDate());
        rentToUpdate.setExpBackDate(rentRequest.getExpBackDate());
        rentToUpdate.setBackDate(rentRequest.getBackDate());

        Rent saved = rentRepository.save(rentToUpdate);
        log.info("Rent updated: {}", saved);
        return saved;
    }

    public final Rent manageFinalization(final Rent rentRequest) {
        var rentToUpdate = rentRepository.findById(rentRequest.getId())
            .orElseThrow(ObjectNotFoundException::new);

        rentToUpdate.setType(rentRequest.getType());
        rentToUpdate.setDestination(rentRequest.getDestination());
        rentToUpdate.setIssuer(rentRequest.getIssuer());
        rentToUpdate.setOutDate(rentRequest.getOutDate());
        rentToUpdate.setBackDate(rentRequest.getBackDate());
        rentToUpdate.setIsClosed(rentRequest.getIsClosed());

        Rent saved = rentRepository.save(rentToUpdate);
        log.info("Rent updated with finalization: {}", saved);
        return saved;
    }

    public final Rent delete(final Rent rentRequest) {
        Rent foundRent = rentRepository.findById(rentRequest.getId())
            .orElseThrow(() => {
                log.warn("Rent not found to delete: {}", rentRequest);
                return new ObjectNotFoundException();
            });

        foundRent.setDeletedData();
        Rent saved = rentRepository.save(foundRent);

        log.info("Rent deleted: {}", saved);
        return saved;
    }

    public final Rent undelete(final Rent rentRequest) {
        Rent foundRent = rentRepository.findById(rentRequest.getId())
            .orElseThrow(() => {
                log.warn("Rent not found to restore: {}", rentRequest);
                return new ObjectNotFoundException();
            });

        foundRent.setUndeletedData();
        Rent saved = rentRepository.save(foundRent);

        log.info("Rent restored: {}", saved);
        return saved;
    }

    public final Rent getById(final Long rentId) {
        var foundRent = rentRepository.findById(rentId)
            .orElseThrow(() => {
                log.error("Rent not found with ID {}", rentId);
                return new ObjectNotFoundException();
            });

        log.info("Rent found: {}", foundRent);
        return foundRent;
    }

    public Optional<Rent> addCommentToRent(final Long rentId, final Comment commentToAdd) {
        return rentRepository.findById(rentId)
            .map(rent -> {
                Comment savedComment = commentRepository.save(commentToAdd);
                rent.getComments().add(savedComment);
                Rent updatedRent = rentRepository.save(rent);
                log.info("Comment added to rent: {}", updatedRent);
                return updatedRent;
            });
    }

    public Optional<Rent> removeCommentFromRent(final Long rentId, final Comment commentToRemove) {
        return rentRepository.findById(rentId)
            .flatMap(rent -> commentRepository.findById(commentToRemove.getId())
                .map(comment -> {
                    rent.getComments().remove(comment);
                    commentRepository.delete(comment);
                    Rent savedRent = rentRepository.save(rent);
                    log.info("Comment successfully removed from rent: {}", savedRent);
                    return savedRent;
                }));
    }

    @SuppressWarnings({"checkstyle:AvoidInlineConditionals"})
    public final ResponseEntity<byte[]> getPdf(final Long rentId, final RentPdfRequest rentPdfRequest) throws IOException {
        Rent rentToGenerate = rentRepository.findById(rentId)
            .orElseThrow(() => {
                log.error("Rent not found with ID {}", rentId);
                return new ObjectNotFoundException();
            });

        String fileName = "pdf/rent_" + rentToGenerate.getId();

        String groupName = getGeneralDataValue(generalDataProperties.groupNameKey(), "Budavári Schönherz Stúdió");
        String groupLeaderName = getGeneralDataValue(generalDataProperties.groupLeaderNameKey(), "");
        String firstSignerName = getGeneralDataValue(generalDataProperties.firstSignerNameKey(), "");
        String firstSignerTitle = getGeneralDataValue(generalDataProperties.firstSignerTitleKey(), "");
        String secondSignerName = getGeneralDataValue(generalDataProperties.secondSignerNameKey(), "");
        String secondSignerTitle = getGeneralDataValue(generalDataProperties.secondSignerTitleKey(), "");

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
                .withItems(new TreeMap<>())
                .build();

        for (var item : rentToGenerate.getRentItems()) {
            String currentName = item.getScannable().getName();

            if (rentPdfData.getItems().containsKey(currentName)) {
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

    private String getGeneralDataValue(String key, String defaultValue) {
        return generalDataRepository.findById(key)
            .map(GeneralData::getData)
            .orElse(defaultValue);
    }
}
