package com.example.resourceservice.cucumber;

import com.example.resourceservice.AbstractIntegrationTest;
import com.example.resourceservice.cucumber.config.TestConfig;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = TestConfig.class)
@ActiveProfiles("test")
@CucumberContextConfiguration
public class CucumberConfig extends AbstractIntegrationTest {
}
