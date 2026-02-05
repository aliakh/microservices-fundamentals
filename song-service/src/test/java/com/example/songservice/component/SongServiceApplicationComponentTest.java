package com.example.songservice.component;

import org.junit.platform.suite.api.*;
import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.example.songservice.component")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/test-results/test/cucumber.html")
public class SongServiceApplicationComponentTest {
}
