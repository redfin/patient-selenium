package com.redfin.patient.selenium.internal;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.expect;
import static com.redfin.validity.Validity.validate;

public abstract class AbstractCachingExecutor<T>
           implements Executor<T> {

    private final Supplier<T> cachedObjectSupplier;

    private T cachedObject;

    public AbstractCachingExecutor(T initialObject,
                                   Supplier<T> cachedObjectSupplier) {
        this.cachedObject = initialObject;
        this.cachedObjectSupplier = validate().withMessage("Cannot use a null cached object supplier.")
                                              .that(cachedObjectSupplier)
                                              .isNotNull();
    }

    protected T getObject() {
        if (null == cachedObject) {
            cachedObject = expect().withMessage("Received a null object from the object supplier.")
                                   .that(cachedObjectSupplier.get())
                                   .isNotNull();
        }
        return cachedObject;
    }

    protected void setCachedObject(T newValue) {
        this.cachedObject = newValue;
    }

    protected boolean isCachedObjectNull() {
        return null == cachedObject;
    }

    @Override
    public <R> R apply(Function<T, R> function) {
        return validate().withMessage("Cannot execute a null function.")
                         .that(function)
                         .isNotNull()
                         .apply(getObject());
    }
}
