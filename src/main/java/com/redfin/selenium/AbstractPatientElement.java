package com.redfin.selenium;

import com.redfin.patience.PatientWait;
import com.redfin.patience.exceptions.PatientTimeoutException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.expect;
import static com.redfin.validity.Validity.validate;

/**
 * Base class for a patient element.
 *
 * @param <W>    the type of {@link WebElement} this wraps.
 * @param <C>    the concrete type of {@link AbstractPatientConfig} for the implementing subclass.
 * @param <L>    the concrete type of {@link AbstractPatientElementLocator} that this type builds.
 * @param <THIS> the concrete subclass of this element.
 */
public abstract class AbstractPatientElement<W extends WebElement,
                                             C extends AbstractPatientConfig<W>,
                                             L extends AbstractPatientElementLocator<W, C, L, THIS>,
                                             THIS extends AbstractPatientElement<W, C, L, THIS>>
              extends AbstractBaseObject<W, C>
           implements FindsElements<W, C, L, THIS>,
                      WrappedExecutor<W> {

    private final Supplier<Optional<W>> elementSupplier;
    private final PatientWait wait;
    private final Duration timeout;

    private W cachedElement = null;

    /**
     * Create a new, lazily located, instance of {@link AbstractPatientElement}.
     *
     * @param config          the {@link C} for this element.
     *                        May not be null.
     * @param description     the String description of this element.
     *                        May not be null or empty.
     * @param elementSupplier the {@link Supplier} of an element for this to wrap.
     *                        May not be null. Should never return null.
     * @param wait            the {@link PatientWait} for waiting for a valid element.
     *                        May not be null.
     * @param timeout         the {@link Duration} timeout used when waiting for an element.
     *                        May not be null or negative.
     */
    public AbstractPatientElement(C config,
                                  String description,
                                  Supplier<Optional<W>> elementSupplier,
                                  PatientWait wait,
                                  Duration timeout) {
        super(config, description);
        this.elementSupplier = validate().that(elementSupplier).isNotNull();
        this.wait = validate().that(wait).isNotNull();
        this.timeout = validate().that(timeout).isGreaterThanOrEqualToZero();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Public instance methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Wait for a selenium element to be found with the matching locator and
     * predicate filter. This method does NOT use any previously cached element
     * but rather triggers a new element lookup. Return true as soon as a matching
     * element is located and place that element in the internal cache. If no
     * element is found within the set timeout then the internal cache will be
     * cleared and false will be returned.
     *
     * @return true if there is a currently matching element or false otherwise.
     */
    public boolean isPresent() {
        // Always start by clearing the cache to trigger a new lookup
        cachedElement = null;
        return getElementPatiently().isPresent();
    }

    /**
     * Wait for no selenium element to be found with the matching locator and
     * predicate filter. This method does NOT use any previously cached element
     * but rather triggers a new element lookup. Return true as soon as no matching
     * element is located and clear the internal. If the timeout is reached before
     * a lookup results in no matching element then return false. If the timeout
     * is reached and false will be returned the last matching element located will
     * be set in the internal cache.
     *
     * @param timeout the Duration timeout for waiting for no element.
     *                May not be null or negative.
     *
     * @return true if there is no currently matching element or false otherwise.
     *
     * @throws IllegalArgumentException if timeout is null or negative.
     */
    public boolean isAbsent(Duration timeout) {
        validate().withMessage("Cannot check if an element is absent with a null or negative timeout")
                  .that(timeout)
                  .isGreaterThanOrEqualToZero();
        // Always start by clearing the cache to trigger a new lookup
        cachedElement = null;
        // Check patiently if the element disappears
        AtomicReference<W> lastElement = new AtomicReference<>(null);
        try {
            wait.from(() -> {
                Optional<W> element = elementSupplier.get();
                element.ifPresent(lastElement::set);
                return !element.isPresent();
            }).get(timeout);
            // It exited without an exception so the element is no longer present
            return true;
        } catch (PatientTimeoutException e) {
            // It timed out so there was still an element present
            cachedElement = lastElement.get();
            return false;
        }
    }

    @Override
    public final void accept(Consumer<W> consumer) {
        validate().withMessage("Cannot execute with a null consumer")
                  .that(consumer)
                  .isNotNull();
        execute(e -> {
            consumer.accept(e);
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
    public final L find(By by) {
        validate().withMessage("Cannot locate elements with a null By locator")
                  .that(by)
                  .isNotNull();
        return buildElementLocator(getLocatorDescription(by), () -> findChildElements(by));
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Protected instance methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * @return the given {@link Supplier} of an element for this instance.
     */
    protected final Supplier<Optional<W>> getElementSupplier() {
        return elementSupplier;
    }

    /**
     * @return the given {@link PatientWait} for this instance.
     */
    protected final PatientWait getWait() {
        return wait;
    }

    /**
     * @return the given {@link Duration} timeout for this instance for waiting for
     * an element to be present.
     */
    protected final Duration getTimeout() {
        return timeout;
    }

    /**
     * @return the current element in the internal cache. This does not
     * try to locate the element if the cache is empty. May return
     * null if the internal cache is empty.
     */
    protected final W getCachedElement() {
        return cachedElement;
    }

    /**
     * Set the internal cache to the given value.
     *
     * @param newValue the element to set in the internal cache.
     *                 May be null.
     */
    protected final void setCachedElement(W newValue) {
        this.cachedElement = newValue;
    }

    /**
     * @param by the {@link By} locator for the element locator to be built.
     *           Will never be null.
     *
     * @return the String description of the {@link L} locator type for this instance with
     * the given {@link By}. Should never return null or the empty String.
     */
    protected abstract String getLocatorDescription(By by);

    /**
     * @param locatorDescription  the String description for the element locator to be built.
     *                            Will never be null or empty.
     * @param elementListSupplier the {@link Supplier} of an element list for the locator to be built.
     *                            Will never be null.
     *
     * @return the {@link L} locator instance to be built with the given description and supplier. Should
     * never return null.
     */
    protected abstract L buildElementLocator(String locatorDescription,
                                             Supplier<List<W>> elementListSupplier);

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private instance methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private <R> R execute(Function<W, R> function) {
        RuntimeException caught = null;
        for (int i = 0; i < getConfig().getMaxElementActionAttempts(); i++) {
            try {
                if (null == cachedElement) {
                    cachedElement = this.getElementPatiently()
                                        .orElseThrow(() -> new NoSuchElementException(String.format("Unable to find the element for [%s] after a timeout of [%s]",
                                                                                                    this,
                                                                                                    timeout)));
                }
                return function.apply(cachedElement);
            } catch (NoSuchElementException e) {
                // Clear the cache on any exception
                cachedElement = null;
                // Just threw this, propagate it
                throw e;
            } catch (RuntimeException e) {
                // Clear the cache on any exception
                cachedElement = null;
                // Check if this is an ignored action type for actions
                if (e instanceof StaleElementReferenceException) {
                    // Do nothing here, we have already cleared the cache, stale
                    // exceptions can't be an ignored action type since they are
                    // always ignored
                } else if (!getConfig().isIgnoredActionException(e.getClass())) {
                    // Not an ignored type, propagate the exception
                    throw e;
                }
                caught = e;
            }
        }
        throw expect().withMessage("Should not have reached this point without a non-null caught exception")
                      .that(caught)
                      .isNotNull();
    }

    /*
     * Simply use the given supplier and by to find the
     * list of elements and return it, or an empty
     * list if no matching elements are found. There is no waiting
     * involved in this lookup as the waiting will be done by the
     * caller of the method if an empty list is returned.
     * Unhandled and non-ignored exceptions will be thrown.
     */

    @SuppressWarnings("unchecked")
    private List<W> findChildElements(By by) {
        try {
            if (null == cachedElement) {
                cachedElement = elementSupplier.get().orElse(null);
            }
            if (null != cachedElement) {
                return (List<W>) cachedElement.findElements(by);
            }
        } catch (StaleElementReferenceException e) {
            // If the element is stale, clear the cache and return an empty list
            cachedElement = null;
        } catch (RuntimeException e) {
            // In the case of any other exception clear the cache and check if it is an ignored type
            cachedElement = null;
            if (!getConfig().isIgnoredLookupException(e.getClass())) {
                throw e;
            }
        }
        return Collections.emptyList();
    }

    /*
     * Use the given supplier and filter to find the matching
     * matching element. This uses the set wait and
     * timeout to wait patiently for results. If a non-empty
     * optional is found simply return it. If the timeout is
     * reached before a non-empty optional is found then
     * return an empty optional. Unhandled and non-ignored
     * exceptions will be thrown.
     */

    private Optional<W> getElementPatiently() {
        try {
            cachedElement = wait.from(elementSupplier::get)
                                .withFilter(Optional::isPresent)
                                .get(timeout)
                                .orElse(null);
        } catch (PatientTimeoutException ignore) {
            // Do nothing
        }
        return Optional.ofNullable(cachedElement);
    }
}
