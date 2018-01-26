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

import com.redfin.patient.selenium.AbstractPsElement;
import com.redfin.patient.selenium.ElementExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.mock;

public final class PsElementImpl
           extends AbstractPsElement<WebDriver,
                                     WebElement,
                                     PsConfigImpl,
                                     PsDriverImpl,
                                     PsElementLocatorBuilderImpl,
                                     PsElementLocatorImpl,
                                     PsElementImpl> {

    @SuppressWarnings("unchecked")
    public PsElementImpl(String description) {
        this(description,
             mock(PsConfigImpl.class),
             mock(PsDriverImpl.class),
             mock(ElementExecutor.class));
    }

    public PsElementImpl(String description,
                         PsConfigImpl config,
                         PsDriverImpl driver,
                         ElementExecutor<WebElement> elementExecutor) {
        super(description,
              config,
              driver,
              elementExecutor);
    }

    @Override
    public PsElementLocatorBuilderImpl find() {
        return new PsElementLocatorBuilderImpl();
    }
}
