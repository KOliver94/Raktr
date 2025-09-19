package hu.bsstudio.raktr.controller;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;


import hu.bsstudio.raktr.model.Comment;
import hu.bsstudio.raktr.model.Ticket;
import hu.bsstudio.raktr.service.TicketService;
import java.util.List;
import java.util.Optional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@RequestMapping("/api/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public List<Ticket> getAllTickets() {
        log.info("Incoming request for all tickets");

        return ticketService.getAll();
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<Ticket> getAllTickets(@PathVariable final Long ticketId) {
        log.info("Incoming request for ticket with id: {}", ticketId);

        var ticket = ticketService.get(ticketId);

        return ticket
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(CONFLICT).build());
    }

    @PostMapping
    public ResponseEntity<Ticket> addTicket(@Valid @RequestBody Ticket newTicket) {
        log.info("Incoming request to add ticket: {}", newTicket);

        Optional<Ticket> ticket = ticketService.addTicket(newTicket);

        return ticket
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(CONFLICT).build());
    }

    @PutMapping
    public ResponseEntity<Ticket> updateTicket(@Valid @RequestBody Ticket ticketToUpdate) {
        log.info("Incoming request to update ticket: {}", ticketToUpdate);

        Optional<Ticket> ticket = ticketService.updateTicket(ticketToUpdate);

        return ticket
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(NOT_FOUND).build());
    }

    @PutMapping("/addcomment/{ticketId}")
    public ResponseEntity<Ticket> addCommentToTicket(@PathVariable final Long ticketId, @Valid @RequestBody Comment comment) {
        log.info("Incoming request to add comment: {} to ticket with id: {}", comment, ticketId);

        Optional<Ticket> ticket = ticketService.addCommentToTicket(ticketId, comment);

        return ticket
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(NOT_FOUND).build());
    }

    @GetMapping("/ofScannable/{scannableId}")
    public List<Ticket> getTicketsOfScannable(@PathVariable final Long scannableId) {
        log.info("Incoming request for tickets of scannable item with id {}", scannableId);

        return ticketService.getTicketsOfScannable(scannableId);
    }
}
