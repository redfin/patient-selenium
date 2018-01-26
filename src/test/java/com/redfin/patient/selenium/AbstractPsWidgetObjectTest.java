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

import com.redfin.patient.selenium.impl.PsElementLocatorImpl;
import com.redfin.patient.selenium.impl.PsWidgetObjectImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

@DisplayName("An AbstractPsWidgetObject")
final class AbstractPsWidgetObjectTest
 implements PageObjectContract<PsWidgetObjectImpl> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test constants, requirements, and helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public PsWidgetObjectImpl getInstance() {
        return new PsWidgetObjectImpl();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    @DisplayName("when getBaseElement() is called should throw an exception if the field hasn't been initialized")
    void testGetBaseElementThrowsForNonInitialized() {
        Assertions.assertThrows(IllegalStateException.class,
                                () -> getInstance().getBaseElement(),
                                "Should throw an exception if the field is still null.");
    }

    @Test
    @DisplayName("when getBaseElement() is called should return the set value if the field has been initialized")
    void testGetBaseElementReturnsSetValueForInitialized() {
        PsElementLocatorImpl element = mock(PsElementLocatorImpl.class);
        PsWidgetObjectImpl widget = getInstance();
        initializePageObjectField(AbstractPsWidgetObject.class, "baseElement", widget, element);
        Assertions.assertSame(element,
                              widget.getBaseElement(),
                              "An initialized page should return the given base element.");
    }
}
