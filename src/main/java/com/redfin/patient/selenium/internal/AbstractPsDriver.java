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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.redfin.validity.Validity.validate;

public abstract class AbstractPsDriver<D extends WebDriver,
        W extends WebElement,
        C extends PsConfig<W, C, B, L, E>,
        B extends PsElementLocatorBuilder<W, C, B, L, E>,
        L extends PsElementLocator<W, C, B, L, E>,
        E extends PsElement<W, C, B, L, E>>
        extends AbstractPsBase<W, C, B, L, E>
        implements PsDriver<D, W, C, B, L, E> {

    private final CachingExecutor<D> driverExecutor;

    public AbstractPsDriver(String description,
                            C config,
                            CachingExecutor<D> driverExecutor) {
        super(description, config);
        this.driverExecutor = validate().withMessage("Cannot use a null driver executor.")
                                        .that(driverExecutor)
                                        .isNotNull();
    }

    protected abstract B createElementLocatorBuilder(String elementLocatorBuilderDescription,
                                                     C config);

    @Override
    public CachingExecutor<D> withWrappedDriver() {
        return driverExecutor;
    }

    @Override
    public B find() {
        return createElementLocatorBuilder(String.format("%s.find()",
                                                         getDescription()),
                                           getConfig());
    }

    public void quit() {
        withWrappedDriver().accept(WebDriver::quit);
        withWrappedDriver().clearCache();
    }

    public void close() {
        int numHandles = withWrappedDriver().apply(d -> d.getWindowHandles().size());
        withWrappedDriver().accept(WebDriver::close);
        if (numHandles <= 1) {
            withWrappedDriver().clearCache();
        }
    }
}
