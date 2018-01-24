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

/**
 * Base class for a page widget.
 * A widget is a form of page object that contains a base element locator.
 * This is to represent the outer container for a widget that might live
 * on multiple pages on a web site.
 * When included in a page object you typically need to annotate the widget
 * in such a way it knows how to build the base element locator but the
 * details are up to the page object initializer implementation.
 *
 * @param <D> the type of wrapped WebDriver.
 * @param <W> the type of wrapped WebElement.
 * @param <C> the type of {@link AbstractPsConfig} used by the implementing type.
 * @param <P> the type of {@link AbstractPsDriver} used by the implementing type.
 * @param <B> the type of {@link AbstractPsElementLocatorBuilder} used by the implementing type.
 * @param <L> the type of {@link AbstractPsElementLocator} used by the implementing type.
 * @param <E> the type of {@link AbstractPsElement} used by the implementing type.
 */
public abstract class AbstractPsWidgetObject<D extends WebDriver,
                                             W extends WebElement,
                                             C extends AbstractPsConfig<D, W, C, P, B, L, E>,
                                             P extends AbstractPsDriver<D, W, C, P, B, L, E>,
                                             B extends AbstractPsElementLocatorBuilder<D, W, C, P, B, L, E>,
                                             L extends AbstractPsElementLocator<D, W, C, P, B, L, E>,
                                             E extends AbstractPsElement<D, W, C, P, B, L, E>>
              extends AbstractPsPageObject<D, W, C, P, B, L, E> {

    private final L baseElement = null;

    /**
     * @return the base element for this widget.
     *
     * @throws IllegalStateException if this widget wasn't initialized by a page object initializer.
     */
    protected final L getBaseElement() {
        expect().withMessage("This widget object has not been initialized by a page object initializer.")
                .that(baseElement)
                .isNotNull();
        return baseElement;
    }
}
