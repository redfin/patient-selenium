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

package com.redfin.patient.selenium.apps;

import com.redfin.patient.selenium.internal.AbstractPsElement;
import com.redfin.patient.selenium.CachingExecutor;
import io.appium.java_client.MobileElement;

public class NativeMobileElement
        extends AbstractPsElement<MobileElement,
        NativeMobileConfig,
        NativeMobileElementLocatorBuilder,
        NativeMobileElementLocator,
        NativeMobileElement> {

    public NativeMobileElement(String description,
                               NativeMobileConfig config,
                               CachingExecutor<MobileElement> elementExecutor) {
        super(description, config, elementExecutor);
    }

    @Override
    protected final NativeMobileElementLocatorBuilder createElementLocatorBuilder(String elementLocatorBuilderDescription,
                                                                                  NativeMobileConfig config) {
        return new NativeMobileElementLocatorBuilder(elementLocatorBuilderDescription,
                                                     config,
                                                     by -> withWrappedElement().apply(e -> e.findElements(by)));
    }

    public boolean isDisplayed() {
        return withWrappedElement().apply(MobileElement::isDisplayed);
    }

    public NativeMobileElement tap() {
        withWrappedElement().accept(MobileElement::click);
        return this;
    }

    public NativeMobileElement sendKeys(CharSequence... keysToSend) {
        withWrappedElement().accept(e -> e.sendKeys(keysToSend));
        return this;
    }
}
