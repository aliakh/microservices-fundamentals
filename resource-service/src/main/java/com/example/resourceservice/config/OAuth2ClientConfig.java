package com.example.resourceservice.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.*;
import org.springframework.security.oauth2.client.web.*;
import org.springframework.security.oauth2.core.*;

/*
 Create a small config that wires an OAuth2AuthorizedClientManager for the client_credentials grant
 and a Feign RequestInterceptor that automatically injects the obtained token into the Authorization header.
 */
@Configuration
public class OAuth2ClientConfig {

    private static final String REGISTRATION_ID = "storage-service-client";

    @Bean
    OAuth2AuthorizedClientManager authorizedClientManager(
        ClientRegistrationRepository clientRegistrationRepository) {

        OAuth2AuthorizedClientService clientService =
            new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);

        AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
            new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, clientService);

        OAuth2AuthorizedClientProvider provider = OAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials()
            .build();

        manager.setAuthorizedClientProvider(provider);
        return manager;
    }

    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor(OAuth2AuthorizedClientManager clientManager) {
        return template -> {
            // Build a client request with the registration id for client_credentials
            OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest
                .withClientRegistrationId(REGISTRATION_ID)
                .principal("resource-service") // synthetic principal for client_credentials
                .build();

            OAuth2AuthorizedClient client = clientManager.authorize(request);
            if (client == null || client.getAccessToken() == null) {
                throw new OAuth2AuthorizationException(
                    new OAuth2Error("authorization_failed", "Failed to acquire access token", null));
            }

            String tokenValue = client.getAccessToken().getTokenValue();
            template.header("Authorization", "Bearer " + tokenValue);
        };
    }
}
