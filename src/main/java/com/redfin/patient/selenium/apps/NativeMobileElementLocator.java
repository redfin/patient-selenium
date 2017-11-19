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

import com.redfin.patience.PatientWait;
import com.redfin.patient.selenium.internal.AbstractPsElementLocator;
import com.redfin.patient.selenium.internal.DefaultElementCachingExecutor;
import io.appium.java_client.MobileElement;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class NativeMobileElementLocator
        extends AbstractPsElementLocator<MobileElement,
        NativeMobileConfig,
        NativeMobileElementLocatorBuilder,
        NativeMobileElementLocator,
        NativeMobileElement> {

    public NativeMobileElementLocator(String description,
                                      NativeMobileConfig config,
                                      PatientWait wait,
                                      Duration defaultTimeout,
                                      Duration defaultAssertNotPresentTimeout,
                                      Supplier<List<MobileElement>> elementSupplier,
                                      Predicate<MobileElement> elementFilter) {
        super(description,
              config,
              wait,
              defaultTimeout,
              defaultAssertNotPresentTimeout,
              elementSupplier,
              elementFilter);
    }

    @Override
    protected NativeMobileElement buildElement(String elementDescription,
                                               MobileElement initialElement,
                                               Supplier<MobileElement> elementSupplier) {
        return new NativeMobileElement(elementDescription,
                                       getConfig(),
                                       new DefaultElementCachingExecutor<>(initialElement,
                                                                           elementSupplier));
    }
}
