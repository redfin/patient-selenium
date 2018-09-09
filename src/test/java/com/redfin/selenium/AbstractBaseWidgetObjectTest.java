package com.redfin.selenium;

import com.redfin.selenium.implementation.TestBaseWidgetObject;
import com.redfin.selenium.implementation.TestPatientElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

@DisplayName("An AbstractBaseWidgetObject")
final class AbstractBaseWidgetObjectTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    @DisplayName("can be instantiated")
    void testCanInstantiate() {
        Assertions.assertNotNull(getInstance(),
                                 "Should be able to instantiate");
    }

    @Test
    @DisplayName("returns the set base element")
    void testReturnsGivenElement() {
        TestPatientElement element = mock(TestPatientElement.class);
        TestWidgetObject widget = getInstance();
        setWidgetElement(widget, element);
        Assertions.assertSame(element,
                              widget.getWidgetElement(),
                              "Should return the given base element");
    }

    @Test
    @DisplayName("throws exception if initialized twice")
    void testThrowsForRepeatedInitialization() {
        TestPatientElement element = mock(TestPatientElement.class);
        TestWidgetObject widget = getInstance();
        setWidgetElement(widget, element);
        Assertions.assertThrows(PageObjectInitializationException.class,
                                () -> setWidgetElement(widget, element),
                              "Should throw an exception if initialized twice");
    }

    @Test
    @DisplayName("throws exception if not initialized")
    void testThrowsForNotInitialized() {
        Assertions.assertThrows(PageObjectInitializationException.class,
                                () -> getInstance().getWidgetElement(),
                                "Should throw an exception for getWidgetElement() if not set");
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @SuppressWarnings("unchecked")
    private static void setWidgetElement(TestWidgetObject widget,
                                         TestPatientElement element) {
        ((AbstractBaseWidgetObject<?, ?, ?, TestPatientElement>) widget).setWidgetElement(element);
    }

    private static TestWidgetObject getInstance() {
        return new TestWidgetObject();
    }

    private static final class TestWidgetObject
                       extends TestBaseWidgetObject {}
}
