package com.redfin.selenium;

import com.redfin.selenium.implementation.TestPatientConfig;
import com.redfin.selenium.implementation.TestPatientDriver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Integration test")
final class IntegrationTest {

    @Test
    @DisplayName("that an element locates expected child elements")
    void testElementLocationFindsExpectedElement() {
        String expectedFinalText = "fooBarBaz";
        WebElement innerElement = mock(WebElement.class);
        when(innerElement.getText()).thenReturn(expectedFinalText);
        WebElement outerElement = mock(WebElement.class);
        when(outerElement.findElements(any())).thenReturn(Collections.singletonList(innerElement));
        WebDriver driver = mock(WebDriver.class);
        when(driver.findElements(any())).thenReturn(Collections.singletonList(outerElement));
        TestPatientDriver instance = new TestPatientDriver(TestPatientConfig.builder().build(),
                                                           "driverDescription",
                                                           () -> driver);
        String actualFinalText = instance.find(By.cssSelector(".foo"))
                                         .get()
                                         .find(By.cssSelector(".bar"))
                                         .get()
                                         .apply(WebElement::getText);
        Assertions.assertEquals(expectedFinalText,
                                actualFinalText,
                                "Should have found the expected final text.");
    }
}
