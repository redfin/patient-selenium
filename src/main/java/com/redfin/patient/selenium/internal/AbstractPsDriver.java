package com.redfin.patient.selenium.internal;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.redfin.validity.Validity.validate;

public abstract class AbstractPsDriver<D extends WebDriver,
        W extends WebElement> {

    private final String driverDescription;
    private final Executor<D> driverExecutor;
    private final PsConfig<W> config;

    public AbstractPsDriver(String driverDescription,
                            Executor<D> driverExecutor,
                            PsConfig<W> config) {
        this.driverDescription = validate().withMessage("Cannot use a null or empty driver description.")
                                           .that(driverDescription)
                                           .isNotEmpty();
        this.driverExecutor = validate().withMessage("Cannot use a null driver executor.")
                                        .that(driverExecutor)
                                        .isNotNull();
        this.config = validate().withMessage("Cannot use a null config.")
                                .that(config)
                                .isNotNull();
    }

    protected final String getDriverDescription() {
        return driverDescription;
    }

    protected final PsConfig<W> getConfig() {
        return config;
    }

    public Executor<D> withWrappedDriver() {
        return driverExecutor;
    }

    public void quit() {
        withWrappedDriver().accept(WebDriver::quit);
        withWrappedDriver().clearCache();
    }

    public void close() {
        int handleCount = withWrappedDriver().apply(d -> d.getWindowHandles().size());
        withWrappedDriver().accept(WebDriver::close);
        if (handleCount <= 1) {
            // Last window handle was closed, clear the cache because the driver was quit
            withWrappedDriver().clearCache();
        }
    }

    @Override
    public String toString() {
        return driverDescription;
    }
}
