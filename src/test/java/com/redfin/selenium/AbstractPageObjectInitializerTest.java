package com.redfin.selenium;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@DisplayName("An AbstractPageObjectInitializer")
final class AbstractPageObjectInitializerTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test Cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("when being constructed")
    final class ConstructorTests {

        @Test
        @DisplayName("succeeds with a valid class object")
        void testCanInstantiate() {
            Assertions.assertNotNull(getInstance(),
                                     "Should be able to instantiate a page object initializer");
        }
    }

    @Nested
    @DisplayName("once instantiated")
    final class BehaviorTests {

        @Test
        @DisplayName("throws exception for initialize(Object) with a null object")
        void testThrowsExceptionForNullObject() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance().initialize(null),
                                    "Should throw an exception for a null object to initialize");
        }

        @Test
        @DisplayName("recursively initializes an object as expected")
        @SuppressWarnings("all")
        void testInitializesRecursivelyAsExpected() {
            PageA pageA = new PageA();
            Assumptions.assumeTrue(null == pageA.superFoo);
            Assumptions.assumeTrue(null == pageA.fooA);
            Assumptions.assumeTrue(null != pageA.notToBeInitialized);
            Assumptions.assumeTrue(null != pageA.widgetA);
            Assumptions.assumeTrue(null == pageA.nullWidget);
            Assumptions.assumeTrue(null != pageA.pageB);
            Assumptions.assumeTrue(null == pageA.pageB.fooB);
            Assumptions.assumeTrue(null != pageA.pageB.notToBeInitialized);
            Assumptions.assumeTrue(null != pageA.pageB.pageC);
            Assumptions.assumeTrue(null == pageA.pageB.pageC.fooC);
            Assumptions.assumeTrue(null != pageA.pageB.pageC.notToBeInitialized);
            Assumptions.assumeTrue(null == pageA.pageB.pageC.pageA);
            Assumptions.assumeTrue(null == pageA.pageB.pageC.nullPage);
            // Create a cycle in the object graph to make sure cycles don't cause an infinite loop
            pageA.pageB.pageC.pageA = pageA;
            // Grab references to the non-null field values
            TestElementFactory fooA = pageA.notToBeInitialized;
            TestElementFactory fooB = pageA.pageB.notToBeInitialized;
            TestElementFactory fooC = pageA.pageB.pageC.notToBeInitialized;
            PageB pageB = pageA.pageB;
            PageC pageC = pageA.pageB.pageC;
            // Initialize the page object
            getInstance().initialize(pageA);
            // Verify the results
            Assertions.assertAll(() -> Assertions.assertSame(pageB, pageA.pageB),
                                 () -> Assertions.assertSame(pageC, pageA.pageB.pageC),
                                 () -> Assertions.assertSame(fooA, pageA.notToBeInitialized),
                                 () -> Assertions.assertSame(fooB, pageA.pageB.notToBeInitialized),
                                 () -> Assertions.assertSame(fooC, pageA.pageB.pageC.notToBeInitialized),
                                 () -> Assertions.assertNull(fooA.getFieldsList()),
                                 () -> Assertions.assertNull(fooB.getFieldsList()),
                                 () -> Assertions.assertNull(fooC.getFieldsList()),
                                 () -> Assertions.assertNull(pageA.nullWidget),
                                 () -> Assertions.assertNotNull(pageA.fooA),
                                 () -> Assertions.assertNotNull(pageA.superFoo),
                                 () -> Assertions.assertNotNull(pageA.widgetA.widgetFoo),
                                 () -> Assertions.assertNotNull(pageA.widgetA.getWidgetObject()),
                                 () -> Assertions.assertNotNull(pageA.pageB.fooB),
                                 () -> Assertions.assertNotNull(pageA.pageB.pageC),
                                 () -> Assertions.assertEquals(1, pageA.fooA.getFieldsList().size()),
                                 () -> Assertions.assertEquals(1, pageA.superFoo.getFieldsList().size()),
                                 () -> Assertions.assertEquals(1, pageA.widgetA.getWidgetObject().getFieldsList().size()),
                                 () -> Assertions.assertEquals(2, pageA.widgetA.widgetFoo.getFieldsList().size()),
                                 () -> Assertions.assertEquals(2, pageA.pageB.fooB.getFieldsList().size()),
                                 () -> Assertions.assertEquals(3, pageA.pageB.pageC.fooC.getFieldsList().size()),
                                 () -> Assertions.assertNull(pageA.pageB.pageC.pageD.fooD));
        }

        @Test
        @DisplayName("propagates a thrown page object exception")
        void testPropagatesPageObjectException() {
            String message = "whoops";
            Throwable thrown = Assertions.assertThrows(PageObjectInitializationException.class,
                                                       () -> new ThrowingPageObjectInitializer(() -> new PageObjectInitializationException(message)).initialize(new PageA()),
                                                       "Should propagate a page object exception");
            Assertions.assertNull(thrown.getCause(), "The thrown exception shouldn't have a set cause");
            Assertions.assertEquals(message, thrown.getMessage(), "The thrown exception should have the given message");
        }

        @Test
        @DisplayName("wraps thrown exceptions in a PageObjectInitializationException")
        void testWrapsUnexpectedExceptions() {
            RuntimeException cause = new RuntimeException("message");
            Throwable thrown = Assertions.assertThrows(PageObjectInitializationException.class,
                                                       () -> new ThrowingPageObjectInitializer(() -> cause).initialize(new PageA()),
                                                       "Should throw a page object exception");
            Assertions.assertEquals(cause, thrown.getCause(), "The thrown exception should have the expected cause.");
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test Helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static TestPageObjectInitializer getInstance() {
        return new TestPageObjectInitializer();
    }

    private static class ThrowingPageObjectInitializer
                 extends AbstractPageObjectInitializer<TestElementFactory, TestWidget> {

        private final Supplier<RuntimeException> exceptionSupplier;

        private ThrowingPageObjectInitializer(Supplier<RuntimeException> exceptionSupplier) {
            this.exceptionSupplier = exceptionSupplier;
        }

        @Override
        protected Class<TestElementFactory> getElementFactoryClass() {
            return TestElementFactory.class;
        }

        @Override
        protected Class<TestWidget> getWidgetClass() {
            return TestWidget.class;
        }

        @Override
        protected TestElementFactory buildValue(List<Field> fields) {
            throw exceptionSupplier.get();
        }
    }

    private static class SuperPageA implements PageObject {

        final TestElementFactory superFoo = null;
    }

    private static final class PageA extends SuperPageA {

        private final TestElementFactory fooA = null;

        private final TestElementFactory notToBeInitialized = new TestElementFactory();

        private final WidgetA widgetA = new WidgetA();

        private final WidgetA nullWidget = null;

        private final PageB pageB = new PageB();
    }

    private static final class WidgetA extends TestWidget {

        private final TestElementFactory widgetFoo = null;
    }

    private static final class PageB implements PageObject {

        private final TestElementFactory fooB = null;

        private final TestElementFactory notToBeInitialized = new TestElementFactory();

        private final PageC pageC = new PageC();
    }

    private static final class PageC implements PageObject {

        private final TestElementFactory fooC = null;

        private final TestElementFactory notToBeInitialized = new TestElementFactory();

        private final PageD pageD = new PageD();

        private PageA pageA = null;

        private PageA nullPage = null;
    }

    private static final class PageD {

        // Not a PageObject interface class so this should stay null

        private final TestElementFactory fooD = null;
    }
}
