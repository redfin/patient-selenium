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

import java.util.function.Consumer;
import java.util.function.Function;

import static com.redfin.validity.Validity.validate;

/**
 * Defines a wrapping object that will use a supplier to retrieve
 * values and then execute functions with that value. This allows for
 * easy customization of behaviors for different types of executors,
 * lazy initialization, etc.
 *
 * @param <T> the type of wrapped object to be used.
 */
public interface Executor<T> {

    /**
     * Execute the {@link Consumer#accept(Object)} method with a T
     * instance supplied by the executor.
     *
     * @param consumer the {@link Consumer} to be executed.
     *                 May not be null.
     *
     * @throws IllegalArgumentException if consumer is null.
     */
    default void accept(Consumer<T> consumer) {
        validate().withMessage("Cannot execute with a null consumer.")
                  .that(consumer)
                  .isNotNull();
        apply(t -> {
            consumer.accept(t);
            return null;
        });
    }

    /**
     * Execute the {@link Function#apply(Object)} method with a T
     * instance supplied by the executor and return the result.
     *
     * @param function the {@link Function} to be executed.
     *                 May not be null.
     * @param <R>      the type of value returned by the function.
     *
     * @return the result of executing the function with the supplied value.
     *
     * @throws IllegalArgumentException if function is null.
     */
    <R> R apply(Function<T, R> function);
}
