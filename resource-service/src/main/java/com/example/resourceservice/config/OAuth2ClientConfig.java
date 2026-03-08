package com.example.resourceservice.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;

/*
 Create a small config that wires an OAuth2AuthorizedClientManager for the client_credentials grant
 and a Feign RequestInterceptor that automatically injects the obtained token into the Authorization header.
 */
@Configuration
public class OAuth2ClientConfig {

    @Bean
    OAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository) {

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
                .withClientRegistrationId("storage-service-client")
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
