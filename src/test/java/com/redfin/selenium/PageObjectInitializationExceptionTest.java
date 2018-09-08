package com.redfin.selenium;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

@DisplayName("A PageObjectInitializationException")
final class PageObjectInitializationExceptionTest {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Nested
    @DisplayName("when created with the zero argument constructor")
    class ZeroArgumentConstructorTests {

        @Test
        @DisplayName("returns null as both the message and the cause")
        void testZeroArgumentConstructorReturnsExpectedValues() {
            PageObjectInitializationException exception = new PageObjectInitializationException();
            Assertions.assertAll(() -> Assertions.assertNotNull(exception, "Should be able to instantiate a PageObjectInitializationException with the zero argument constructor"),
                                 () -> Assertions.assertNull(exception.getMessage(), "Should have a null message"),
                                 () -> Assertions.assertNull(exception.getCause(), "Should have a null cause"));
        }
    }

    @Nested
    @DisplayName("when created with the String argument constructor")
    class MessageArgumentConstructorTests {

        @ParameterizedTest
        @ArgumentsSource(MessageArgumentProvider.class)
        @DisplayName("returns the given message and null as the cause")
        void testMessageConstructorReturnsExpectedValues(String message) {
            PageObjectInitializationException exception = new PageObjectInitializationException(message);
            Assertions.assertAll(() -> Assertions.assertNotNull(exception, "Should be able to instantiate a PageObjectInitializationException with the message argument constructor"),
                                 () -> Assertions.assertEquals(message,
                                                               exception.getMessage(),
                                                               "Should have the expected message"),
                                 () -> Assertions.assertNull(exception.getCause(),
                                                             "Should have a null cause"));
        }
    }

    @Nested
    @DisplayName("when created with the Throwable argument constructor")
    class CauseArgumentConstructorTests {

        @ParameterizedTest
        @ArgumentsSource(CauseArgumentProvider.class)
        @DisplayName("returns the given cause and the toString of the cause as the message")
        void testCauseConstructorReturnsExpectedValues(Throwable cause) {
            PageObjectInitializationException exception = new PageObjectInitializationException(cause);
            Assertions.assertAll(() -> Assertions.assertNotNull(exception, "Should be able to instantiate a PageObjectInitializationException with the cause argument constructor"),
                                 () -> Assertions.assertEquals((null == cause) ? null : cause.toString(),
                                                               exception.getMessage(),
                                                               "Should have null as the message if the cause is null otherwise it should be the toString() of the cause."),
                                 () -> Assertions.assertEquals(cause,
                                                               exception.getCause(),
                                                               "Should have the expected cause"));
        }
    }

    @Nested
    @DisplayName("when created with the String and Throwable argument constructor")
    class MessageAndCauseConstructorTests {

        @ParameterizedTest
        @ArgumentsSource(MessageAndCauseArgumentProvider.class)
        @DisplayName("returns the given message and cause")
        void testMessageAndCauseConstructorReturnsExpectedValues(String message,
                                                                 Throwable cause) {
            PageObjectInitializationException exception = new PageObjectInitializationException(message, cause);
            Assertions.assertAll(() -> Assertions.assertNotNull(exception, "Should be able to instantiate a PageObjectInitializationException with the message and cause constructor"),
                                 () -> Assertions.assertEquals(message,
                                                               exception.getMessage(),
                                                               "Should have the expected message"),
                                 () -> Assertions.assertEquals(cause,
                                                               exception.getCause(),
                                                               "Should have the expected cause"));
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static final class MessageArgumentProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of("hello"),
                             Arguments.of((String) null));
        }
    }

    private static final class CauseArgumentProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(new RuntimeException("whoops")),
                             Arguments.of((Throwable) null));
        }
    }

    private static final class MessageAndCauseArgumentProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of("hello", new RuntimeException("whoops")),
                             Arguments.of(null, new RuntimeException("whoops")),
                             Arguments.of("hello", null),
                             Arguments.of(null, null));
        }
    }
}