package com.redfin.patient.selenium.internal;

import org.openqa.selenium.WebDriver;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.expect;
import static com.redfin.validity.Validity.validate;

public final class DefaultDriverExecutor<D extends WebDriver>
        implements Executor<D> {

    private final Supplier<D> driverSupplier;

    private D driver = null;

    public DefaultDriverExecutor(Supplier<D> driverSupplier) {
        this.driverSupplier = validate().withMessage("Cannot create a driver executor with a null driver supplier.")
                                        .that(driverSupplier)
                                        .isNotNull();
    }

    private D getDriver() {
        if (null == driver) {
            driver = expect().withMessage("Received a null driver from the driver supplier.")
                             .that(driverSupplier.get())
                             .isNotNull();
        }
        return driver;
    }

    @Override
    public <R> R apply(Function<D, R> function) {
        validate().withMessage("Cannot execute with a null function.")
                  .that(function)
                  .isNotNull();
        return function.apply(getDriver());
    }

    @Override
    public void clearCache() {
        driver = null;
    }
}
