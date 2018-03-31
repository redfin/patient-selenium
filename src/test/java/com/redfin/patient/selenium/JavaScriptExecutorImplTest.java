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
import com.redfin.patient.selenium.impl.PsElementImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@DisplayName("A JavaScriptExecutorImpl")
final class JavaScriptExecutorImplTest
 implements Testable<JavaScriptExecutorImpl<WebDriver>> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    @SuppressWarnings("unchecked")
    public JavaScriptExecutorImpl<WebDriver> getInstance() {
        return getInstance(mock(DriverExecutor.class, withSettings().extraInterfaces(JavascriptExecutor.class)));
    }

    private JavaScriptExecutorImpl<WebDriver> getInstance(DriverExecutor<WebDriver> executor) {
        return new JavaScriptExecutorImpl<>(executor);
    }

    private WebDriver getJsDriver() {
        return mock(WebDriver.class, withSettings().extraInterfaces(JavascriptExecutor.class));
    }

    private WebDriver getNonJsDriver() {
        return mock(WebDriver.class);
    }

    private DriverExecutor<WebDriver> getExecutor(WebDriver driver) {
        return new DefaultDriverExecutor<>(() -> driver);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("while instantiating")
    final class ConstructorTests {

        @Test
        void testThrowsForNullExecutor() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance(null),
                                    "Should throw an exception for a null executor.");
        }
    }

    @Nested
    @DisplayName("once instantiated")
    final class BehaviorTests {

        @Nested
        @DisplayName("when scriptWithResult(String, Object) is called")
        final class ScriptTests {

            @Test
            @DisplayName("it calls the executeScript method on the driver")
            void testCallsExpectedDriver() {
                WebDriver driver = getJsDriver();
                getInstance(getExecutor(driver)).scriptWithResult("hello");
                verify(((JavascriptExecutor) driver), times(1)).executeScript(any(), any());
            }

            @Test
            @DisplayName("it handles a null argument array")
            void testIgnoresNullArgsArray() {
                WebDriver driver = getJsDriver();
                getInstance(getExecutor(driver)).scriptWithResult("hello", (Object[]) null);
                verify(((JavascriptExecutor) driver), times(1)).executeScript(any(), any());
            }

            @Test
            @DisplayName("it unwraps wrapped elements in argument array")
            @SuppressWarnings("unchecked")
            void testConvertsWrappedElementsInArgsArray() {
                ElementExecutor<WebElement> elementExecutor = mock(ElementExecutor.class);
                PsElementImpl element = mock(PsElementImpl.class);
                when(element.withWrappedElement()).thenReturn(elementExecutor);
                WebDriver driver = getJsDriver();
                getInstance(getExecutor(driver)).scriptWithResult("hello", element);
                verify(elementExecutor, times(1)).apply(any());
            }

            @Test
            @DisplayName("it throws an exception for an empty scriptWithResult string")
            void testThrowsForEmptyString() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getInstance(getExecutor(getJsDriver())).scriptWithResult(""),
                                        "Should throw an exception for an empty scriptWithResult.");
            }

            @Test
            @DisplayName("it throws an exception for a null scriptWithResult string")
            void testThrowsForNullScriptString() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getInstance(getExecutor(getJsDriver())).scriptWithResult(null),
                                        "Should throw an exception for a null scriptWithResult.");
            }

            @Test
            @DisplayName("throws an exception if the driver isn't a javascript executor")
            @SuppressWarnings("unchecked")
            void testThrowsForNonJavascriptExecutor() {
                Assertions.assertThrows(IllegalStateException.class,
                                        () -> getInstance(getExecutor(getNonJsDriver())).scriptWithResult("hello"),
                                        "Should throw an exception for a driver that isn't a javascript executor.");
            }
        }

        @Nested
        @DisplayName("when asyncScriptWithResult(String, Object) is called")
        final class AsyncScriptTests {

            @Test
            @DisplayName("it calls the executeAsyncScript method on the driver")
            void testCallsExpectedDriver() {
                WebDriver driver = getJsDriver();
                getInstance(getExecutor(driver)).asyncScriptWithResult("hello");
                verify(((JavascriptExecutor) driver), times(1)).executeAsyncScript(any(), any());
            }

            @Test
            @DisplayName("it handles a null argument array")
            void testIgnoresNullArgsArray() {
                WebDriver driver = getJsDriver();
                getInstance(getExecutor(driver)).asyncScriptWithResult("hello", (Object[]) null);
                verify(((JavascriptExecutor) driver), times(1)).executeAsyncScript(any(), any());
            }

            @Test
            @DisplayName("it unwraps wrapped elements in argument array")
            @SuppressWarnings("unchecked")
            void testConvertsWrappedElementsInArgsArray() {
                ElementExecutor<WebElement> elementExecutor = mock(ElementExecutor.class);
                PsElementImpl element = mock(PsElementImpl.class);
                when(element.withWrappedElement()).thenReturn(elementExecutor);
                WebDriver driver = getJsDriver();
                getInstance(getExecutor(driver)).asyncScriptWithResult("hello", element);
                verify(elementExecutor, times(1)).apply(any());
            }

            @Test
            @DisplayName("it throws an exception for an empty asyncScriptWithResult string")
            void testThrowsForEmptyString() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getInstance(getExecutor(getJsDriver())).asyncScriptWithResult(""),
                                        "Should throw an exception for an empty asyncScriptWithResult.");
            }

            @Test
            @DisplayName("it throws an exception for a null asyncScriptWithResult string")
            void testThrowsForNullasyncScriptString() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getInstance(getExecutor(getJsDriver())).asyncScriptWithResult(null),
                                        "Should throw an exception for a null asyncScriptWithResult.");
            }

            @Test
            @DisplayName("throws an exception if the driver isn't a javaasyncScript executor")
            @SuppressWarnings("unchecked")
            void testThrowsForNonJavaasyncScriptExecutor() {
                Assertions.assertThrows(IllegalStateException.class,
                                        () -> getInstance(getExecutor(getNonJsDriver())).asyncScriptWithResult("hello"),
                                        "Should throw an exception for a driver that isn't a javaasyncScript executor.");
            }
        }
    }
}
