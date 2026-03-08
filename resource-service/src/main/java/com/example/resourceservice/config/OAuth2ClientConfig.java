package com.example.resourceservice.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.*;

/*
 Create a small config that wires an OAuth2AuthorizedClientManager for the client_credentials grant
 and a Feign RequestInterceptor that automatically injects the obtained token into the Authorization header.
 */
@Configuration
public class OAuth2ClientConfig {

    @Bean
    OAuth2AuthorizedClientManager authorizedClientManager(
        ClientRegistrationRepository clientRegistrationRepository,
        OAuth2AuthorizedClientService clientService) {

//        OAuth2AuthorizedClientService clientService =
//            new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);

        AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
            new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, clientService);

        OAuth2AuthorizedClientProvider provider = OAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials()
            .build();

        manager.setAuthorizedClientProvider(provider);
        return manager;
    }

    /**
     * Feign interceptor that:
     * 1) forwards current user's JWT if present, else
     * 2) obtains client-credentials token using registration "storage-service-client".
     *
     * If you always want client-credentials and never want to forward the user’s token,
     * remove the resolveCurrentRequestJwt() branch and always call acquireClientCredentialsToken(...).
     */
    @Bean
    RequestInterceptor oauth2FeignRequestInterceptor(OAuth2AuthorizedClientManager authorizedClientManager) {
        return template -> {
            // 1) Try to reuse current request's JWT (user propagation)
//            String bearer = resolveCurrentRequestJwt();
//            if (bearer == null) {
                // 2) Fallback to client-credentials
            String   bearer = acquireClientCredentialsToken(authorizedClientManager, "storage-service-client");
//            }

            if (bearer != null) {
                template.header(HttpHeaders.AUTHORIZATION, "Bearer " + bearer);
            }
        };
    }

//    private String resolveCurrentRequestJwt() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth instanceof JwtAuthenticationToken jwtAuth && jwtAuth.getToken() != null) {
//            return jwtAuth.getToken().getTokenValue();
//        }
//        return null;
//    }

    private String acquireClientCredentialsToken(OAuth2AuthorizedClientManager manager, String registrationId) {
        // principal can be anything for client_credentials; it is not used for end-user context
        var principal = new AnonymousAuthenticationToken("key", "resource-service",
            AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

        var authorizeRequest = OAuth2AuthorizeRequest
            .withClientRegistrationId(registrationId)
            .principal(principal)
            .build();

        OAuth2AuthorizedClient client = manager.authorize(authorizeRequest);
        OAuth2AccessToken token = client != null ? client.getAccessToken() : null;
        return token != null ? token.getTokenValue() : null;
    }
}

/*
    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor(OAuth2AuthorizedClientManager authorizedClientManager) {
        return template -> {
            // Build a client request with the registration id for client_credentials
            OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest
                .withClientRegistrationId("storage-service-client")
                .principal("resource-service") // synthetic principal for client_credentials
                .build();

            OAuth2AuthorizedClient client = authorizedClientManager.authorize(request);
            if (client == null || client.getAccessToken() == null) {
                throw new OAuth2AuthorizationException(
                    new OAuth2Error("authorization_failed", "Failed to acquire access token for storage-service", null));
            }

            String tokenValue = client.getAccessToken().getTokenValue();
            template.header("Authorization", "Bearer " + tokenValue);
        };
    }

 */

