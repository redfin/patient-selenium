package com.redfin.selenium.contracts;

import com.redfin.selenium.FindsElements;
import com.redfin.selenium.implementation.TestPatientConfig;
import com.redfin.selenium.implementation.TestPatientElement;
import com.redfin.selenium.implementation.TestPatientElementLocator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * A test contract for types that implement the {@link FindsElements}
 * interface.
 *
 * @param <F> the type of the implementing class under test.
 */
public interface FindsElementsTestContract<F extends FindsElements<WebElement, TestPatientConfig, TestPatientElementLocator, TestPatientElement>> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    @DisplayName("returns a non-null element locator instance")
    default void testReturnsNonNullForNonNullBy() {
        Assertions.assertNotNull(getFindsElementsInstance().find(By.cssSelector(".foo")),
                                 "Should have returned a non-null element locator instance for a non-null By locator");
    }

    @Test
    @DisplayName("throws exception for a null By locator")
    default void testThrowsForNullBy() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getFindsElementsInstance().find(null),
                                "Should have thrown an exception for calling find(By) with a null argument");
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * @return an instance of the type under test that implements the {@link FindsElements}
     * interface.
     */
    F getFindsElementsInstance();
}
