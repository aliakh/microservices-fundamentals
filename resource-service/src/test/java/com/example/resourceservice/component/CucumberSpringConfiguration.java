package com.example.resourceservice.component;

import com.example.resourceservice.AbstractTestcontainersTest;
import com.example.resourceservice.config.KafkaTestConfig;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(KafkaTestConfig.class)
public class CucumberSpringConfiguration extends AbstractTestcontainersTest {
}
