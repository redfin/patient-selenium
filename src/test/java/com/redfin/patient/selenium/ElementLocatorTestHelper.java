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

import com.redfin.patience.PatientWait;
import com.redfin.patient.selenium.impl.PsConfigImpl;
import com.redfin.patient.selenium.impl.PsDriverImpl;
import com.redfin.patient.selenium.impl.PsElementLocatorImpl;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ElementLocatorTestHelper {

    static PsElementLocatorImpl getSuccessfulInstance(Duration timeout) {
        return getSuccessfulInstance(timeout, timeout);
    }

    @SuppressWarnings("unchecked")
    static PsElementLocatorImpl getSuccessfulInstance(Duration timeout,
                                                      Duration notPresentTimeout) {
        WebElement element1 = mock(WebElement.class);
        WebElement element2 = mock(WebElement.class);
        WebElement element3 = mock(WebElement.class);
        when(element1.isDisplayed()).thenReturn(false);
        when(element2.isDisplayed()).thenReturn(true);
        when(element3.isDisplayed()).thenReturn(true);
        when(element1.getText()).thenReturn("element1");
        when(element2.getText()).thenReturn("element2");
        when(element3.getText()).thenReturn("element3");
        PsConfigImpl config = new PsConfigImpl();
        return new PsElementLocatorImpl("description",
                                        config,
                                        new PsDriverImpl("driver", config, mock(DriverExecutor.class)),
                                        PatientWait.builder().build(),
                                        timeout,
                                        notPresentTimeout,
                                        () -> Arrays.asList(element1, element2, element3),
                                        WebElement::isDisplayed);
    }

    @SuppressWarnings("unchecked")
    static PsElementLocatorImpl getUnsuccessfulInstance(Duration timeout) {
        Function<String, NoSuchElementException> exceptionFunction = NoSuchElementException::new;
        PsConfigImpl config = mock(PsConfigImpl.class);
        when(config.getElementNotFoundExceptionBuilderFunction()).thenReturn(exceptionFunction);
        return new PsElementLocatorImpl("description",
                                        config,
                                        new PsDriverImpl("driver", config, mock(DriverExecutor.class)),
                                        PatientWait.builder().build(),
                                        timeout,
                                        Duration.ZERO,
                                        Collections::emptyList,
                                        WebElement::isDisplayed);
    }

    private ElementLocatorTestHelper() {
        throw new AssertionError("No instances for you!");
    }
}
