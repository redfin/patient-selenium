package com.redfin.patient.selenium.internal;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.redfin.validity.Validity.validate;

public interface Executor<T> {

    default void accept(Consumer<T> consumer) {
        validate().withMessage("Cannot execute with a null consumer.")
                  .that(consumer)
                  .isNotNull();
        apply(t -> {
            consumer.accept(t);
            return null;
        });
    }

    <R> R apply(Function<T, R> function);
}
