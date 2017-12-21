package com.redfin.patient.selenium.internal;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.expect;
import static com.redfin.validity.Validity.validate;

public abstract class AbstractExecutor<T>
           implements Executor<T> {

    private final Supplier<T> supplier;

    protected T object;

    public AbstractExecutor(Supplier<T> supplier) {
        this(null, supplier);
    }

    public AbstractExecutor(T initialObject,
                            Supplier<T> supplier) {
        this.object = initialObject;
        this.supplier = validate().withMessage("Cannot use a null supplier.")
                                  .that(supplier)
                                  .isNotNull();
    }

    protected T getCachedObject() {
        if (null == object) {
            object = expect().withMessage("Received a null object from the supplier.")
                             .that(supplier.get())
                             .isNotNull();
        }
        return object;
    }

    @Override
    public <R> R apply(Function<T, R> function) {
        validate().withMessage("Cannot execute with a null function.")
                  .that(function)
                  .isNotNull();
        return function.apply(getCachedObject());
    }
}
