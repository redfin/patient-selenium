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

    protected final P getDriver() {
        return driver;
    }

    public final ElementExecutor<W> withWrappedElement() {
        return elementExecutor;
    }
}
