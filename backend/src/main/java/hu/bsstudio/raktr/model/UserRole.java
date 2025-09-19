package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "user_role")
@JsonSerialize
@JsonDeserialize(builder = UserRole.Builder.class)
@NoArgsConstructor
@Data
@ToString(onlyExplicitlyIncluded = true)
public class UserRole {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @ToString.Include
    @Column(unique = true)
    private String roleName;

    @ManyToMany(mappedBy = "roles")
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<User> users;

    public UserRole(final Builder builder) {
        this.id = builder.id;
        this.roleName = builder.roleName;
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("hiddenfield")
    public static final class Builder {
        private Long id;
        private String roleName;

        public Builder withId(final Long id) {
            this.id = id;
            return this;
        }

        public Builder withRoleName(final String roleName) {
            this.roleName = roleName;
            return this;
        }

        public UserRole build() {
            return new UserRole(this);
        }
    }
}
