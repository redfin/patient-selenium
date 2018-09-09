package com.redfin.selenium;

import com.redfin.selenium.implementation.TestBasePageObject;
import com.redfin.selenium.implementation.TestPatientDriver;
import com.redfin.selenium.implementation.TestPatientElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

@DisplayName("An AbstractBasePageObject")
final class AbstractBasePageObjectTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    @DisplayName("can be instantiated")
    void testCanInstantiate() {
        Assertions.assertNotNull(getInstance(),
                                 "Should be able to instantiate");
    }

    @Test
    @DisplayName("returns the set driver")
    @SuppressWarnings("unchecked")
    void testReturnsGivenElement() {
        TestPatientDriver driver = mock(TestPatientDriver.class);
        TestPageObject page = getInstance();
        setDriver(page, driver);
        Assertions.assertSame(driver,
                              page.getDriver(),
                              "Should return the given base driver");
    }

    @Test
    @DisplayName("throws exception if initialized twice")
    void testThrowsForRepeatedInitialization() {
        TestPatientDriver driver = mock(TestPatientDriver.class);
        TestPageObject page = getInstance();
        setDriver(page, driver);
        Assertions.assertThrows(PageObjectInitializationException.class,
                                () -> setDriver(page, driver),
                                "Should throw an exception if initialized twice");
    }

    @Test
    @DisplayName("throws exception if not initialized")
    void testThrowsForNotInitialized() {
        Assertions.assertThrows(PageObjectInitializationException.class,
                                () -> getInstance().getDriver(),
                                "Should throw an exception for getDriver() if not set");
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @SuppressWarnings("unchecked")
    private static void setDriver(TestPageObject page,
                                  TestPatientDriver driver) {
        ((AbstractBasePageObject<?, ?, ?, TestPatientDriver, ?, ?>) page).setDriver(driver);
    }

    private static TestPageObject getInstance() {
        return new TestPageObject();
    }

    private static final class TestPageObject
                       extends TestBasePageObject {}
}
