package com.example.storageservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;

@Configuration
public class OAuth2ServerConfig {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2ServerConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(smc ->
                smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(amrmr ->
                amrmr.requestMatchers("/actuator/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/**").hasAnyRole("user", "admin")
                    .requestMatchers(HttpMethod.POST, "/**").hasRole("admin")
                    .requestMatchers(HttpMethod.DELETE, "/**").hasRole("admin")
                    .anyRequest().authenticated()
            )
            .oauth2ResourceServer(rsc ->
                rsc.jwt(
                    jc ->
                        jc.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // combine Keycloak roles + standard scopes
            var authorities = new ArrayList<GrantedAuthority>();

            // include Keycloak realm/client roles
            authorities.addAll(new KeycloakRealmRoleConverter().convert(jwt));

            // include OAuth2 scopes (e.g., SCOPE_openid, SCOPE_profile, etc.)
            var scopesConverter = new JwtGrantedAuthoritiesConverter();
            authorities.addAll(scopesConverter.convert(jwt));

            logger.info("Granted authorities: {}", authorities);
            return authorities;
        });
        return converter;
    }
}
