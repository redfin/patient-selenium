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

import static com.redfin.validity.Validity.expect;

public abstract class AbstractPsPageObject<D extends WebDriver,
        W extends WebElement,
        C extends AbstractPsConfig<D, W, C, P, B, L, E>,
        P extends AbstractPsDriver<D, W, C, P, B, L, E>,
        B extends AbstractPsElementLocatorBuilder<D, W, C, P, B, L, E>,
        L extends AbstractPsElementLocator<D, W, C, P, B, L, E>,
        E extends AbstractPsElement<D, W, C, P, B, L, E>> {

    private static final Object[] NULL = {null};

    private final P driver = null;
    private final FindsElements<D, W, C, P, B, L, E> pageContext = null;

    protected final P getDriver() {
        return expect().withMessage("This page object has not been initialized by a page object initializer.")
                       .that(driver)
                       .isNotNull();
    }

    protected final FindsElements<D, W, C, P, B, L, E> getPageSearchContext() {
        return expect().withMessage("This page object has not been initialized by a page object initializer.")
                       .that(pageContext)
                       .isNotNull();
    }

    @SuppressWarnings("unchecked")
    protected final L toBeInitialized() {
        return (L) NULL[0];
    }
}
