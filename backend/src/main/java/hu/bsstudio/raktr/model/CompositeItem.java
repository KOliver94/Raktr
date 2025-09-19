package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static jakarta.persistence.CascadeType.REFRESH;
import static jakarta.persistence.FetchType.EAGER;

@EqualsAndHashCode(callSuper = true)
@Entity
@JsonSerialize
@JsonDeserialize(builder = CompositeItem.CompositeItemBuilder.class)
@NoArgsConstructor
@Data
@PrimaryKeyJoinColumn(name = "id")
public class CompositeItem extends Scannable {

    @ManyToMany(targetEntity = Device.class, cascade = REFRESH, fetch = EAGER)
    @Setter(AccessLevel.NONE)
    private List<Device> devices;

    @Builder
    public CompositeItem(Long id, @NotBlank String name, @NotBlank String textIdentifier, @NotNull String barcode,
                         @NotNull Boolean isPublicRentable, @NotNull Boolean isDeleted, List<RentItem> rentItems,
                         List<Device> devices, @NotNull Location location, @NotNull Category category) {
        super(id, name, textIdentifier, barcode, isPublicRentable, location, category, rentItems, isDeleted);
        this.devices = devices;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class CompositeItemBuilder{}

    @SuppressWarnings("checkstyle:DesignForExtension")
    @Override
    public String toString() {
        return "CompositeItem{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", barcode='" + barcode + '\''
                + ", textIdentifier='" + textIdentifier + '\''
                + ", public_rentable='" + isPublicRentable + "'"
                + ", devices=" + devices
                + ", location=" + location
                + '}';
    }

}
