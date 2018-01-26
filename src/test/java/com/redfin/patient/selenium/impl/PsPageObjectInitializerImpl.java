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

import com.redfin.patient.selenium.AbstractPsPageObjectInitializer;
import com.redfin.patient.selenium.FindsElements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;
import java.util.List;

import static org.mockito.Mockito.mock;

public final class PsPageObjectInitializerImpl
           extends AbstractPsPageObjectInitializer<WebDriver,
                                                   WebElement,
                                                   PsConfigImpl,
                                                   PsDriverImpl,
                                                   PsElementLocatorBuilderImpl,
                                                   PsElementLocatorImpl,
                                                   PsElementImpl> {

    public PsPageObjectInitializerImpl(PsDriverImpl driver,
                                       Class<PsElementLocatorImpl> elementLocatorClass) {
        super(driver, elementLocatorClass);
    }

    @Override
    protected PsElementLocatorImpl buildElementLocator(FindsElements<WebDriver,
                                                                     WebElement,
                                                                     PsConfigImpl,
                                                                     PsDriverImpl,
                                                                     PsElementLocatorBuilderImpl,
                                                                     PsElementLocatorImpl,
                                                                     PsElementImpl> searchContext,
                                                       List<Field> fields) {
        return mock(PsElementLocatorImpl.class);
    }
}
