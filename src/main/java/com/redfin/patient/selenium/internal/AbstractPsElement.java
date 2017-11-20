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

import org.openqa.selenium.WebElement;

import static com.redfin.validity.Validity.validate;

public abstract class AbstractPsElement<W extends WebElement,
        C extends PsConfig<W, C, B, L, THIS>,
        B extends PsElementLocatorBuilder<W, C, B, L, THIS>,
        L extends PsElementLocator<W, C, B, L, THIS>,
        THIS extends AbstractPsElement<W, C, B, L, THIS>>
        extends AbstractPsBase<W, C, B, L, THIS>
        implements PsElement<W, C, B, L, THIS> {

    private final CachingExecutor<W> elementExecutor;

    public AbstractPsElement(String description,
                             C config,
                             CachingExecutor<W> elementExecutor) {
        super(description, config);
        this.elementExecutor = validate().withMessage("Cannot use a null element executor.")
                                         .that(elementExecutor)
                                         .isNotNull();
    }

    protected abstract B createElementLocatorBuilder(String elementLocatorBuilderDescription);

    @Override
    public final CachingExecutor<W> withWrappedElement() {
        return elementExecutor;
    }

    @Override
    public final B find() {
        return createElementLocatorBuilder(String.format("%s.find()",
                                                         getDescription()));
    }
}
