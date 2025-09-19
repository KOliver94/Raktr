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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;

@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("checkstyle:FinalClass")
@Entity
@Data
@JsonSerialize
@JsonDeserialize(builder = RentItem.RentItemBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentItem extends DomainAuditModel {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @NotNull
    @JoinColumn
    @ManyToOne(fetch = EAGER)
    private Scannable scannable;

    @NotNull
    private BackStatus backStatus;

    @NotNull
    private Integer outQuantity;

    @NotNull
    private Date addedAt;

    @ManyToOne(targetEntity = User.class, fetch = LAZY)
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "addedByUserId")
    private User addedBy;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @ManyToOne(targetEntity = Rent.class, fetch = LAZY)
    @JoinColumn(name = "rent_id")
    private Rent rent;

    @JsonPOJOBuilder(withPrefix = "")
    public static class RentItemBuilder {}
}
