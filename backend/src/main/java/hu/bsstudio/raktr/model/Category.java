package hu.bsstudio.raktr.model;

import static jakarta.persistence.CascadeType.REFRESH;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
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
@Table(name = "category")
@JsonDeserialize(builder = Category.CategoryBuilder.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Category extends DomainAuditModel {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String name;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Setter(AccessLevel.NONE)
    @OneToMany(targetEntity = Device.class, cascade = REFRESH, fetch = FetchType.LAZY, mappedBy = "category")
    private Set<Device> devices;

    @JsonPOJOBuilder(withPrefix = "")
    public static class CategoryBuilder {}
}
