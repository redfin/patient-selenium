package com.redfin.selenium;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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

        @Test
        @DisplayName("throws exception for null class object")
        void testThrowsExceptionForNullClass() {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> new TestPageObjectInitializer(null),
                                    "Should throw an exception for a null class type to the page object initializer constructor");
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
            Foo fooA = pageA.notToBeInitialized;
            Foo fooB = pageA.pageB.notToBeInitialized;
            Foo fooC = pageA.pageB.pageC.notToBeInitialized;
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
                                 () -> Assertions.assertNull(fooA.fieldsList),
                                 () -> Assertions.assertNull(fooB.fieldsList),
                                 () -> Assertions.assertNull(fooC.fieldsList),
                                 () -> Assertions.assertNotNull(pageA.fooA),
                                 () -> Assertions.assertNotNull(pageA.superFoo),
                                 () -> Assertions.assertNotNull(pageA.pageB.fooB),
                                 () -> Assertions.assertNotNull(pageA.pageB.pageC),
                                 () -> Assertions.assertEquals(1, pageA.fooA.fieldsList.size()),
                                 () -> Assertions.assertEquals(1, pageA.superFoo.fieldsList.size()),
                                 () -> Assertions.assertEquals(2, pageA.pageB.fooB.fieldsList.size()),
                                 () -> Assertions.assertEquals(3, pageA.pageB.pageC.fooC.fieldsList.size()));
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test Helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static TestPageObjectInitializer getInstance() {
        return new TestPageObjectInitializer(Foo.class);
    }

    private static final class TestPageObjectInitializer
                       extends AbstractPageObjectInitializer<Foo> {

        private TestPageObjectInitializer(Class<Foo> fooClass) {
            super(fooClass);
        }

        @Override
        protected Foo buildValue(List<Field> fields) {
            return new Foo(fields);
        }
    }

    private static final class Foo {

        private final List<Field> fieldsList;

        private Foo(List<Field> fieldsList) {
            this.fieldsList = (null == fieldsList) ? null : new ArrayList<>(fieldsList);
        }
    }

    private static class SuperPageA {

        final Foo superFoo = null;
    }

    private static final class PageA extends SuperPageA {

        private final Foo fooA = null;

        private final Foo notToBeInitialized = new Foo(null);

        private final PageB pageB = new PageB();
    }

    private static final class PageB {

        private final Foo fooB = null;

        private final Foo notToBeInitialized = new Foo(null);

        private final PageC pageC = new PageC();
    }

    private static final class PageC {

        private final Foo fooC = null;

        private final Foo notToBeInitialized = new Foo(null);

        private PageA pageA = null;

        private PageA nullPage = null;
    }
}
