package com.example.resourceservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
public class OAuth2ClientConfig2 {

//    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository) {
        return new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository,
                new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository)
        );
    }
/*
It does not set a provider (e.g., clientCredentials()), so authorize(...) will return null.
It creates a new InMemoryOAuth2AuthorizedClientService instead of reusing the one auto-configured by Spring Boot.
Prefer injecting the existing OAuth2AuthorizedClientService bean.
 */
    @Bean
    OAuth2AuthorizedClientManager authorizedClientManager(
        ClientRegistrationRepository clientRegistrationRepository,
        OAuth2AuthorizedClientService clientService) {

        var provider = OAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials()   // <-- IMPORTANT
            .build();

        var manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, clientService);
        manager.setAuthorizedClientProvider(provider);
        return manager;
    }
}
/*
This is the same manager we referenced in the Feign RequestInterceptor.
Without it, your fallback to client‑credentials cannot acquire a token.


When could you skip this bean?

If your Feign calls always forward the end-user JWT from the SecurityContext and you never fall back to client‑credentials, then you could omit the manager.
In our earlier design, we do fall back to client‑credentials—so keep this bean.


Avoid duplicate beans
If you already defined an authorizedClientManager bean (e.g., in FeignConfig), remove one to avoid duplicate bean definition errors. Define it once and inject it where needed (e.g., your Feign interceptor).
If you want, paste your current FeignConfig here and I’ll make sure the wiring is consistent end‑to‑end.
 */
