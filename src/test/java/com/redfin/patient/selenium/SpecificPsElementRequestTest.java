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

import com.redfin.patient.selenium.impl.PsConfigImpl;
import com.redfin.patient.selenium.impl.PsDriverImpl;
import com.redfin.patient.selenium.impl.PsElementImpl;
import com.redfin.patient.selenium.impl.PsElementLocatorBuilderImpl;
import com.redfin.patient.selenium.impl.PsElementLocatorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;

import static com.redfin.patient.selenium.ElementLocatorTestHelper.getSuccessfulInstance;
import static com.redfin.patient.selenium.ElementLocatorTestHelper.getUnsuccessfulInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@DisplayName("A SpecificPsElementRequestTest")
final class SpecificPsElementRequestTest {

    @SuppressWarnings("unchecked")
    private SpecificPsElementRequest<WebDriver,
                                     WebElement,
                                     PsConfigImpl,
                                     PsDriverImpl,
                                     PsElementLocatorBuilderImpl,
                                     PsElementLocatorImpl,
                                     PsElementImpl> getRequest(int index,
                                                               Duration timeout) {
        return new SpecificPsElementRequest<>((i, t) -> mock(PsElementImpl.class), index, timeout);
    }

    @Test
    @DisplayName("returns it's given index and default timeout values")
    void testRequestReturnsGivenValues() {
        int index = 10;
        Duration timeout = Duration.ofMinutes(5);
        SpecificPsElementRequest request = getRequest(index, timeout);
        Assertions.assertAll(() -> Assertions.assertEquals(index, request.getIndex(), "A specific element request should return the given index."),
                             () -> Assertions.assertEquals(timeout, request.getDefaultTimeout(), "A specific element request should return the given default timeout."));
    }

    @Test
    @DisplayName("returns it's given index and default timeout values")
    void testGetCallsGetWithDefaultTimeout() {
        Duration timeout = Duration.ofMinutes(2);
        SpecificPsElementRequest request = spy(getRequest(0, timeout));
        Assertions.assertNotNull(request.get(),
                                 "Expected a non-null result.");
        verify(request).get(timeout);
    }

    @Test
    @DisplayName("when get is called returns the nth matching element")
    void testGetReturnsNthMatchingElement() {
        PsElementLocatorImpl el = getSuccessfulInstance(Duration.ZERO);
        el.atIndex(1)
          .get(Duration.ZERO)
          .withWrappedElement()
          .accept(e -> Assertions.assertEquals("element3",
                                               e.getText(),
                                               "Expected to get the 2nd matching element."));
    }

    @Test
    @DisplayName("when get is called and no matching elements are found throws an exception")
    void testGetUnsuccessfullyThrowsException() {
        Assertions.assertThrows(NoSuchElementException.class,
                                () -> getUnsuccessfulInstance(Duration.ZERO).get(Duration.ZERO),
                                "An unsuccessful get should throw an exception.");
    }
}
