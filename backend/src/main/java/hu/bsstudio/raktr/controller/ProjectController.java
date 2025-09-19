package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.Project;
import hu.bsstudio.raktr.model.Rent;
import hu.bsstudio.raktr.service.ProjectService;
import jakarta.validation.constraints.NotNull;
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

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CONFLICT;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    private ResponseEntity<Project> addProject(@RequestBody @NotNull final Project projectRequest) {
        log.info("Incoming request for new project: {}", projectRequest);

        var project = projectService.create(projectRequest);

        return project
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(CONFLICT).build());
    }

    @PutMapping
    private ResponseEntity<Project> updateProject(@RequestBody @NotNull final Project projectUpdateRequest) {
        log.info("Incoming request to update project: {}", projectUpdateRequest);

        Optional<Project> updated = projectService.update(projectUpdateRequest);

        return updated
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    private ResponseEntity<List<Project>> getAllProjects() {
        log.info("Incoming request for all projects");
        return ResponseEntity
                .ok(projectService.getAll());
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/deleted")
    private ResponseEntity<List<Project>> getAllDeletedProjects() {
        log.info("Incoming request for all deleted projects");
        return ResponseEntity
                .ok(projectService.getAllDeleted());
    }

    @GetMapping("/{projectId}")
    private ResponseEntity<Project> getOneProject(final @PathVariable @NotNull Long projectId) {
        log.info("Incoming request for project with id: {}", projectId);

        Optional<Project> project = projectService.getOne(projectId);

        return project
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{projectId}")
    private ResponseEntity<Project> deleteProject(final @PathVariable @NotNull Long projectId) {
        log.info("Incoming request to delete project with id: {}", projectId);

        Optional<Project> project = projectService.delete(projectId);

        return project
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/undelete/{projectId}")
    private ResponseEntity<Project> unDeleteProject(final @PathVariable @NotNull Long projectId) {
        log.info("Incoming request to restore project with id: {}", projectId);

        Optional<Project> project = projectService.unDelete(projectId);

        return project
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{projectId}/newrent/")
    private ResponseEntity<Project> addRentToProject(final @PathVariable @NotNull Long projectId,
                                                     final @RequestBody @NotNull Rent rentRequest) {
        log.info("Incoming request to add new rent {}\n to project with id: {}", rentRequest, projectId);

        Optional<Project> project = projectService.addNewRentToProject(projectId, rentRequest);

        return project
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
