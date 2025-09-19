package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "owner")
@JsonSerialize
@JsonDeserialize(builder = Owner.OwnerBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Owner extends DomainAuditModel {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;

    @NotNull
    private boolean inSchInventory;

    @Setter(AccessLevel.NONE)
    @JsonIgnore
    @OneToMany(targetEntity = Device.class, fetch = FetchType.LAZY)
    private List<Device> devices;

    @JsonPOJOBuilder(withPrefix = "")
    public static class OwnerBuilder {}
}
