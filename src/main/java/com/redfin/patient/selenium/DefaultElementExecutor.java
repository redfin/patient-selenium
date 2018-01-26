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

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.expect;
import static com.redfin.validity.Validity.validate;

/**
 * The default implementation of the {@link ElementExecutor} interface
 * for the Patient-Selenium library. It customizes the behavior of the
 * wrapped execution methods by catching {@link StaleElementReferenceException}
 * and retrying up to a maximum number of times.
 *
 * @param <W> the type of {@link WebElement} that is wrapped.
 */
public final class DefaultElementExecutor<W extends WebElement>
           extends AbstractCachingExecutor<W>
        implements ElementExecutor<W> {

    private static final int DEFAULT_MAX_STALE_RETRIES = 3;

    private final int maxStaleRetries;

    /**
     * Creates a new {@link DefaultElementExecutor} instance with
     * the given initial element to be cached and the supplier to
     * retrieve an element when needed. It default the number of times
     * that a command will be retried if a {@link StaleElementReferenceException}
     * is caught to 3.
     *
     * @param initialElement  the initial element to be held in the cache.
     *                        May not be null.
     * @param elementSupplier the supplier to retrieve additional elements
     *                        as needed.
     *                        May not be null.
     *
     * @throws IllegalArgumentException if either initialElement or elementSupplier are null.
     */
    public DefaultElementExecutor(W initialElement,
                                  Supplier<W> elementSupplier) {
        this(initialElement, elementSupplier, DEFAULT_MAX_STALE_RETRIES);
    }

    /**
     * Creates a new {@link DefaultElementExecutor} instance with
     * the given initial element to be cached and the supplier to
     * retrieve an element when needed.
     *
     * @param initialElement  the initial element to be held in the cache.
     *                        May not be null.
     * @param elementSupplier the supplier to retrieve additional elements
     *                        as needed.
     *                        May not be null.
     * @param maxStaleRetries the maximum number of times to retry a
     *                        command if a {@link StaleElementReferenceException} is caught.
     *                        May not be less than 1.
     *
     * @throws IllegalArgumentException if either initialElement or elementSupplier are null
     *                                  or if maxStaleRetries is less than 1.
     */
    public DefaultElementExecutor(W initialElement,
                                  Supplier<W> elementSupplier,
                                  int maxStaleRetries) {
        super(validate().withMessage("Cannot use a null initial element")
                        .that(initialElement)
                        .isNotNull(),
              elementSupplier);
        this.maxStaleRetries = validate().withMessage("Cannot use a maxStaleRetries value that is less than 1.")
                                         .that(maxStaleRetries)
                                         .isAtLeast(1);
    }

    /**
     * @return the maximum stale retries for this executor.
     */
    int getMaxStaleRetries() {
        return maxStaleRetries;
    }

    @Override
    public <R> R apply(Function<W, R> function) {
        validate().withMessage("Cannot execute with a null function.")
                  .that(function)
                  .isNotNull();
        StaleElementReferenceException exception = null;
        // up to equal the retry count since the first attempt isn't a retry
        for (int i = 0; i <= maxStaleRetries; i++) {
            try {
                W element = getObject();
                return function.apply(element);
            } catch (StaleElementReferenceException stale) {
                exception = stale;
                // We need to re-locate the element, clear the cache
                clearCachedElement();
            }
        }
        throw expect().withMessage("Max retries reached but the exception is null.")
                      .that(exception)
                      .isNotNull();
    }

    @Override
    public void clearCachedElement() {
        setCachedObject(null);
    }
}
