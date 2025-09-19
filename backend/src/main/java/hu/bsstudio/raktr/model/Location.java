package hu.bsstudio.raktr.model;

import static jakarta.persistence.CascadeType.REFRESH;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "location")
@JsonSerialize
@JsonDeserialize(builder = Location.LocationBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Location extends DomainAuditModel {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Setter(AccessLevel.NONE)
    @OneToMany(targetEntity = Device.class, fetch = FetchType.LAZY, cascade = REFRESH, mappedBy = "location")
    private Set<Device> devices;

    @JsonPOJOBuilder(withPrefix = "")
    public static class LocationBuilder {}
}
