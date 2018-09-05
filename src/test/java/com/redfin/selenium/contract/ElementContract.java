package com.redfin.selenium.contract;

import com.redfin.selenium.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.stream.Stream;

/**
 * A test contract interface for types implementing the {@link Element} interface.
 *
 * @param <E> the type of {@link Element} implementation being tested.
 * @param <W> the type of {@link WebElement} the element is wrapping.
 */
public interface ElementContract<E extends Element<W>, W extends WebElement> {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test Cases
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Test
    @DisplayName("returns true for isPresent() when the element is present")
    default void testIsPresentReturnsTrueForPresentElement(@IsPresent(true) E instance) {
        Assumptions.assumeTrue(null != instance,
                               "Should have received a non-null element instance");
        Assertions.assertTrue(instance.isPresent(),
                              "A present element should return true for isPresent()");
    }

    @Test
    @DisplayName("returns false for isPresent() when the element is not present")
    default void testIsPresentReturnsFalseForNonPresentElement(@IsPresent(false) E instance) {
        Assumptions.assumeTrue(null != instance,
                               "Should have received a non-null element instance");
        Assertions.assertFalse(instance.isPresent(),
                               "A non-present element should return false for isPresent()");
    }

    @Test
    @DisplayName("replaces the previous cached object for the isPresent() call on present element")
    default void testIsPresentReplacesPreviousCache(@IsPresent(true) E instance,
                                                  @Cacheable W object) {
        Assumptions.assumeTrue(null != instance,
                               "Should have received a non-null element instance");
        Assumptions.assumeTrue(null != object,
                               "Should have received a non-null object instance");
        instance.setCache(object);
        Assumptions.assumeTrue(object == getCachedObjectFromExecutor(instance),
                               "The object should be set in the cache now");
        instance.isPresent();
        Assertions.assertNotSame(object,
                                 getCachedObjectFromExecutor(instance),
                                 "After calling isPresent() the previous cache should have been replaced");
    }

    @Test
    @DisplayName("clears the previous cached object for the isPresent() call on non-present element")
    default void testIsPresentClearsPreviousCache(@IsPresent(false) E instance,
                                                  @Cacheable W object) {
        Assumptions.assumeTrue(null != instance,
                               "Should have received a non-null element instance");
        Assumptions.assumeTrue(null != object,
                               "Should have received a non-null object instance");
        instance.setCache(object);
        Assumptions.assumeTrue(object == getCachedObjectFromExecutor(instance),
                               "The object should be set in the cache now");
        instance.isPresent();
        Assertions.assertNull(getCachedObjectFromExecutor(instance),
                              "After calling isPresent() the previous cache should have been cleared for non-present element");
    }

    @ParameterizedTest
    @ArgumentsSource(ValidIsAbsentDurations.class)
    @DisplayName("returns false for isAbsent(Duration) when the element is present and the duration is valid")
    default void testIsAbsentReturnsFalseForPresentElement(Duration timeout,
                                                           @IsPresent(true) E instance) {
        Assumptions.assumeTrue(null != instance,
                               "Should have received a non-null element instance");
        Assertions.assertFalse(instance.isAbsent(timeout),
                               "A present element should return false for isAbsent(Duration)");
    }

    @ParameterizedTest
    @ArgumentsSource(ValidIsAbsentDurations.class)
    @DisplayName("returns true for isAbsent(Duration) when the element is not present and the duration is valid")
    default void testIsAbsentReturnsTrueForNonPresentElement(Duration timeout,
                                                             @IsPresent(false) E instance) {
        Assumptions.assumeTrue(null != instance,
                               "Should have received a non-null element instance");
        Assertions.assertTrue(instance.isAbsent(timeout),
                              "A non-present element should return true for isAbsent(Duration)");
    }

    @Test
    @DisplayName("clears the previous cache for the isPresent() call")
    default void testIsAbsentClearsPreviousCache(@IsPresent(false) E instance,
                                                 @Cacheable W object) {
        Assumptions.assumeTrue(null != instance,
                               "Should have received a non-null element instance");
        Assumptions.assumeTrue(null != object,
                               "Should have received a non-null object instance");
        instance.setCache(object);
        Assumptions.assumeTrue(object == getCachedObjectFromExecutor(instance),
                               "The object should be set in the cache now");
        instance.isAbsent(Duration.ZERO);
        Assertions.assertNull(getCachedObjectFromExecutor(instance),
                              "After calling isAbsent(Duration) the previous cache should have been cleared");
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidIsAbsentDurations.class)
    default void testIsAbsentThrowsForInvalidDuration(Duration timeout,
                                                      @IsPresent(false) E instance) {
        Assumptions.assumeTrue(null != instance,
                               "Should have received a non-null element instance");
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> instance.isAbsent(timeout),
                                "The isAbsent(Duration) method should throw an exception for an invalid timeout duration.");
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test Helpers
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * @param instance the instance of the type implementing {@link Element}.
     *                 Should never be null.
     *
     * @return the object currently in the cache of instance.
     */
    W getCachedObjectFromExecutor(E instance);

    class ValidIsAbsentDurations implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(Duration.ZERO),
                             Arguments.of(Duration.ofSeconds(1)));
        }
    }

    class InvalidIsAbsentDurations implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(Duration.ofMillis(-1)),
                             Arguments.of(Duration.ofMinutes(Integer.MIN_VALUE)),
                             Arguments.of((Duration) null));
        }
    }
}
