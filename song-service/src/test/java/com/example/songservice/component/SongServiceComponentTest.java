package com.example.songservice.component;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@RunWith(Cucumber.class)
@SpringBootTest(classes = TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@CucumberOptions(features = "src/test/resources/component", plugin = {"pretty", "html:target/test-results/test/cucumber.html"})
@CucumberContextConfiguration
public class SongServiceComponentTest {
}