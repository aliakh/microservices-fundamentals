package com.example.storageservice.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null || realmAccess.isEmpty()) {
            return Collections.emptyList();
        }

        Collection<String> roles = (Collection<String>) realmAccess.get("roles");
        if (roles == null) {
            return Collections.emptyList();
        }

        return roles.stream()
                .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName))
                .collect(Collectors.toSet());
    }
}
/*
public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Set<String> roles = new HashSet<>();

        // 1) Realm roles (default Keycloak)
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null) {
            Object realmRoles = realmAccess.get("roles");
            if (realmRoles instanceof Collection<?> rr) {
                rr.stream().filter(String.class::isInstance).map(String.class::cast).forEach(roles::add);
            }
        }

        // 2) Optional: top-level "roles" (if a custom mapper adds it)
        Object topLevelRoles = jwt.getClaim("roles");
        if (topLevelRoles instanceof Collection<?> tr) {
            tr.stream().filter(String.class::isInstance).map(String.class::cast).forEach(roles::add);
        }

        // 3) Optional: client roles (resource_access.<clientId>.roles)
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess != null) {
            resourceAccess.forEach((clientId, value) -> {
                if (value instanceof Map<?, ?> m) {
                    Object clientRoles = m.get("roles");
                    if (clientRoles instanceof Collection<?> cr) {
                        cr.stream().filter(String.class::isInstance).map(String.class::cast).forEach(roles::add);
                    }
                }
            });
        }

        return roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .collect(Collectors.toSet());
    }
}
 */