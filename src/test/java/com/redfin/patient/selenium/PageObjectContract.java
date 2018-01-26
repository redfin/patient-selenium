/*
 * Copyright: (c) 2017 Redfin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redfin.patient.selenium;

import com.redfin.contractual.Testable;
import com.redfin.patient.selenium.impl.PsConfigImpl;
import com.redfin.patient.selenium.impl.PsDriverImpl;
import com.redfin.patient.selenium.impl.PsElementImpl;
import com.redfin.patient.selenium.impl.PsElementLocatorBuilderImpl;
import com.redfin.patient.selenium.impl.PsElementLocatorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;

public interface PageObjectContract<T extends AbstractPsPageObject<WebDriver,
                                                                   WebElement,
                                                                   PsConfigImpl,
                                                                   PsDriverImpl,
                                                                   PsElementLocatorBuilderImpl,
                                                                   PsElementLocatorImpl,
                                                                   PsElementImpl>>
         extends Testable<T> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    default void initializePageObjectField(Class<?> declaringClass,
                                           String fieldName,
                                           T page,
                                           Object value) {
        try {
            Field field = declaringClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(page, value);
        } catch (Throwable thrown) {
            Assertions.fail("Unable to set the field named: " + fieldName);
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    @DisplayName("should return null from the toBeInitialized() method")
    default void testToBeInitializedReturnsNull() {
        Assertions.assertNull(getInstance().toBeInitialized(),
                              "The toBeInitialized method should return null.");
    }

    @Test
    @DisplayName("when getDriver() is called should throw an exception if the field hasn't been initialized")
    default void testGetDriverThrowsForNonInitialized() {
        Assertions.assertThrows(IllegalStateException.class,
                                () -> getInstance().getDriver(),
                                "Should throw an exception if the field is still null.");
    }

    @Test
    @DisplayName("when getDriver() is called should return the set value if the field has been initialized")
    default void testGetDriverReturnsSetValueForInitialized() {
        PsDriverImpl driver = mock(PsDriverImpl.class);
        T page = getInstance();
        initializePageObjectField(AbstractPsPageObject.class, "driver", page, driver);
        Assertions.assertSame(driver,
                              page.getDriver(),
                              "An initialized page should return the given driver.");
    }

    @Test
    @DisplayName("when getPageSearchContext() is called should throw an exception if the field hasn't been initialized")
    default void testGetPageSearchContextThrowsForNonInitialized() {
        Assertions.assertThrows(IllegalStateException.class,
                                () -> getInstance().getPageSearchContext(),
                                "Should throw an exception if the field is still null.");
    }

    @Test
    @DisplayName("when getPageSearchContext() is called should return the set value if the field has been initialized")
    default void testGetPageSearchContextReturnsSetValueForInitialized() {
        PsDriverImpl driver = mock(PsDriverImpl.class);
        T page = getInstance();
        initializePageObjectField(AbstractPsPageObject.class, "pageContext", page, driver);
        Assertions.assertSame(driver,
                              page.getPageSearchContext(),
                              "An initialized page should return the given search context.");
    }
}
