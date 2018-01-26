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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("An OptionalExecutor")
final class OptionalExecutorTest
 implements Testable<OptionalExecutor> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public OptionalExecutor getInstance() {
        return getInstance(true);
    }

    private OptionalExecutor getInstance(boolean executeOther) {
        return new OptionalExecutor(executeOther);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("once instantiated")
    final class BehaviorTests {

        @ParameterizedTest
        @DisplayName("should return the given executeOther flag")
        @ValueSource(strings = {"true", "false"})
        void testReturnsGivenExecuteOtherFlag(boolean executeOther) {
            Assertions.assertEquals(executeOther,
                                    getInstance(executeOther).isExecuteOther(),
                                    "Should return the given executeOther flag.");
        }

        @Nested
        @DisplayName("when calling orElse(Runnable)")
        final class OrElseTests {

            @Test
            @DisplayName("with a true executeOther it should execute the runnable")
            void testOrElseWithTrueExecuteOtherExecutesRunnable() {
                Runnable runnable = mock(Runnable.class);
                getInstance(true).orElse(runnable);
                verify(runnable, times(1)).run();
            }

            @Test
            @DisplayName("with a false executeOther it should not execute the runnable")
            void testOrElseWithFalseExecuteOtherDoesNotExecuteRunnable() {
                Runnable runnable = mock(Runnable.class);
                getInstance(false).orElse(runnable);
                verify(runnable, times(0)).run();
            }

            @Test
            @DisplayName("should throw an exception for a null runnable")
            void testThrowsForNullRunnable() {
                Assertions.assertThrows(IllegalArgumentException.class,
                                        () -> getInstance().orElse(null),
                                        "Should throw for orElse with a null runnable.");
            }
        }
    }
}
