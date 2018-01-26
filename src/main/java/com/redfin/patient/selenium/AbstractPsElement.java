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
import org.openqa.selenium.WebElement;

import static com.redfin.validity.Validity.validate;

/**
 * Base type for an element.
 * This is an immutable type that allows you to interact with the wrapped
 * selenium element.
 *
 * @param <D>    the type of wrapped WebDriver.
 * @param <W>    the type of wrapped WebElement.
 * @param <C>    the type of {@link AbstractPsConfig} used by the implementing type.
 * @param <P>    the type of {@link AbstractPsDriver} used by the implementing type.
 * @param <B>    the type of {@link AbstractPsElementLocatorBuilder} used by the implementing type.
 * @param <L>    the type of {@link AbstractPsElementLocator} used by the implementing type.
 * @param <THIS> the type of {@link AbstractPsElement} used by the implementing type.
 */
public abstract class AbstractPsElement<D extends WebDriver,
                                        W extends WebElement,
                                        C extends AbstractPsConfig<D, W, C, P, B, L, THIS>,
                                        P extends AbstractPsDriver<D, W, C, P, B, L, THIS>,
                                        B extends AbstractPsElementLocatorBuilder<D, W, C, P, B, L, THIS>,
                                        L extends AbstractPsElementLocator<D, W, C, P, B, L, THIS>,
                                     THIS extends AbstractPsElement<D, W, C, P, B, L, THIS>>
              extends AbstractPsBase<D, W, C, P, B, L, THIS>
           implements FindsElements<D, W, C, P, B, L, THIS> {

    private final P driver;
    private final ElementExecutor<W> elementExecutor;

    /**
     * Create a new {@link AbstractPsElement} instance.
     *
     * @param description     the String description of the wrapped element.
     *                        May not be null or empty.
     * @param config          the {@link AbstractPsConfig} for this instance.
     *                        May not be null.
     * @param driver          the {@link AbstractPsDriver} that was used to locate this element.
     *                        May not be null.
     * @param elementExecutor the {@link ElementExecutor} that actually wraps the
     *                        desired element.
     *                        May not be null.
     *
     * @throws IllegalArgumentException if any argument is null or if the description is empty.
     */
    public AbstractPsElement(String description,
                             C config,
                             P driver,
                             ElementExecutor<W> elementExecutor) {
        super(description, config);
        this.driver = validate().withMessage("Cannot use a null driver.")
                                .that(driver)
                                .isNotNull();
        this.elementExecutor = validate().withMessage("Cannot use a null element executor.")
                                         .that(elementExecutor)
                                         .isNotNull();
    }

    /**
     * @return the driver used to locate this element.
     */
    protected final P getDriver() {
        return driver;
    }

    /**
     * @return the given {@link ElementExecutor}.
     */
    public final ElementExecutor<W> withWrappedElement() {
        return elementExecutor;
    }
}
