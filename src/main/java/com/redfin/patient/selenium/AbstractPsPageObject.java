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

import static com.redfin.validity.Validity.expect;

/**
 * Base class for a page object.
 * A page object is a container for declaring element locators via annotations
 * to create a selenium representation of a particular web page.
 * This allows you to respond to changes in a particular web page in a centralized
 * location.
 * Note that element locator fields or page objects that are to be recursively initialized
 * must be declared as instance fields (e.g. static fields are ignored).
 *
 * @param <D> the type of wrapped WebDriver.
 * @param <W> the type of wrapped WebElement.
 * @param <C> the type of {@link AbstractPsConfig} used by the implementing type.
 * @param <P> the type of {@link AbstractPsDriver} used by the implementing type.
 * @param <B> the type of {@link AbstractPsElementLocatorBuilder} used by the implementing type.
 * @param <L> the type of {@link AbstractPsElementLocator} used by the implementing type.
 * @param <E> the type of {@link AbstractPsElement} used by the implementing type.
 */
public abstract class AbstractPsPageObject<D extends WebDriver,
                                           W extends WebElement,
                                           C extends AbstractPsConfig<D, W, C, P, B, L, E>,
                                           P extends AbstractPsDriver<D, W, C, P, B, L, E>,
                                           B extends AbstractPsElementLocatorBuilder<D, W, C, P, B, L, E>,
                                           L extends AbstractPsElementLocator<D, W, C, P, B, L, E>,
                                           E extends AbstractPsElement<D, W, C, P, B, L, E>> {

    private static final Object NULL = null;

    private final P driver = null;
    private final FindsElements<D, W, C, P, B, L, E> pageContext = null;

    /**
     * @return the driver used as the outer context to initialize this page.
     *
     * @throws IllegalStateException if this page object hasn't been initialized.
     */
    protected final P getDriver() {
        return expect().withMessage("This page object has not been initialized by a page object initializer.")
                       .that(driver)
                       .isNotNull();
    }

    /**
     * @return the config for the driver that initialized this page.
     *
     * @throws IllegalStateException if this page object hasn't been initialized.
     */
    protected final C getConfig() {
        return getDriver().getConfig();
    }

    /**
     * @return the direct search context used to initialize this page. It might
     * be the same as the driver, but might be a different page object as well.
     *
     * @throws IllegalStateException if this page object hasn't been initialized.
     */
    protected final FindsElements<D, W, C, P, B, L, E> getPageSearchContext() {
        return expect().withMessage("This page object has not been initialized by a page object initializer.")
                       .that(pageContext)
                       .isNotNull();
    }

    /**
     * @param <T> the type the return value should be cast to.
     *
     * @return a null value cast to the type T.
     */
    @SuppressWarnings("unchecked")
    protected final <T> T toBeInitialized() {
        return (T) NULL;
    }
}
