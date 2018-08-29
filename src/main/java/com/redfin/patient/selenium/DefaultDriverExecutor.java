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

import org.openqa.selenium.WebDriver;

import java.util.function.Supplier;

/**
 * The default implementation of the {@link DriverExecutor} interface for
 * the Patient-Selenium library. It can take in a supplier of web drivers to
 * allow for lazy initialization.
 *
 * @param <D> the type of wrapped {@link WebDriver}.
 */
public final class DefaultDriverExecutor<D extends WebDriver>
           extends AbstractCachingExecutor<D>
        implements DriverExecutor<D> {

    /**
     * Create a new instance of a {@link DefaultDriverExecutor} with the
     * given supplier of web drivers.
     *
     * @param driverSupplier the supplier to retrieve {@link WebDriver} instances
     *                       from for use. The supplier must never return a null value
     *                       or an exception will be thrown at that time.
     *                       May not be null.
     *
     * @throws IllegalArgumentException if driverSupplier is null.
     */
    public DefaultDriverExecutor(Supplier<D> driverSupplier) {
        this(null, driverSupplier);
    }

    /**
     * Create a new instance of a {@link DefaultDriverExecutor} with the
     * given supplier of web drivers.
     *
     * @param initialDriver  the {@link WebDriver} to set in the cache.
     *                       May be null.
     * @param driverSupplier the supplier to retrieve {@link WebDriver} instances
     *                       from for use. The supplier must never return a null value
     *                       or an exception will be thrown at that time.
     *                       May not be null.
     *
     * @throws IllegalArgumentException if driverSupplier is null.
     */
    public DefaultDriverExecutor(D initialDriver,
                                 Supplier<D> driverSupplier) {
        super(initialDriver, driverSupplier);
    }

    @Override
    public void quit() {
        if (!isCachedObjectNull()) {
            try {
                accept(WebDriver::quit);
            } finally {
                setCachedObject(null);
            }
        }
    }

    @Override
    public void close() {
        if (!isCachedObjectNull()) {
            int numHandles = 0;
            try {
                numHandles = apply(d -> {
                    int count = d.getWindowHandles().size();
                    d.close();
                    return count;
                });
            } finally {
                if (numHandles <= 1) {
                    setCachedObject(null);
                }
            }
        }
    }
}