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

import com.redfin.patient.selenium.internal.AbstractPsElementLocatorBuilder;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.validate;

public class NativeMobileElementLocatorBuilder
        extends AbstractPsElementLocatorBuilder<MobileElement,
        NativeMobileConfig,
        NativeMobileElementLocatorBuilder,
        NativeMobileElementLocator,
        NativeMobileElement> {

    public NativeMobileElementLocatorBuilder(String description,
                                             NativeMobileConfig config,
                                             Function<By, List<MobileElement>> baseSeleniumLocatorFunction) {
        super(description, config, baseSeleniumLocatorFunction);
    }

    @Override
    protected NativeMobileElementLocatorBuilder getThis() {
        return this;
    }

    @Override
    protected NativeMobileElementLocator build(String description,
                                               Supplier<List<MobileElement>> elementSupplier) {
        return new NativeMobileElementLocator(description,
                                              getConfig(),
                                              getDefaultWait(),
                                              getDefaultTimeout(),
                                              getDefaultAssertNotPresentTimeout(),
                                              elementSupplier,
                                              getElementFilter());
    }

    public NativeMobileElementLocator id(String id) {
        validate().withMessage("Cannot locate an element with a null or empty locator string.")
                  .that(id).isNotNull();
        return by(MobileBy.id(id));
    }

    public NativeMobileElementLocator accessibility(String accessibility) {
        validate().withMessage("Cannot locate an element with a null or empty locator string.")
                  .that(accessibility).isNotNull();
        return by(MobileBy.AccessibilityId(accessibility));
    }

    public NativeMobileElementLocator xpath(String accessibility) {
        validate().withMessage("Cannot locate an element with a null or empty locator string.")
                  .that(accessibility).isNotNull();
        return by(MobileBy.AccessibilityId(accessibility));
    }
}
