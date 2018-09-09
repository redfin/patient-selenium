package com.redfin.selenium;

import com.redfin.selenium.implementation.FindByCss;
import com.redfin.selenium.implementation.TestPageObjectInitializer;
import com.redfin.selenium.implementation.TestPatientDriver;
import com.redfin.selenium.implementation.TestPatientElement;
import com.redfin.selenium.implementation.TestPatientElementLocator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Supplier;

import static org.mockito.Mockito.mock;

@DisplayName("An AbstractPageObjectInitializer")
final class AbstractPageObjectInitializerTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test Cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    @DisplayName("can be instantiated")
    void testCanInstantiate() {
        Assertions.assertNotNull(getInstance(),
                                 "Should be able to instantiate a page object initializer");
    }

    @Nested
    @DisplayName("once instantiated")
    final class BehaviorTests {

        @Test
        @DisplayName("throws exception for initialize(Object) with a null object")
        void testThrowsExceptionForNullObject() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance().initializePage(null, mock(TestPatientDriver.class)),
                                    "Should throw an exception for a null object to initialize");
        }

        @Test
        @DisplayName("throws exception for initialize(Object) with a null object")
        void testThrowsExceptionForNullFindsElements() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance().initializePage(mock(PageObject.class), null),
                                    "Should throw an exception for a null finds elements to be the root of element locator");
        }

        @Test
        @DisplayName("recursively initializes an object as expected")
        @SuppressWarnings("all")
        void testInitializesRecursivelyAsExpected() {
            PageA pageA = new PageA();
            Assumptions.assumeTrue(null == pageA.superFoo);
            Assumptions.assumeTrue(null == pageA.fooA);
            Assumptions.assumeTrue(null != pageA.notToBeInitialized);
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
            TestPatientElementLocator pageANoInit = pageA.notToBeInitialized;
            TestPatientElementLocator pageBNoInit = pageA.pageB.notToBeInitialized;
            TestPatientElementLocator pageCNoInit = pageA.pageB.pageC.notToBeInitialized;
            PageB pageB = pageA.pageB;
            PageC pageC = pageA.pageB.pageC;
            // Initialize the page object
            TestPatientDriver driver = new TestPatientDriver(PatientSeleniumConfig.builder().build(),
                                                             "chrome",
                                                             () -> mock(WebDriver.class));
            new TestPageObjectInitializer().initializePage(pageA, driver);
            // Verify the results
            Assertions.assertAll(() -> Assertions.assertSame(pageB, pageA.pageB),
                                 () -> Assertions.assertSame(pageC, pageA.pageB.pageC),
                                 () -> Assertions.assertSame(pageANoInit, pageA.notToBeInitialized),
                                 () -> Assertions.assertSame(pageBNoInit, pageA.pageB.notToBeInitialized),
                                 () -> Assertions.assertSame(pageCNoInit, pageA.pageB.pageC.notToBeInitialized),
                                 () -> Assertions.assertNotNull(pageA.fooA),
                                 () -> Assertions.assertNotNull(pageA.superFoo),
                                 () -> Assertions.assertNotNull(pageA.pageB.fooB),
                                 () -> Assertions.assertNotNull(pageA.pageB.pageC),
                                 () -> Assertions.assertEquals("chrome.find(By.cssSelector: fooA)", pageA.fooA.toString()),
                                 () -> Assertions.assertEquals("chrome.find(By.cssSelector: superFoo)", pageA.superFoo.toString()),
                                 () -> Assertions.assertEquals("chrome.find(By.cssSelector: fooB)", pageA.pageB.fooB.toString()),
                                 () -> Assertions.assertEquals("chrome.find(By.cssSelector: fooC)", pageA.pageB.pageC.fooC.toString()),
                                 () -> Assertions.assertNull(pageA.pageB.pageC.pageD.fooD));
        }

        @Test
        @DisplayName("propagates a thrown page object exception")
        void testPropagatesPageObjectException() {
            String message = "whoops";
            Throwable thrown = Assertions.assertThrows(PageObjectInitializationException.class,
                                                       () -> new ThrowingPageObjectInitializer(() -> new PageObjectInitializationException(message)).initializePage(new PageA(), mock(TestPatientDriver.class)),
                                                       "Should propagate a page object exception");
            Assertions.assertNull(thrown.getCause(), "The thrown exception shouldn't have a set cause");
            Assertions.assertEquals(message, thrown.getMessage(), "The thrown exception should have the given message");
        }

        @Test
        @DisplayName("wraps thrown exceptions in a PageObjectInitializationException")
        void testWrapsUnexpectedExceptions() {
            RuntimeException cause = new RuntimeException("message");
            Throwable thrown = Assertions.assertThrows(PageObjectInitializationException.class,
                                                       () -> new ThrowingPageObjectInitializer(() -> cause).initializePage(new PageA(), mock(TestPatientDriver.class)),
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

    private static class ThrowingPageObjectInitializer extends AbstractPageObjectInitializer<WebElement, TestPatientElementLocator, TestPatientElement> {

        private final Supplier<RuntimeException> exceptionSupplier;

        private ThrowingPageObjectInitializer(Supplier<RuntimeException> exceptionSupplier) {
            this.exceptionSupplier = exceptionSupplier;
        }

        @Override
        protected void preProcessPage(PageObject page,
                                      Field field,
                                      FindsElements<WebElement, TestPatientElementLocator, TestPatientElement> findsElements) { }

        @Override
        protected Optional<Object> getValueForField(PageObject page, Field field, FindsElements findsElements) {
            throw exceptionSupplier.get();
        }
    }

    private static class SuperPageA implements PageObject {

        @FindByCss("superFoo")
        final TestPatientElementLocator superFoo = null;
    }

    private static final class PageA extends SuperPageA {

        @FindByCss("fooA")
        private final TestPatientElementLocator fooA = null;

        @FindByCss("notToBeInitialized")
        private final TestPatientElementLocator notToBeInitialized = mock(TestPatientElementLocator.class);

        private final PageB pageB = new PageB();
    }

    private static final class PageB implements PageObject {

        @FindByCss("fooB")
        private final TestPatientElementLocator fooB = null;

        @FindByCss("notToBeInitialized")
        private final TestPatientElementLocator notToBeInitialized = mock(TestPatientElementLocator.class);

        private final PageC pageC = new PageC();
    }

    private static final class PageC implements PageObject {

        @FindByCss("fooC")
        private final TestPatientElementLocator fooC = null;

        @FindByCss("notToBeInitialized")
        private final TestPatientElementLocator notToBeInitialized = mock(TestPatientElementLocator.class);

        private final PageD pageD = new PageD();

        private PageA pageA = null;

        private PageA nullPage = null;
    }

    private static final class PageD {

        // Not a PageObject interface class so this should stay null

        @FindByCss("fooD")
        private final TestPatientElementLocator fooD = null;
    }
}