package com.redfin.selenium;

import com.redfin.selenium.contracts.FindsElementsTestContract;
import com.redfin.selenium.contracts.WrappedExecutorTestContract;
import com.redfin.selenium.implementation.TestPatientConfig;
import com.redfin.selenium.implementation.TestPatientDriver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.openqa.selenium.WebDriver;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.redfin.selenium.TestMocks.getMockConfig;
import static com.redfin.selenium.TestMocks.getMockDriverSupplier;
import static org.mockito.Mockito.mock;

@DisplayName("An AbstractPatientDriver")
final class AbstractPatientDriverTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("while being instantiated")
    final class ConstructorTest {

        @Test
        @DisplayName("instantiates successfully when the constructor is called with valid arguments")
        void testConstructedWithValidArguments() {
            Assertions.assertNotNull(getInstance(),
                                     "Should be able to instantiate with valid arguments for the constructor");
        }

        @ParameterizedTest
        @ArgumentsSource(InvalidConstructorArguments.class)
        @DisplayName("throws an exception when the constructor is called with invalid arguments")
        void testConstructedWithInvalidArguments(TestPatientConfig config,
                                                 String description,
                                                 Supplier<WebDriver> webDriverSupplier) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance(config, description, webDriverSupplier),
                                    "Should throw an exception with invalid arguments for the constructor");
        }
    }

    @Nested
    @DisplayName("once instantiated")
    final class BehaviorTest {

        @Test
        @DisplayName("returns expected values from the getter methods")
        void testGettersReturnGivenValues() {
            TestPatientConfig config = getMockConfig();
            String description = "fooBarBaz";
            Supplier<WebDriver> driverSupplier = getMockDriverSupplier();
            TestPatientDriver driver = getInstance(config, description, driverSupplier);
            Assertions.assertAll(() -> Assertions.assertSame(config, driver.getConfig(), "Should have returned the given config"),
                                 () -> Assertions.assertSame(description, driver.getDescription(), "Should have returned the given description"),
                                 () -> Assertions.assertSame(driverSupplier, driver.getWebDriverSupplier(), "Should have returned the given driver supplier"),
                                 () -> Assertions.assertSame(description, driver.toString(), "The toString() method should have returned the given description"));
        }

        @Test
        @DisplayName("starts with a null driver cache")
        void testStartsWithNullCache() {
            TestPatientConfig config = getMockConfig();
            String description = "fooBarBaz";
            WebDriver driver = mock(WebDriver.class);
            Supplier<WebDriver> driverSupplier = () -> driver;
            Assertions.assertNull(getInstance(config, description, driverSupplier).getCachedDriver(), "Should start with a null cache");
        }

        @Test
        @DisplayName("starts with a null driver cache")
        void testExtractsExpectedValueFromSupplierForCache() {
            TestPatientConfig config = getMockConfig();
            String description = "fooBarBaz";
            WebDriver driver = mock(WebDriver.class);
            Supplier<WebDriver> driverSupplier = () -> driver;
            TestPatientDriver instance = getInstance(config, description, driverSupplier);
            instance.apply(d -> d);
            Assertions.assertSame(driver, instance.getCachedDriver(), "Should have extracted the expected driver from the supplier");
        }

        @Test
        @DisplayName("returns the given cached value")
        void testReturnsGivenCachedValue() {
            TestPatientConfig config = getMockConfig();
            String description = "fooBarBaz";
            WebDriver driver = mock(WebDriver.class);
            Supplier<WebDriver> driverSupplier = getMockDriverSupplier();
            TestPatientDriver instance = getInstance(config, description, driverSupplier);
            instance.setCachedDriver(driver);
            Assertions.assertSame(driver, instance.getCachedDriver(), "Should return the given value from the cache");
        }
    }

    @Nested
    @DisplayName("as a WrappedExecutor")
    final class AsWrappedExecutorTest implements WrappedExecutorTestContract<WebDriver, TestPatientDriver> {

        @Override
        public Class<WebDriver> getWrappedObjectClass() {
            return WebDriver.class;
        }

        @Override
        public TestPatientDriver getExecutor(WebDriver wrappedObject) {
            TestPatientDriver instance = getInstance();
            instance.setCachedDriver(wrappedObject);
            return instance;
        }
    }

    @Nested
    @DisplayName("as a FindsElements")
    final class AsFindsElementTest implements FindsElementsTestContract<TestPatientDriver> {

        @Override
        public TestPatientDriver getFindsElementsInstance() {
            return getInstance();
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static TestPatientDriver getInstance() {
        return getInstance(getMockConfig(), "hello", getMockDriverSupplier());
    }

    private static TestPatientDriver getInstance(TestPatientConfig config,
                                                 String description,
                                                 Supplier<WebDriver> webDriverSupplier) {
        return new TestPatientDriver(config, description, webDriverSupplier);
    }

    private static final class InvalidConstructorArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(null, "hello", getMockDriverSupplier()),
                             Arguments.of(getMockConfig(), null, getMockDriverSupplier()),
                             Arguments.of(getMockConfig(), "", getMockDriverSupplier()),
                             Arguments.of(getMockConfig(), "hello", null));
        }
    }
}
