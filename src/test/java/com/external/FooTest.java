package com.external;

import com.redfin.patient.selenium.internal.PsConfig;
import com.redfin.patient.selenium.PatientWebDriver;
import com.redfin.patient.selenium.internal.DefaultDriverExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class FooTest {

    private static final String[] PATH = {System.getProperty("user.home"), "webdrivers", "%s"};
    private static final String CHROME_DRIVER_PROPERTY_KEY = "webdriver.chrome.driver";
    private static final String CHROME_DRIVER_NAME = "chromedriver";
    private static final String GECKO_DRIVER_PROPERTY_KEY = "webdriver.gecko.driver";
    private static final String GECKO_DRIVER_NAME = "geckodriver";

    static {
        String formatPath = Arrays.stream(PATH).collect(Collectors.joining(File.separator));
        System.setProperty(CHROME_DRIVER_PROPERTY_KEY, String.format(formatPath, CHROME_DRIVER_NAME));
        System.setProperty(GECKO_DRIVER_PROPERTY_KEY, String.format(formatPath, GECKO_DRIVER_NAME));
    }

    private PatientWebDriver driver;

    @BeforeEach
    void setup() {
        driver = new PatientWebDriver("chrome",
                                      new DefaultDriverExecutor<>(ChromeDriver::new),
                                      PsConfig.builder().build());
    }

    @Test
    void testFoo() throws Exception {
        driver.withWrappedDriver().accept(d -> d.get("https://www.bing.com"));
        driver.find().by(By.tagName("html")).get()
              .find().by(By.name("q")).get()
              .withWrappedElement()
              .accept(e -> e.sendKeys("hello, world", Keys.ENTER));
    }

    @AfterEach
    void tearDown() throws Exception {
        Thread.sleep(2_000);
        driver.quit();
    }
}
