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

package com.redfin.patient.selenium.impl;

import com.redfin.patience.PatientWait;
import com.redfin.patient.selenium.AbstractPsConfig;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.mockito.Mockito.mock;

public final class PsConfigImpl
           extends AbstractPsConfig<WebDriver,
                                    WebElement,
                                    PsConfigImpl,
                                    PsDriverImpl,
                                    PsElementLocatorBuilderImpl,
                                    PsElementLocatorImpl,
                                    PsElementImpl> {

    @SuppressWarnings("unchecked")
    public PsConfigImpl() {
        this(mock(PatientWait.class),
             mock(Duration.class),
             mock(Duration.class),
             mock(Predicate.class),
             mock(Function.class));
    }

    public PsConfigImpl(PatientWait defaultWait,
                        Duration defaultTimeout,
                        Duration defaultNotPresentTimeout,
                        Predicate<WebElement> defaultElementFilter,
                        Function<String, NoSuchElementException> elementNotFoundExceptionBuilderFunction) {
        super(defaultWait,
              defaultTimeout,
              defaultNotPresentTimeout,
              defaultElementFilter,
              elementNotFoundExceptionBuilderFunction);
    }
}
