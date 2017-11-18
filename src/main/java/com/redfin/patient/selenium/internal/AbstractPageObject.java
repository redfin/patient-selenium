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

public abstract class AbstractPageObject<D extends WebDriver,
        W extends WebElement,
        P extends PsDriver<D, W, C, B, L, E>,
        C extends PsConfig<W, C, B, L, E>,
        B extends PsElementLocatorBuilder<W, C, B, L, E>,
        L extends PsElementLocator<W, C, B, L, E>,
        E extends PsElement<W, C, B, L, E>> {

    private final P driver = null;

    protected final P getPatientDriver() {
        return expect().withMessage("This page object has not been initialized by a page object initializer.")
                       .that(driver)
                       .isNotNull();
    }
}
