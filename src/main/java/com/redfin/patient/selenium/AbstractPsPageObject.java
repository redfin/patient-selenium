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

/**
 * Base class for a page object.
 *
 * A page object is a container for declaring element locators via annotations
 * to create a selenium representation of a particular web page.
 * This allows you to respond to changes in a particular web page in a centralized
 * location.
 *
 * Note that element locator fields or page objects that are to be recursively initialized
 * must be declared as instance fields (e.g. static fields are ignored).
 *
 * A subclass of this type, when initialized by a page object initializer has the search
 * context for set as the driver (e.g. even if this is a field on a widget it won't use the
 * base element of the widget as the context to search within).
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
                                           E extends AbstractPsElement<D, W, C, P, B, L, E>>
              extends AbstractPsBaseInitializedObject<D, W, C, P, B, L, E> {

    // Nothing needed here
}
