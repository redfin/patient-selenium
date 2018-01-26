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

import com.redfin.patient.selenium.impl.PsConfigImpl;
import com.redfin.patient.selenium.impl.PsDriverImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@DisplayName("An AbstractPsDriver")
final class AbstractPsDriverTest
 implements AbstractPsBaseContract<PsDriverImpl>,
            FindsElementsContract<PsDriverImpl> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public PsDriverImpl getInstance() {
        return getInstance("hello", mock(PsConfigImpl.class));
    }

    @Override
    public PsDriverImpl getInstance(String description,
                                    PsConfigImpl config) {
        return getInstance(description, config, getExecutor());
    }

    private PsDriverImpl getInstance(String description,
                                     PsConfigImpl config,
                                     DriverExecutor<WebDriver> driverExecutor) {
        return new PsDriverImpl(description, config, driverExecutor);
    }

    private PsConfigImpl getConfig() {
        return mock(PsConfigImpl.class);
    }

    @SuppressWarnings("unchecked")
    private DriverExecutor<WebDriver> getExecutor() {
        return mock(DriverExecutor.class);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    @DisplayName("whose constructor is called with a null executor throws an exception")
    void testConstructorThrowsForNullExecutor() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance("hello", mock(PsConfigImpl.class), null),
                                "Should throw an exception when the construct is called with a null executor.");
    }

    @Nested
    @DisplayName("once instantiated")
    final class BehaviorTests {

        @Test
        @DisplayName("returns the given executor for withWrappedDriver()")
        void testReturnsGivenExecutor() {
            DriverExecutor<WebDriver> executor = getExecutor();
            Assertions.assertSame(executor,
                                  getInstance("hello", getConfig(), executor).withWrappedDriver(),
                                  "Should return the given executor for withWrappedDriver().");
        }

        @Test
        @DisplayName("returns a non-null JavaScript executor")
        void testReturnsNonNullJavaScriptExecutor() {
            Assertions.assertNotNull(getInstance().execute(),
                                     "Should return a non-null JavaScript executor.");
        }

        @Test
        @DisplayName("calls quit() on the given executor for quit()")
        void testQuitCallsQuitOnExecutor() {
            DriverExecutor<WebDriver> executor = getExecutor();
            getInstance("description", getConfig(), executor).quit();
            verify(executor, times(1)).quit();
        }

        @Test
        @DisplayName("calls close() on the given executor for close()")
        void testCloseCallsCloseOnExecutor() {
            DriverExecutor<WebDriver> executor = getExecutor();
            getInstance("description", getConfig(), executor).close();
            verify(executor, times(1)).close();
        }
    }
}
