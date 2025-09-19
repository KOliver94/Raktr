package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Device.class, name = "device"),
    @JsonSubTypes.Type(value = CompositeItem.class, name = "compositeItem")
})
@SuppressWarnings("checkstyle:VisibilityModifier")
public abstract class Scannable extends DomainAuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @NotBlank
    protected String name;

    @NotBlank
    @Column(unique = true)
    protected String textIdentifier;

    @NotNull
    @Column(unique = true)
    protected String barcode;

    @NotNull
    protected Boolean isPublicRentable;

    @NotNull
    @ManyToOne
    @JoinColumn
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    protected Location location;

    @NotNull
    @JoinColumn
    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    protected Category category;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "scannable")
    List<RentItem> rentItems;

    @JsonIgnore
    protected Boolean isDeleted;

    public Scannable setDeletedData() {
        barcode = barcode + "_d_" + id;
        textIdentifier = textIdentifier + "_d_" + id;
        isDeleted = true;

        return this;
    }

    public Scannable setUndeletedData() {
        barcode = barcode.split("_")[0];
        textIdentifier = textIdentifier.split("_")[0];
        isDeleted = false;

        return this;
    }
}
