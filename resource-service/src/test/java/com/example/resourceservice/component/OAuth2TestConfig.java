package com.example.resourceservice.component;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import static org.mockito.Mockito.mock;

@Configuration
public class OAuth2TestConfig {

    @Bean
    @Primary
    public ClientRegistrationRepository clientRegistrationRepositoryMock() {
        return mock(ClientRegistrationRepository.class);
    }

    @Bean
    @Primary
    public OAuth2AuthorizedClientService authorizedClientServiceMock() {
        return mock(OAuth2AuthorizedClientService.class);
    }
}
