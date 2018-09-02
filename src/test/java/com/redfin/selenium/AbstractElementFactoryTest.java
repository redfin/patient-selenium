package com.redfin.selenium;

import com.redfin.patience.PatientWait;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("An AbstractElementFactory")
final class AbstractElementFactoryTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test Cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("while being constructed")
    final class ConstructorTests {

        @ParameterizedTest
        @ArgumentsSource(ValidConstructorArguments.class)
        @DisplayName("succeeds with valid arguments")
        void testSucceedsWithValidArguments(String description,
                                            PatientWait wait,
                                            Predicate<WebElement> filter,
                                            Duration timeout,
                                            Supplier<List<WebElement>> elementListSupplier) {
            Assertions.assertNotNull(getInstance(description, wait, filter, timeout, elementListSupplier),
                                     "Should be able to instantiate an element factory with valid arguments");
        }

        @ParameterizedTest
        @ArgumentsSource(InvalidConstructorArguments.class)
        @DisplayName("throws exception for invalid arguments")
        void testThrowsExceptionForInvalidArguments(String description,
                                                    PatientWait wait,
                                                    Predicate<WebElement> filter,
                                                    Duration timeout,
                                                    Supplier<List<WebElement>> elementListSupplier) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance(description, wait, filter, timeout, elementListSupplier),
                                    "Calling the constructor with invalid arguments should throw an exception");
        }
    }

    @Nested
    @DisplayName("once instantiated")
    final class BehaviorTests {

        @Test
        @DisplayName("toString() method returns the given description")
        void testToStringReturnsDescription() {
            String description = "foo";
            Assertions.assertSame(description,
                                  getInstance(description).toString(),
                                  "The toString method should return the given description");
        }

        @Test
        @DisplayName("getters return given values")
        void testReturnsGivenArguments() {
            String description = "foo";
            PatientWait wait = mock(PatientWait.class);
            Predicate<WebElement> filter = e -> true;
            Duration timeout = Duration.ZERO;
            Supplier<List<WebElement>> elementListSupplier = ArrayList::new;
            TestElementFactory instance = getInstance(description, wait, filter, timeout, elementListSupplier);
            Assertions.assertAll(() -> Assertions.assertSame(description, instance.getDescription()),
                                 () -> Assertions.assertSame(wait, instance.getWait()),
                                 () -> Assertions.assertSame(filter, instance.getFilter()),
                                 () -> Assertions.assertSame(timeout, instance.getTimeout()),
                                 () -> Assertions.assertSame(elementListSupplier, instance.getElementListSupplier()));
        }

        @Test
        @DisplayName("returns the same built element for repeated calls to atIndex(int) with same index")
        void testAtIndexReturnsSameInstanceForMultipleCallsWithSameIndex() {
            int index = 0;
            TestElementFactory instance = getInstance();
            TestElement element = instance.atIndex(index);
            Assertions.assertAll(() -> Assertions.assertSame(element, instance.atIndex(index)),
                                 () -> Assertions.assertNotSame(element, instance.atIndex(index + 1)),
                                 () -> Assertions.assertSame(element, instance.atIndex(index)));
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, Integer.MIN_VALUE})
        @DisplayName("throws exception for invalid arguments to atIndex(int)")
        void testAtIndexThrowsForInvalidArguments(int index) {
            Assertions.assertThrows(IllegalArgumentException.class,
                                    () -> getInstance().atIndex(index),
                                    "Should throw an exception for atIndex(int) with an invalid argument");
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 5})
        @DisplayName("returns lazily located elements from atIndex(int)")
        void testAtIndexReturnsLazilyLocatedElements(int index) {
            Assertions.assertNull(getInstance().atIndex(index).getCachedElement(),
                                  "Elements from the atIndex(int) method should not have a cached element");
        }

        @Test
        @DisplayName("returns the expected list for getAll()")
        void testGetAllReturnsExpectedList() {
            List<WebElement> expectedList = new ArrayList<>();
            expectedList.add(mock(WebElement.class));
            expectedList.add(mock(WebElement.class));
            expectedList.add(mock(WebElement.class));
            expectedList.add(mock(WebElement.class));
            for (int i = 0; i < expectedList.size(); i++) {
                if (i != 3) {
                    when(expectedList.get(i).isDisplayed()).thenReturn(true);
                } else {
                    when(expectedList.get(i).isDisplayed()).thenReturn(false);
                }
            }
            String description = "hello";
            PatientWait wait = PatientWait.builder().build();
            Predicate<WebElement> filter = WebElement::isDisplayed;
            Duration timeout = Duration.ZERO;
            Supplier<List<WebElement>> elementListSupplier = () -> expectedList;
            List<TestElement> elementList = getInstance(description, wait, filter, timeout, elementListSupplier).getAll();
            // Remove the one that shouldn't have passed the filter
            expectedList.remove(3);
            Assertions.assertAll(() -> Assertions.assertNotNull(elementList, "The returned element list should not be null"),
                                 () -> Assertions.assertEquals(3, elementList.size(), "The returned list should have a size of 3"),
                                 () -> Assertions.assertIterableEquals(expectedList, elementList.stream().map(TestElement::getCachedElement).collect(Collectors.toList()),
                                                                       "The returned element list should have the expected elements as the initially cached values"));
        }

        @Test
        @DisplayName("should return an empty list to getAll() if there are no matching elements")
        void testGetAllReturnsEmptyListForNoMatches() {
            List<WebElement> expectedList = new ArrayList<>();
            expectedList.add(mock(WebElement.class));
            expectedList.add(mock(WebElement.class));
            expectedList.add(mock(WebElement.class));
            expectedList.add(mock(WebElement.class));
            for (WebElement element : expectedList) {
                when(element.isDisplayed()).thenReturn(false);
            }
            String description = "hello";
            PatientWait wait = PatientWait.builder().build();
            Predicate<WebElement> filter = WebElement::isDisplayed;
            Duration timeout = Duration.ZERO;
            Supplier<List<WebElement>> elementListSupplier = () -> expectedList;
            List<TestElement> elementList = getInstance(description, wait, filter, timeout, elementListSupplier).getAll();
            Assertions.assertTrue(elementList.isEmpty(), "No matching elements should return an empty list to getAll()");
        }

        @Test
        @DisplayName("should return an element that gives the expected values for isPresent() and isAbsent(Duration) when present")
        void testReturnsElementWithExpectedPresentChecksIfPresent() {
            List<WebElement> expectedList = new ArrayList<>();
            expectedList.add(mock(WebElement.class));
            expectedList.add(mock(WebElement.class));
            expectedList.add(mock(WebElement.class));
            expectedList.add(mock(WebElement.class));
            for (WebElement element : expectedList) {
                when(element.isDisplayed()).thenReturn(true);
            }
            String description = "hello";
            PatientWait wait = PatientWait.builder().build();
            Predicate<WebElement> filter = WebElement::isDisplayed;
            Duration timeout = Duration.ZERO;
            Supplier<List<WebElement>> elementListSupplier = () -> expectedList;
            TestElement element = getInstance(description, wait, filter, timeout, elementListSupplier).atIndex(0);
            Assertions.assertAll(() -> Assertions.assertTrue(element.isPresent()),
                                 () -> Assertions.assertFalse(element.isAbsent(Duration.ZERO)));
        }

        @Test
        @DisplayName("should return an element that gives the expected values for isPresent() and isAbsent(Duration) when absent")
        void testReturnsElementWithExpectedPresentChecksIfAbsent() {
            List<WebElement> expectedList = new ArrayList<>();
            expectedList.add(mock(WebElement.class));
            expectedList.add(mock(WebElement.class));
            expectedList.add(mock(WebElement.class));
            expectedList.add(mock(WebElement.class));
            for (WebElement element : expectedList) {
                when(element.isDisplayed()).thenReturn(false);
            }
            String description = "hello";
            PatientWait wait = PatientWait.builder().build();
            Predicate<WebElement> filter = WebElement::isDisplayed;
            Duration timeout = Duration.ZERO;
            Supplier<List<WebElement>> elementListSupplier = () -> expectedList;
            TestElement element = getInstance(description, wait, filter, timeout, elementListSupplier).atIndex(0);
            Assertions.assertAll(() -> Assertions.assertFalse(element.isPresent()),
                                 () -> Assertions.assertTrue(element.isAbsent(Duration.ZERO)));
        }

        @Test
        @DisplayName("should return an element that has the correct n-th found element")
        void testReturnsElementWithTheCorrectNthValue() {
            List<WebElement> expectedList = new ArrayList<>();
            expectedList.add(mock(WebElement.class));
            expectedList.add(mock(WebElement.class));
            expectedList.add(mock(WebElement.class));
            expectedList.add(mock(WebElement.class));
            when(expectedList.get(0).isDisplayed()).thenReturn(false);
            when(expectedList.get(1).isDisplayed()).thenReturn(true);
            when(expectedList.get(2).isDisplayed()).thenReturn(false);
            when(expectedList.get(3).isDisplayed()).thenReturn(true);
            String description = "hello";
            PatientWait wait = PatientWait.builder().build();
            Predicate<WebElement> filter = WebElement::isDisplayed;
            Duration timeout = Duration.ZERO;
            Supplier<List<WebElement>> elementListSupplier = () -> expectedList;
            TestElement element = getInstance(description, wait, filter, timeout, elementListSupplier).atIndex(1);
            element.accept(e -> {
            });
            Assertions.assertSame(expectedList.get(3),
                                  element.getCachedElement(),
                                  "Should return the element with the expected n-th found matching element");
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 5})
        @DisplayName("builds elements with the expected description")
        void testBuiltElementHasExpectedDescription(int index) {
            String description = "foo";
            Assertions.assertEquals(description + ".atIndex(" + index + ")",
                                    getInstance(description).atIndex(index).getDescription(),
                                    "The element factory should build elements with the expected description");
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test Helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static TestElementFactory getInstance() {
        return getInstance("defaultDescription");
    }

    @SuppressWarnings("unchecked")
    private static TestElementFactory getInstance(String description) {
        return getInstance(description,
                           mock(PatientWait.class),
                           mock(Predicate.class),
                           Duration.ZERO,
                           mock(Supplier.class));
    }

    private static TestElementFactory getInstance(String description,
                                                  PatientWait wait,
                                                  Predicate<WebElement> filter,
                                                  Duration timeout,
                                                  Supplier<List<WebElement>> elementListSupplier) {
        return new TestElementFactory(description, wait, filter, timeout, elementListSupplier);
    }

    private static final class ValidConstructorArguments
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of("hello", mock(PatientWait.class), mock(Predicate.class), Duration.ZERO, mock(Supplier.class)),
                             Arguments.of("hello", mock(PatientWait.class), mock(Predicate.class), Duration.ofDays(5), mock(Supplier.class)),
                             Arguments.of("😄", mock(PatientWait.class), mock(Predicate.class), Duration.ZERO, mock(Supplier.class)),
                             Arguments.of("😄", mock(PatientWait.class), mock(Predicate.class), Duration.ofDays(5), mock(Supplier.class)));
        }
    }

    private static final class InvalidConstructorArguments
                    implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of("", mock(PatientWait.class), mock(Predicate.class), Duration.ZERO, mock(Supplier.class)),
                             Arguments.of("hello", mock(PatientWait.class), mock(Predicate.class), Duration.ofMillis(-1), mock(Supplier.class)),
                             Arguments.of(null, mock(PatientWait.class), mock(Predicate.class), Duration.ZERO, mock(Supplier.class)),
                             Arguments.of("hello", null, mock(Predicate.class), Duration.ZERO, mock(Supplier.class)),
                             Arguments.of("hello", mock(PatientWait.class), null, Duration.ZERO, mock(Supplier.class)),
                             Arguments.of("hello", mock(PatientWait.class), mock(Predicate.class), null, mock(Supplier.class)),
                             Arguments.of("hello", mock(PatientWait.class), mock(Predicate.class), Duration.ZERO, null));
        }
    }
}
