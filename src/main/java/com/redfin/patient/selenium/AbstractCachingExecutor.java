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

import java.util.function.Function;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.expect;
import static com.redfin.validity.Validity.validate;

/**
 * Base class for a concrete {@link Executor} implementation
 * that should cache it's wrapped inner instance.
 *
 * @param <T> the type that is wrapped.
 */
public abstract class AbstractCachingExecutor<T>
           implements Executor<T> {

    private final Supplier<T> cachedObjectSupplier;

    private T cachedObject;

    /**
     * Create a new {@link AbstractCachingExecutor} instance.
     *
     * @param initialObject        the initial object to be cached.
     *                             May be null.
     * @param cachedObjectSupplier the {@link Supplier} of objects to
     *                             be held in the cache.
     *                             An exception will be thrown if this supplier ever
     *                             returns a null.
     *                             May not be null.
     *
     * @throws IllegalArgumentException if cachedObjectSupplier is null.
     */
    public AbstractCachingExecutor(T initialObject,
                                   Supplier<T> cachedObjectSupplier) {
        this.cachedObject = initialObject;
        this.cachedObjectSupplier = validate().withMessage("Cannot use a null cached object supplier.")
                                              .that(cachedObjectSupplier)
                                              .isNotNull();
    }

    /**
     * @return the cached object. If none is currently in the cache then one will be retrieved from
     * the given supplier and then held in the cache.
     */
    protected T getObject() {
        if (null == cachedObject) {
            cachedObject = expect().withMessage("Received a null object from the object supplier.")
                                   .that(cachedObjectSupplier.get())
                                   .isNotNull();
        }
        return cachedObject;
    }

    /**
     * @param newValue the value to set the currently cached object to.
     *                 Any previously cached object will be discarded.
     */
    protected void setCachedObject(T newValue) {
        this.cachedObject = newValue;
    }

    /**
     * @return true if the currently cached object is null.
     */
    protected boolean isCachedObjectNull() {
        return null == cachedObject;
    }

    @Override
    public <R> R apply(Function<T, R> function) {
        return validate().withMessage("Cannot execute a null function.")
                         .that(function)
                         .isNotNull()
                         .apply(getObject());
    }
}
