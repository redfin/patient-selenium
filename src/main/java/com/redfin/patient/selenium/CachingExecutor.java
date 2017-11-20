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

public interface CachingExecutor<T> {

    default void accept(Consumer<T> consumer) {
        validate().withMessage("Cannot execute with a null consumer.")
                  .that(consumer)
                  .isNotNull();
        apply(t -> {
            consumer.accept(t);
            return null;
        });
    }

    <R> R apply(Function<T, R> function);

    void clearCache();
}
