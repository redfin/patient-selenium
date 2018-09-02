package com.redfin.selenium;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.expect;
import static com.redfin.validity.Validity.validate;

/**
 * Base class for an element. This is the type that is built from an {@link AbstractElementFactory}
 * instance and is intended to be used as a replacement for the default selenium {@link WebElement}.
 * It can contain a cached instance of a selenium {@link WebElement} along with the ability to relocate
 * the element if it needs to (e.g. if it is lazily initialized for instance).
 * <p>
 * Any public method that interacts with the wrapped element except for {@link #isPresent()} and
 * {@link #isAbsent(Duration)} may throw a {@link NoSuchElementException} if the element cannot be
 * located.
 * <p>
 * Note that this class, like other classes in this library, is not intended for multi threaded use.
 *
 * @param <W> the type of {@link WebElement} this is wrapping.
 */
public abstract class AbstractElement<W extends WebElement>
           implements WrappedObject<W> {

    private final String description;
    private final int maxExecutionAttempts;
    private final Supplier<Optional<W>> elementSupplier;
    private final Function<Duration, Boolean> checkIfAbsentFunction;

    private W cachedElement;

    /**
     * Create a new {@link AbstractElement} instance that is lazily located.
     *
     * @param description           the String description for the element.
     *                              May not be null or empty.
     * @param maxExecutionAttempts  the int number of attempts that a command will be attempted.
     *                              May not be less than 1.
     * @param checkIfAbsentFunction the {@link Function} that takes in a {@link Duration} and returns
     *                              true if the selenium element is not present or false if it is.
     *                              May not be null.
     * @param elementSupplier       the {@link Supplier} used to actually locate the selenium element.
     *                              Should never return a null value.
     *                              May not be null.
     *
     * @throws IllegalArgumentException if description, checkIfAbsentFunction, or elementSupplier are null.
     * @throws IllegalArgumentException if description is empty or if maxExecutionAttempts is less than 1.
     */
    public AbstractElement(String description,
                           int maxExecutionAttempts,
                           Function<Duration, Boolean> checkIfAbsentFunction,
                           Supplier<Optional<W>> elementSupplier) {
        this(description, maxExecutionAttempts, checkIfAbsentFunction, elementSupplier, null);
    }

    /**
     * Create a new {@link AbstractElement} instance that with the given initial element.
     *
     * @param description           the String description for the element.
     *                              May not be null or empty.
     * @param maxExecutionAttempts  the int number of attempts that a command will be attempted.
     *                              May not be less than 1.
     * @param checkIfAbsentFunction the {@link Function} that takes in a {@link Duration} and returns
     *                              true if the selenium element is not present or false if it is.
     *                              May not be null.
     * @param elementSupplier       the {@link Supplier} used to actually locate the selenium element.
     *                              Should never return a null value.
     *                              May not be null.
     * @param initialElement        the {@link WebElement} that this is wrapping.
     *                              If not null this will be set as the cached wrapped instance.
     *                              If null, then this is a lazily located element.
     *
     * @throws IllegalArgumentException if description, checkIfAbsentFunction, or elementSupplier are null.
     * @throws IllegalArgumentException if description is empty or if maxExecutionAttempts is less than 1.
     */
    public AbstractElement(String description,
                           int maxExecutionAttempts,
                           Function<Duration, Boolean> checkIfAbsentFunction,
                           Supplier<Optional<W>> elementSupplier,
                           W initialElement) {
        this.description = validate().that(description).isNotEmpty();
        this.maxExecutionAttempts = validate().that(maxExecutionAttempts).isAtLeast(1);
        this.checkIfAbsentFunction = validate().that(checkIfAbsentFunction).isNotNull();
        this.elementSupplier = validate().that(elementSupplier).isNotNull();
        this.cachedElement = initialElement;
    }

    /**
     * Note that this method does NOT use the cached value and will clear out any
     * previously cached element.
     *
     * @return true if the selenium element this is representing can be located and
     * false otherwise.
     */
    public final boolean isPresent() {
        Optional<W> element = expect().withMessage("Received a null optional element from the element supplier")
                                      .that(elementSupplier.get())
                                      .isNotNull();
        element.ifPresent(e -> cachedElement = e);
        return element.isPresent();
    }

    /**
     * Wait until either the selenium element that is located by this element is either
     * no longer on the page or the timeout is reached.
     * <p>
     * Note that this method does NOT use the cached value and will clear out any
     * previously cached element.
     *
     * @param timeout the {@link Duration} to wait while waiting for a matching element to no longer be present.
     *                May not be null or negative.
     *
     * @return true if the selenium element this is representing can not be located and
     * false otherwise.
     *
     * @throws IllegalArgumentException if timeout is null or negative.
     */
    public final boolean isAbsent(Duration timeout) {
        validate().withMessage("Cannot check if an element is not present with a null or negative timeout")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        cachedElement = null;
        return expect().withMessage("Received a null Boolean from the check if absent function")
                       .that(checkIfAbsentFunction.apply(timeout))
                       .isNotNull();
    }

    @Override
    public final void accept(Consumer<W> consumer) {
        validate().withMessage("Cannot execute with a null consumer")
                  .that(consumer)
                  .isNotNull();
        execute(w -> {
            consumer.accept(w);
            return null;
        });
    }

    @Override
    public final <R> R apply(Function<W, R> function) {
        validate().withMessage("Cannot execute with a null function")
                  .that(function)
                  .isNotNull();
        return execute(function);
    }

    @Override
    public String toString() {
        return description;
    }

    /**
     * Actually perform the requested function with the wrapped selenium element.
     * This is set as protected so that a subclass can override the behavior if
     * desired.
     *
     * @param function the {@link Function} to be executed with the cached element.
     * @param <R>      the type returned by the given function.
     *
     * @return the result of applying the cached element to the given function.
     *
     * @throws IllegalArgumentException if function is null.
     * @throws NoSuchElementException   if the selenium element needs to be located and cannot be found.
     */
    protected <R> R execute(Function<W, R> function) {
        WebDriverException caught = null;
        for (int i = 0; i < maxExecutionAttempts; i++) {
            try {
                if (null == cachedElement) {
                    Optional<W> element = elementSupplier.get();
                    if (!element.isPresent()) {
                        throw new NoSuchElementException("No element found for: " + description);
                    }
                    cachedElement = element.get();
                }
                return function.apply(cachedElement);
            } catch (NoSuchElementException noElement) {
                // We just threw this, but need to handle and rethrow since other web driver exceptions are retried
                throw noElement;
            } catch (WebDriverException thrown) {
                // Clear out the cache and save the last thrown exception for throwing if this is last retry
                cachedElement = null;
                caught = thrown;
            }
        }
        // If we got here then we failed through all the retries, throw last caught exception
        throw expect().withMessage("Should never reach here with a null caught throwable")
                      .that(caught)
                      .isNotNull();
    }

    /**
     * @return the String description of this element.
     */
    protected final String getDescription() {
        return description;
    }

    /**
     * @return the int number of attempts to try to execute a command.
     */
    protected final int getMaxExecutionAttempts() {
        return maxExecutionAttempts;
    }

    /**
     * @return the {@link Supplier} that returns an {@link Optional} wrapped
     * selenium element for this element.
     */
    protected final Supplier<Optional<W>> getElementSupplier() {
        return elementSupplier;
    }

    /**
     * @return the {@link Function} used to check if the element to be located
     * by this instance is absent.
     */
    protected final Function<Duration, Boolean> getCheckIfAbsentFunction() {
        return checkIfAbsentFunction;
    }

    /**
     * @return the currently cached element. May be null.
     */
    protected final W getCachedElement() {
        return cachedElement;
    }

    /**
     * Set the cache to the given value.
     *
     * @param element the element to place as the cached value of this instance.
     *                May be null.
     */
    protected final void setCachedElement(W element) {
        cachedElement = element;
    }

    /**
     * Clear out the element cache for this instance.
     */
    protected final void clearCachedElement() {
        cachedElement = null;
    }
}
