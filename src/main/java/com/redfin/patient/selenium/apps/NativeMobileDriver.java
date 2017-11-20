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

import com.redfin.patient.selenium.internal.AbstractPsDriver;
import com.redfin.patient.selenium.CachingExecutor;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

public class NativeMobileDriver
        extends AbstractPsDriver<AppiumDriver<MobileElement>,
        MobileElement,
        NativeMobileConfig,
        NativeMobileElementLocatorBuilder,
        NativeMobileElementLocator,
        NativeMobileElement> {

    public NativeMobileDriver(String description,
                              NativeMobileConfig config,
                              CachingExecutor<AppiumDriver<MobileElement>> driverExecutor) {
        super(description, config, driverExecutor);
    }

    @Override
    protected final NativeMobileElementLocatorBuilder createElementLocatorBuilder(String elementLocatorBuilderDescription,
                                                                                  NativeMobileConfig config) {
        return new NativeMobileElementLocatorBuilder(elementLocatorBuilderDescription,
                                                     config,
                                                     by -> withWrappedDriver().apply(d -> d.findElements(by)));
    }
}
