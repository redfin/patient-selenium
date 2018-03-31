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
import com.redfin.patient.selenium.impl.PsDriverImpl;
import com.redfin.patient.selenium.impl.PsElementLocatorImpl;
import com.redfin.patient.selenium.impl.PsPageObjectImpl;
import com.redfin.patient.selenium.impl.PsPageObjectInitializerImpl;
import com.redfin.patient.selenium.impl.PsWidgetObjectImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("An PageObjectInitializer")
final class PageObjectInitializerTest
 implements Testable<PsPageObjectInitializerImpl> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public PsPageObjectInitializerImpl getInstance() {
        return getInstance(mock(PsDriverImpl.class), PsElementLocatorImpl.class);
    }

    private PsPageObjectInitializerImpl getInstance(PsDriverImpl driver,
                                                    Class<PsElementLocatorImpl> clazz) {
        return new PsPageObjectInitializerImpl(driver, clazz);
    }

    private static final class TestPage
                       extends PsPageObjectImpl {

        // These fields should be touched by the initializer
        private final InnerTestPage innerPage = new InnerTestPage();
        private final PsElementLocatorImpl outerElement = toBeInitialized();
    }

    private static final class InnerTestPage
                       extends PsPageObjectImpl {

        private final TestWidget widget = new TestWidget();
        private final PsElementLocatorImpl element = toBeInitialized();
    }

    private static final class TestWidget
                       extends PsWidgetObjectImpl {

        private final PsElementLocatorImpl element = toBeInitialized();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("while instantiating")
    final class ConstructorTests {

        @Test
        @DisplayName("throws an exception for a null driver")
        void testThrowsExceptionForNullDriver() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance(null, PsElementLocatorImpl.class),
                                    "Should throw an exception for a null driver.");
        }

        @Test
        @DisplayName("throws an exception for a null element locator class")
        void testThrowsForNullLocatorClass() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance(mock(PsDriverImpl.class), null),
                                    "Should throw an exception for a null element locator class.");
        }
    }

    @Nested
    @DisplayName("once instantiated")
    final class BehaviorTests {

        @Test
        @DisplayName("returns the given values")
        void testReturnsGivenValues() {
            PsDriverImpl driver = mock(PsDriverImpl.class);
            Class<PsElementLocatorImpl> clazz = PsElementLocatorImpl.class;
            PsPageObjectInitializerImpl initializer = getInstance(driver, clazz);
            Assertions.assertAll(() -> Assertions.assertSame(driver,
                                                             initializer.getDriver(),
                                                             "Should return the given driver."),
                                 () -> Assertions.assertSame(clazz,
                                                             initializer.getElementLocatorClass(),
                                                             "Should return the given element locator class."));
        }

        @Test
        @DisplayName("when initialize(Page) is called it calls initialize(Page, Context) with the driver as the context")
        void testInitializeCallsInitializeWithDriverAsContext() {
            PsDriverImpl driver = mock(PsDriverImpl.class);
            Class<PsElementLocatorImpl> clazz = PsElementLocatorImpl.class;
            PsPageObjectInitializerImpl initializer = spy(getInstance(driver, clazz));
            TestPage page = new TestPage();
            initializer.initialize(page);
            verify(initializer, times(1)).initialize(page, driver);
        }

        @Test
        @DisplayName("when it initializes a page object it initializes null element locator fields and recursively initializes non-null page object fields")
        void testInitializerInitializesExpectedFieldsRecursively() {
            TestPage page = new TestPage();
            getInstance().initialize(page);
            Assertions.assertAll(() -> Assertions.assertNotNull(page.outerElement),
                                 () -> Assertions.assertNotNull(page.innerPage.element),
                                 () -> Assertions.assertNotNull(page.innerPage.widget.getBaseElementLocator()),
                                 () -> Assertions.assertNotNull(page.innerPage.widget.element));
        }
    }
}
