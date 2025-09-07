package hu.bsstudio.raktr.security;

import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.User;
import hu.bsstudio.raktr.model.UserRole;
import hu.bsstudio.raktr.service.UserDataService;
import hu.bsstudio.raktr.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class BssLoginJwtAuthenticationConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final UserDataService userDataService;
    private final UserRoleService userRoleService;

    private final List<String> roleCache = new ArrayList<>();

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        User user = findOrCreateUser(jwt);
        user = setUserRoles(user, jwt);
        return getGrantedAuthorities(user.getRoles());
    }

    private User findOrCreateUser(Jwt jwt) {
        String username = jwt.getClaims().get("preferred_username").toString();
        try {
            return userDataService.getByUsername(username);
        } catch (ObjectNotFoundException e) {
            User user = User.builder()
                    .withUsername(username)
                    .withFamilyName(jwt.getClaims().get("family_name").toString())
                    .withGivenName(jwt.getClaims().get("given_name").toString())
                    .withNickName(jwt.getClaims().get("given_name").toString())
                    .withPersonalId("")
                    .build();

            String familyNameads = user.getFamilyName();
            return userDataService.createUser(user);
        }
    }

    private User setUserRoles(User user, Jwt jwt) {
        JSONArray array = (JSONArray) jwt.getClaims().get("groups");

        for (Object role : array.stream()
                .map(role -> role.toString().equals("Öregtag") ? "Stúdiós" : role.toString())
                .distinct()
                .toArray()) {
            if(!roleCache.contains(role.toString())) {
                UserRole foundRole = userRoleService.getRole(role.toString());

                if(foundRole == null) {
                    log.info("Role not found, creating it: {}", role.toString());

                    userRoleService.createRole(role.toString());
                }

                roleCache.add(role.toString());
            }
        }

        Set<UserRole> userRoles = array.stream()
                .map(role -> role.toString().equals("Öregtag") ? "Stúdiós" : role.toString())
                .map(userRoleService::getRole)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));
        return userDataService.setUserRoles(user, userRoles);
    }

    private Collection<GrantedAuthority> getGrantedAuthorities(final Set<UserRole> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (roles == null) {
            return authorities;
        }

        for (var role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }

        return authorities;
    }
}
