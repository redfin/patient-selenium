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
import com.redfin.patient.selenium.AbstractPsElementLocator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.mockito.Mockito.mock;

public final class PsElementLocatorImpl
           extends AbstractPsElementLocator<WebDriver,
                                            WebElement,
                                            PsConfigImpl,
                                            PsDriverImpl,
                                            PsElementLocatorBuilderImpl,
                                            PsElementLocatorImpl,
                                            PsElementImpl> {

    @SuppressWarnings("unchecked")
    public PsElementLocatorImpl(String description,
                                Supplier<List<WebElement>> elementSupplier) {
        this(description,
             mock(PsConfigImpl.class),
             mock(PsDriverImpl.class),
             mock(PatientWait.class),
             mock(Duration.class),
             mock(Duration.class),
             elementSupplier,
             mock(Predicate.class));
    }

    public PsElementLocatorImpl(String description,
                                PsConfigImpl config,
                                PsDriverImpl driver,
                                PatientWait wait,
                                Duration defaultTimeout,
                                Duration defaultNotPresentTimeout,
                                Supplier<List<WebElement>> elementSupplier,
                                Predicate<WebElement> elementFilter) {
        super(description,
              config,
              driver,
              wait,
              defaultTimeout,
              defaultNotPresentTimeout,
              elementSupplier,
              elementFilter);
    }

    @Override
    protected PsElementImpl buildElement(String description,
                                         WebElement initialElement,
                                         Supplier<WebElement> elementSupplier) {
        return new PsElementImpl(description);
    }
}
