package com.redfin.patient.selenium.internal;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.expect;
import static com.redfin.validity.Validity.validate;

public final class DefaultElementExecutor<W extends WebElement>
        implements Executor<W> {

    private static final int MAX_RETRIES = 3;

    private final Supplier<W> elementSupplier;

    private W cachedElement;

    public DefaultElementExecutor(W initialElement,
                                  Supplier<W> elementSupplier) {
        this.cachedElement = validate().withMessage("Cannot create an element executor with a null initial element.")
                                       .that(initialElement)
                                       .isNotNull();
        this.elementSupplier = validate().withMessage("Cannot create an element executor with a null element supplier.")
                                         .that(elementSupplier)
                                         .isNotNull();
    }

    private W getElement() {
        if (null == cachedElement) {
            cachedElement = expect().withMessage("Received a null element from the element supplier.")
                                    .that(elementSupplier.get())
                                    .isNotNull();
        }
        return cachedElement;
    }

    @Override
    public <R> R apply(Function<W, R> function) {
        validate().withMessage("Cannot execute with a null function.")
                  .that(function)
                  .isNotNull();
        RuntimeException exception = null;
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                W element = getElement();
                return function.apply(element);
            } catch (StaleElementReferenceException stale) {
                exception = stale;
                // We need to re-locate the element, clear the cache
                cachedElement = null;
            }
        }
        throw expect().withMessage("Max retries reached but the exception is null.")
                      .that(exception)
                      .isNotNull();
    }

    @Override
    public void clearCache() {
        cachedElement = null;
    }
}
