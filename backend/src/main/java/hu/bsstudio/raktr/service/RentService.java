package hu.bsstudio.raktr.service;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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
@PropertySource("classpath:generalData.properties")
@RequiredArgsConstructor
public class RentService {

    private final RentRepository rentRepository;
    private final RentItemRepository rentItemRepository;
    private final DeviceRepository deviceRepository;
    private final GeneralDataRepository generalDataRepository;
    private final CommentRepository commentRepository;
    @Value("${generalData.groupNameKey}")
    private String GROUP_NAME_KEY;
    @Value("${generalData.groupLeaderNameKey}")
    private String GROUP_LEADER_NAME_KEY;
    @Value("${generalData.firstSignerNameKey}")
    private String FIRST_SIGNER_NAME_KEY;
    @Value("${generalData.firstSignerTitleKey}")
    private String FIRST_SIGNER_TITLE_KEY;
    @Value("${generalData.secondSignerNameKey}")
    private String SECOND_SIGNER_NAME_KEY;
    @Value("${generalData.secondSignerTitleKey}")
    private String SECOND_SIGNER_TITLE_KEY;

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

        rentToUpdate.get().setType(rentRequest.getType());
        rentToUpdate.get().setDestination(rentRequest.getDestination());
        rentToUpdate.get().setOutDate(rentRequest.getOutDate());
        rentToUpdate.get().setExpBackDate(rentRequest.getExpBackDate());
        rentToUpdate.get().setBackDate(rentRequest.getBackDate());

        Rent saved = rentRepository.save(rentToUpdate.get());
        log.info("Rent updated: {}", saved);
        return saved;
    }

    public final Rent manageFinalization(final Rent rentRequest) {
        var rentToUpdate = rentRepository.findById(rentRequest.getId());

        if (rentToUpdate.isEmpty()) {
            throw new ObjectNotFoundException();
        }

        rentToUpdate.get().setType(rentRequest.getType());
        rentToUpdate.get().setDestination(rentRequest.getDestination());
        rentToUpdate.get().setIssuer(rentRequest.getIssuer());
        rentToUpdate.get().setOutDate(rentRequest.getOutDate());
        rentToUpdate.get().setBackDate(rentRequest.getBackDate());
        rentToUpdate.get().setIsClosed(rentRequest.getIsClosed());

        Rent saved = rentRepository.save(rentToUpdate.get());
        log.info("Rent updated with finalization: {}", saved);
        return saved;
    }

    public final Rent delete(final Rent rentRequest) {
        Optional<Rent> foundRent = rentRepository.findById(rentRequest.getId());

        if (foundRent.isEmpty()) {
            log.warn("Rent not found to delete: {}", rentRequest);
            throw new ObjectNotFoundException();
        }

        foundRent.get().setDeletedData();
        Rent saved = rentRepository.save(foundRent.get());

        log.info("Rent deleted: {}", saved);
        return saved;
    }

    public final Rent undelete(final Rent rentRequest) {
        Optional<Rent> foundRent = rentRepository.findById(rentRequest.getId());

        if (foundRent.isEmpty()) {
            log.warn("Rent not found to restore: {}", rentRequest);
            throw new ObjectNotFoundException();
        }

        foundRent.get().setUndeletedData();
        Rent saved = rentRepository.save(foundRent.get());

        log.info("Rent restored: {}", saved);
        return saved;
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

    public Optional<Rent> addCommentToRent(final Long rentId, final Comment commentToAdd) {
        Optional<Rent> rentToAddTo = rentRepository.findById(rentId);

        if (rentToAddTo.isEmpty()) {
            log.info("Rent not found with id: {}", rentId);
            return Optional.empty();
        }

        Comment savedComment = commentRepository.save(commentToAdd);

        rentToAddTo.get().getComments().add(savedComment);

        Rent updatedRent = rentRepository.save(rentToAddTo.get());
        log.info("Comment added to rent: {}", updatedRent);

        return Optional.of(updatedRent);
    }

    public Optional<Rent> removeCommentFromRent(final Long rentId, final Comment commentToRemove) {
        Optional<Rent> rentToUpdate = rentRepository.findById(rentId);
        Optional<Comment> commentFound = commentRepository.findById(commentToRemove.getId());

        if (rentToUpdate.isEmpty()) {
            log.info("Rent not found with id: {}", rentId);
            return Optional.empty();
        }

        if (commentFound.isEmpty()) {
            log.info("Comment not found with id: {}", commentToRemove.getId());
            return Optional.empty();
        }

        rentToUpdate.get().getComments().remove(commentFound.get());
        commentRepository.delete(commentFound.get());

        Rent savedRent = rentRepository.save(rentToUpdate.get());

        log.info("Comment successfully removed from rent: {}", savedRent);
        return Optional.of(savedRent);
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
}
