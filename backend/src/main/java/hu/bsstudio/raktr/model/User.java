package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "users")
@JsonSerialize
@JsonDeserialize(builder = User.Builder.class)
@NoArgsConstructor
@Data
@SuppressWarnings("checkstyle:DesignForExtension")
public class User implements UserDetails {

    @Id
    @Setter(AccessLevel.PROTECTED)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String username;

    private String nickName;

    @NotNull
    private String familyName;

    @NotNull
    private String givenName;

    @NotNull
    private String personalId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<UserRole> roles;

    public User(final Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.nickName = builder.nickName;
        this.familyName = builder.familyName;
        this.givenName = builder.givenName;
        this.personalId = builder.personalId;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @SuppressWarnings("hiddenfield")
    public static final class Builder {
        private Long id;
        private String username;
        private String nickName;
        private String familyName;
        private String givenName;
        private String personalId;

        public Builder withId(final Long id) {
            this.id = id;
            return this;
        }

        public Builder withUsername(final String ldapUid) {
            this.username = ldapUid;
            return this;
        }

        public Builder withNickName(final String nickName) {
            this.nickName = nickName;
            return this;
        }

        public Builder withFamilyName(final String familyName) {
            this.familyName = familyName;
            return this;
        }

        public Builder withGivenName(final String givenName) {
            this.givenName = givenName;
            return this;
        }

        public Builder withPersonalId(final String personalId) {
            this.personalId = personalId;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
