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

import com.redfin.patient.selenium.CachingExecutor;
import org.openqa.selenium.WebDriver;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.expect;
import static com.redfin.validity.Validity.validate;

public final class DefaultDriverCachingExecutor<D extends WebDriver>
        implements CachingExecutor<D> {

    private final Supplier<D> driverSupplier;

    private D driver = null;

    public DefaultDriverCachingExecutor(Supplier<D> driverSupplier) {
        this.driverSupplier = validate().withMessage("Cannot create a driver executor with a null driver supplier.")
                                        .that(driverSupplier)
                                        .isNotNull();
    }

    private D getDriver() {
        if (null == driver) {
            driver = expect().withMessage("Received a null driver from the driver supplier.")
                             .that(driverSupplier.get())
                             .isNotNull();
        }
        return driver;
    }

    @Override
    public <R> R apply(Function<D, R> function) {
        validate().withMessage("Cannot execute with a null function.")
                  .that(function)
                  .isNotNull();
        return function.apply(getDriver());
    }

    @Override
    public void clearCache() {
        driver = null;
    }
}
