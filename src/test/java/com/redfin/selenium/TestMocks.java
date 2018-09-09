package com.redfin.selenium;

import com.redfin.patience.PatientWait;
import com.redfin.selenium.implementation.TestPatientConfig;
import com.redfin.selenium.implementation.TestPatientDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class TestMocks {

    @SuppressWarnings("unchecked")
    static TestPatientConfig getMockConfig() {
        TestPatientConfig config = mock(TestPatientConfig.class);
        PatientWait wait = mock(PatientWait.class);
        Duration timeout = mock(Duration.class);
        Predicate<WebElement> filter = getMockFilter();
        when(config.getDefaultWait()).thenReturn(wait);
        when(config.getDefaultTimeout()).thenReturn(timeout);
        when(config.getDefaultFilter()).thenReturn(filter);
        when(config.getMaxElementActionAttempts()).thenReturn(3);
        when(config.isIgnoredLookupException(any())).thenReturn(false);
        when(config.isIgnoredActionException(any())).thenReturn(false);
        return config;
    }

    @SuppressWarnings("unchecked")
    static Supplier<WebDriver> getMockDriverSupplier() {
        return mock(Supplier.class);
    }

    @SuppressWarnings("unchecked")
    static Supplier<Optional<WebElement>> getMockElementSupplier() {
        return mock(Supplier.class);
    }

    @SuppressWarnings("unchecked")
    static Supplier<List<WebElement>> getMockElementListSupplier() {
        return mock(Supplier.class);
    }

    @SuppressWarnings("unchecked")
    static Predicate<WebElement> getMockFilter() {
        return mock(Predicate.class);
    }

    private TestMocks() {
        throw new AssertionError("Cannot instantiate a static class");
    }
}
