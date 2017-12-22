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

package com.redfin.patient.selenium.internal;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.expect;
import static com.redfin.validity.Validity.validate;

public final class DefaultElementExecutor<W extends WebElement>
           extends AbstractCachingExecutor<W>
        implements ElementExecutor<W> {

    private static final int MAX_STALE_RETRIES = 3;

    public DefaultElementExecutor(W initialElement,
                                  Supplier<W> elementSupplier) {
        super(validate().withMessage("Cannot use a null initial element")
                        .that(initialElement)
                        .isNotNull(),
              elementSupplier);
    }

    @Override
    public <R> R apply(Function<W, R> function) {
        validate().withMessage("Cannot execute with a null function.")
                  .that(function)
                  .isNotNull();
        RuntimeException exception = null;
        for (int i = 0; i < MAX_STALE_RETRIES; i++) {
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
