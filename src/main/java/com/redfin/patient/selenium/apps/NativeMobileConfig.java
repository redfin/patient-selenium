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
import com.redfin.patient.selenium.internal.AbstractPsConfig;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.NoSuchElementException;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Predicate;

public class NativeMobileConfig
        extends AbstractPsConfig<MobileElement,
        NativeMobileConfig,
        NativeMobileElementLocatorBuilder,
        NativeMobileElementLocator,
        NativeMobileElement> {

    public NativeMobileConfig(PatientWait defaultWait,
                              Duration defaultTimeout,
                              Predicate<MobileElement> defaultElementFilter,
                              Function<String, NoSuchElementException> noSuchElementExceptionFunction) {
        super(defaultWait,
              defaultTimeout,
              defaultElementFilter,
              noSuchElementExceptionFunction);
    }

    public NativeMobileConfig(PatientWait defaultWait,
                              Duration defaultTimeout,
                              Duration defaultAssertNotPresentTimeout,
                              Predicate<MobileElement> defaultElementFilter,
                              Function<String, NoSuchElementException> noSuchElementExceptionFunction) {
        super(defaultWait,
              defaultTimeout,
              defaultAssertNotPresentTimeout,
              defaultElementFilter,
              noSuchElementExceptionFunction);
    }
}
