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

import com.redfin.patient.selenium.internal.AbstractPsElement;
import com.redfin.patient.selenium.internal.CachingExecutor;
import org.openqa.selenium.WebElement;

import java.util.Arrays;

public final class ExamplePsElement
        extends AbstractPsElement<WebElement,
        ExamplePsConfig,
        ExamplePsElementLocatorBuilder,
        ExamplePsElementLocator,
        ExamplePsElement> {

    public ExamplePsElement(String description,
                            ExamplePsConfig config,
                            CachingExecutor<WebElement> elementExecutor) {
        super(description, config, elementExecutor);
    }

    @Override
    protected ExamplePsElementLocatorBuilder createElementLocatorBuilder(String elementLocatorBuilderDescription,
                                                                         ExamplePsConfig config) {
        return new ExamplePsElementLocatorBuilder(elementLocatorBuilderDescription,
                                                  config,
                                                  by -> withWrappedElement().apply(e -> e.findElements(by)));
    }

    // implement any other element methods you want to expose as the API for your element

    public ExamplePsElement clear() {
        System.out.println(String.format("%s.clear()",
                                         this));
        withWrappedElement().accept(WebElement::clear);
        return this;
    }

    public ExamplePsElement sendKeys(CharSequence... keysToSend) {
        System.out.println(String.format("%s.sendKeys(%s)",
                                         this,
                                         Arrays.toString(keysToSend)));
        withWrappedElement().accept(e -> e.sendKeys(keysToSend));
        return this;
    }

    public ExamplePsElement click() {
        System.out.println(String.format("%s.click()",
                                         this));
        withWrappedElement().accept(WebElement::click);
        return this;
    }
}
