package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
import lombok.ToString;

import java.util.Date;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "rents")
@JsonSerialize()
@JsonDeserialize(builder = Rent.RentBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Rent extends DomainAuditModel {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private RentType type;

    private String destination;

    @ManyToOne(fetch = LAZY)
    private User issuer;

    @NotBlank
    private String renter;

    @NotNull
    private Date outDate;

    @NotNull
    private Date expBackDate;

    private Date backDate;

    @NotNull
    private Boolean isClosed;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToMany(targetEntity = RentItem.class, fetch = LAZY, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<RentItem> rentItems;

    @ManyToMany
    private List<Comment> comments;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(fetch = LAZY, targetEntity = Project.class)
    private Project project;

    @JsonIgnore
    private Boolean isDeleted;

    @SuppressWarnings("checkstyle:DesignForExtension")
    public RentItem getRentItemOfScannable(final Scannable scannableToFind) {
        for (RentItem rentItem : rentItems) {
            if (rentItem.getScannable().getId().equals(scannableToFind.getId())) {
                return rentItem;
            }
        }

        return null;
    }

    public Rent setDeletedData() {
        isDeleted = true;

        return this;
    }

    public Rent setUndeletedData() {
        isDeleted = false;

        return this;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class RentBuilder {}
}
