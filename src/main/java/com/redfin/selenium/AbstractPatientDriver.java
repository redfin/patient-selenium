package com.redfin.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.expect;
import static com.redfin.validity.Validity.validate;

/**
 * Base class for a patient web driver.
 *
 * @param <D> the type of {@link WebDriver} this patient driver wraps.
 * @param <W> the type of {@link WebElement} the wrapped driver type locates.
 * @param <C> the concrete type of {@link AbstractPatientConfig} for the implementing subclass.
 * @param <L> the concrete type of {@link AbstractPatientElementLocator} for the implementing subclass.
 * @param <E> the concrete type of {@link AbstractPatientElement} for the implementing subclass.
 */
public abstract class AbstractPatientDriver<D extends WebDriver,
                                            W extends WebElement,
                                            C extends AbstractPatientConfig<W>,
                                            L extends AbstractPatientElementLocator<W, C, L, E>,
                                            E extends AbstractPatientElement<W, C, L, E>>
              extends AbstractBaseObject<W, C>
           implements FindsElements<W, C, L, E>,
                      WrappedExecutor<D> {

    private final Supplier<D> webDriverSupplier;

    private D driver = null;

    /**
     * Create a new instance of {@link AbstractPatientDriver}.
     *
     * @param config            the {@link C} to use for this driver and all objects it creates.
     *                          May not be null.
     * @param description       the String description fo this driver.
     *                          May not be null or empty.
     * @param webDriverSupplier the {@link Supplier} of {@link WebDriver} for this patient driver.
     *                          May not be null. Should never return a null object.
     *
     * @throws IllegalArgumentException if any argument is null or if the description is empty.
     */
    public AbstractPatientDriver(C config,
                                 String description,
                                 Supplier<D> webDriverSupplier) {
        super(config, description);
        this.webDriverSupplier = validate().withMessage("Cannot create a patient driver with a null web driver supplier")
                                           .that(webDriverSupplier)
                                           .isNotNull();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Public instance methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public final void accept(Consumer<D> consumer) {
        validate().withMessage("Cannot execute with a null consumer")
                  .that(consumer)
                  .isNotNull();
        execute(d -> {
            consumer.accept(d);
            return null;
        });
    }

    @Override
    public final <R> R apply(Function<D, R> function) {
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
        return buildElementLocator(getLocatorDescription(by), () -> findElements(by));
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Protected instance methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * @return the {@link Supplier} of {@link WebDriver}s for this instance.
     */
    protected final Supplier<D> getWebDriverSupplier() {
        return webDriverSupplier;
    }

    /**
     * @return the {@link WebDriver} currently cached for this instance. May return null.
     */
    protected final D getCachedDriver() {
        return driver;
    }

    /**
     * Set the given newValue as the current value in the cache, replacing anything previously there.
     *
     * @param newValue the {@link WebDriver} to set as the new cached value.
     *                 May be null.
     */
    protected final void setCachedDriver(D newValue) {
        this.driver = newValue;
    }

    /**
     * @param by the {@link By} locator to create a description for.
     *           Will never be null.
     *
     * @return the created String description for an {@link AbstractPatientElementLocator} instance
     * created with the given by. Should never return null or the empty String.
     */
    protected abstract String getLocatorDescription(By by);

    /**
     * @param locatorDescription  the String description for the built {@link AbstractPatientElementLocator}.
     *                            Will never be null or empty.
     * @param elementListSupplier the {@link Supplier} of a list of {@link WebElement} objects for the element locator.
     *                            Will never be null.
     *
     * @return a new {@link AbstractPatientElementLocator} instance with the given values.
     */
    protected abstract L buildElementLocator(String locatorDescription,
                                             Supplier<List<W>> elementListSupplier);

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private instance methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private <R> R execute(Function<D, R> function) {
        if (null == driver) {
            driver = expect().withMessage("Received a null web driver from the driver supplier")
                             .that(webDriverSupplier.get())
                             .isNotNull();
        }
        return function.apply(driver);
    }

    @SuppressWarnings("unchecked")
    private List<W> findElements(By by) {
        try {
            return execute(d -> (List<W>) d.findElements(by));
        } catch (RuntimeException e) {
            if (getConfig().isIgnoredLookupException(e.getClass())) {
                return Collections.emptyList();
            }
            throw e;
        }
    }
}
