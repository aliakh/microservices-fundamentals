package com.example.resourceprocessor.endToEnd;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(Cucumber.class)
@SpringBootTest
@CucumberOptions(features = "src/test/resources/endToEnd",
        plugin = {"pretty", "html:build/test-results/test/cucumber.html"})
@CucumberContextConfiguration
public class ETETest {
}
