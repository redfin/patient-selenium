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
import org.junit.jupiter.api.Test;

public interface ExecutorContract<E extends Executor<?>>
         extends Testable<E> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    @DisplayName("returns when accept(Consumer) is called with a non-null consumer")
    default void testConsumerWithNonNullReturns() {
        getInstance().accept(t -> {});
    }

    @Test
    @DisplayName("throws an exception when accept(Consumer) is called with a null consumer")
    default void testConsumerWithNullThrows() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance().accept(null),
                                "Should throw an exception if accept(Consumer) is called with a null consumer.");
    }

    @Test
    @DisplayName("returns expected value when apply(Function) is called")
    default void testFunctionWithNonNullReturnsExpectedObject() {
        String result = "hello";
        Assertions.assertEquals(result,
                                getInstance().apply(t -> result),
                                "The function should return the expected result when calling apply(Function)");
    }

    @Test
    @DisplayName("throws an exception when apply(Function) is called with a null function")
    default void testFunctionWithNullThrows() {
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> getInstance().apply(null),
                                "Should throw an exception if apply(Function) is called with a null function.");
    }
}
