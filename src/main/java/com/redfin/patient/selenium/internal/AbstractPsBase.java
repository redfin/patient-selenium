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

public abstract class AbstractPsBase<D extends WebDriver,
        W extends WebElement,
        C extends AbstractPsConfig<D, W, C, P, B, L, E>,
        P extends AbstractPsDriver<D, W, C, P, B, L, E>,
        B extends AbstractPsElementLocatorBuilder<D, W, C, P, B, L, E>,
        L extends AbstractPsElementLocator<D, W, C, P, B, L, E>,
        E extends AbstractPsElement<D, W, C, P, B, L, E>> {

    private final String description;
    private final C config;

    public AbstractPsBase(String description,
                          C config) {
        this.description = validate().withMessage("Cannot use a null or empty description.")
                                     .that(description)
                                     .isNotEmpty();
        this.config = validate().withMessage("Cannot use a null config.")
                                .that(config)
                                .isNotNull();
    }

    protected final String getDescription() {
        return description;
    }

    protected final C getConfig() {
        return config;
    }

    @Override
    public String toString() {
        return description;
    }
}
