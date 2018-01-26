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
 * Base class for most of the Patient-Selenium types.
 * It contains a reference to the description of the concrete subclass and
 * to the config object to be used.
 *
 * @param <D> the type of wrapped WebDriver.
 * @param <W> the type of wrapped WebElement.
 * @param <C> the type of {@link AbstractPsConfig} used by the implementing type.
 * @param <P> the type of {@link AbstractPsDriver} used by the implementing type.
 * @param <B> the type of {@link AbstractPsElementLocatorBuilder} used by the implementing type.
 * @param <L> the type of {@link AbstractPsElementLocator} used by the implementing type.
 * @param <E> the type of {@link AbstractPsElement} used by the implementing type.
 */
public abstract class AbstractPsBase<D extends WebDriver,
                                     W extends WebElement,
                                     C extends AbstractPsConfig<D, W, C, P, B, L, E>,
                                     P extends AbstractPsDriver<D, W, C, P, B, L, E>,
                                     B extends AbstractPsElementLocatorBuilder<D, W, C, P, B, L, E>,
                                     L extends AbstractPsElementLocator<D, W, C, P, B, L, E>,
                                     E extends AbstractPsElement<D, W, C, P, B, L, E>> {

    private final String description;
    private final C config;

    /**
     * Create a new {@link AbstractPsBase} instance.
     *
     * @param description the String description for this object.
     *                    May not be null or empty.
     * @param config      the config to be used for this object.
     *                    May not be null.
     *
     * @throws IllegalArgumentException if config is null or if description is null or empty.
     */
    public AbstractPsBase(String description,
                          C config) {
        this.description = validate().withMessage("Cannot use a null or empty description.")
                                     .that(description)
                                     .isNotEmpty();
        this.config = validate().withMessage("Cannot use a null config.")
                                .that(config)
                                .isNotNull();
    }

    /**
     * @return the description for this object.
     */
    protected final String getDescription() {
        return description;
    }

    /**
     * @return the config instance to be used for this object.
     */
    protected final C getConfig() {
        return config;
    }

    @Override
    public String toString() {
        return description;
    }
}
