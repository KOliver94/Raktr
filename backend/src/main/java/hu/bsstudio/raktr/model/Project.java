package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "projects")
@JsonSerialize()
@JsonDeserialize(builder = Project.ProjectBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Project extends DomainAuditModel {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    @ManyToOne
    private User prodManager;

    @NotNull
    private Date startDate;

    private Date expEndDate;

    @OneToMany(targetEntity = Rent.class, fetch = LAZY)
    @Setter(AccessLevel.NONE)
    private List<Rent> rents;

    @JsonIgnore
    private Boolean isDeleted;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ProjectBuilder {}

    public Project setDeletedData() {
        isDeleted = true;

        return this;
    }

    public Project setUndeletedData() {
        isDeleted = false;

        return this;
    }
}
