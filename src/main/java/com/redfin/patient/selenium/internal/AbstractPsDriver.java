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

/**
 * Base type for a driver.
 * This is an immutable type that allows you to interact with the wrapped
 * selenium driver.
 *
 * @param <D>    the type of wrapped WebDriver.
 * @param <W>    the type of wrapped WebElement.
 * @param <C>    the type of {@link AbstractPsConfig} used by the implementing type.
 * @param <THIS> the type of {@link AbstractPsDriver} used by the implementing type.
 * @param <B>    the type of {@link AbstractPsElementLocatorBuilder} used by the implementing type.
 * @param <L>    the type of {@link AbstractPsElementLocator} used by the implementing type.
 * @param <E>    the type of {@link AbstractPsElement} used by the implementing type.
 */
public abstract class AbstractPsDriver<D extends WebDriver,
                                       W extends WebElement,
                                       C extends AbstractPsConfig<D, W, C, THIS, B, L, E>,
                                    THIS extends AbstractPsDriver<D, W, C, THIS, B, L, E>,
                                       B extends AbstractPsElementLocatorBuilder<D, W, C, THIS, B, L, E>,
                                       L extends AbstractPsElementLocator<D, W, C, THIS, B, L, E>,
                                       E extends AbstractPsElement<D, W, C, THIS, B, L, E>>
              extends AbstractPsBase<D, W, C, THIS, B, L, E>
           implements FindsElements<D, W, C, THIS, B, L, E> {

    private final DriverExecutor<D> driverExecutor;
    private final JavaScriptExecutor executor;

    /**
     * Create a new {@link AbstractPsDriver} instance.
     *
     * @param description    the String description for this driver.
     *                       May not be null or empty.
     * @param config         the {@link AbstractPsConfig} for this driver.
     *                       May not be null.
     * @param driverExecutor the {@link DriverExecutor} for this driver.
     *                       May not be null.
     *
     * @throws IllegalArgumentException if any argument is null or if description is empty.
     */
    protected AbstractPsDriver(String description,
                               C config,
                               DriverExecutor<D> driverExecutor) {
        super(description, config);
        this.driverExecutor = validate().withMessage("Cannot use a null driver executor.")
                                        .that(driverExecutor)
                                        .isNotNull();
        this.executor = new JavaScriptExecutorImpl<>(driverExecutor);
    }

    /**
     * @return the {@link Executor} for this driver.
     */
    public final Executor<D> withWrappedDriver() {
        return driverExecutor;
    }

    /**
     * @return the {@link JavaScriptExecutor} for this driver.
     */
    public JavaScriptExecutor execute() {
        return executor;
    }

    /**
     * Call quit on the currently cached driver and clear the cache.
     * Any future commands to the driver will get a new one from the driver supplier.
     * If the driver is not yet initialized or was previously quit and
     * not restarted then this will do nothing.
     *
     * @see WebDriver#quit()
     */
    public void quit() {
        driverExecutor.quit();
    }

    /**
     * Call close on the currently cached driver. If this causes the
     * driver to quit (there is only 1 window left) then this will clear
     * the cache. Any future commands to the driver will retrieve a new one
     * from the driver supplier.
     * If the driver is not yet initialized or was previously quit and not
     * restarted then this will do nothing.
     *
     * @see WebDriver#close()
     */
    public void close() {
        driverExecutor.close();
    }
}
