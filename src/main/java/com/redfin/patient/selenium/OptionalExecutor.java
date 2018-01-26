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

import static com.redfin.validity.Validity.validate;

/**
 * Immutable class that allows for a chained Runnable to be executed if
 * the given boolean is true.
 */
public final class OptionalExecutor {

    private final boolean executeOther;

    /**
     * Create a new {@link OptionalExecutor} instance with the given boolean
     * value.
     *
     * @param executeOther true if the given {@link Runnable} should be
     *                     executed if {@link #orElse(Runnable)} is called.
     */
    public OptionalExecutor(boolean executeOther) {
        this.executeOther = executeOther;
    }

    /**
     * For testing
     *
     * @return true if this executor will execute a runnable
     * given to {@link #orElse(Runnable)} or false otherwise.
     */
    boolean isExecuteOther() {
        return executeOther;
    }

    /**
     * Execute the given {@link Runnable} if the boolean flag given to
     * this instance's constructor was true.
     *
     * @param runnable the {@link Runnable} that might be executed.
     *                 May not be null.
     *
     * @throws IllegalArgumentException if runnable is null.
     */
    public void orElse(Runnable runnable) {
        validate().withMessage("Cannot execute a null runnable.")
                  .that(runnable)
                  .isNotNull();
        if (executeOther) {
            runnable.run();
        }
    }
}
