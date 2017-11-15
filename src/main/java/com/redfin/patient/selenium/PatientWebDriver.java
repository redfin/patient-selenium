package com.redfin.patient.selenium;

import com.redfin.patient.selenium.internal.AbstractPsDriver;
import com.redfin.patient.selenium.internal.Executor;
import org.openqa.selenium.WebDriver;

import static com.redfin.validity.Validity.validate;

public final class PatientWebDriver
        extends AbstractPsDriver<WebDriver> {

    private final PatientWebConfig config;

    public PatientWebDriver(String driverDescription,
                            Executor<WebDriver> driverExecutor,
                            PatientWebConfig config) {
        super(driverDescription, driverExecutor);
        this.config = validate().withMessage("Cannot use a null patient web config.")
                                .that(config)
                                .isNotNull();
    }

    public PatientWebElementLocatorBuilder find() {
        return new PatientWebElementLocatorBuilder(getDriverDescription(),
                                                   withWrappedDriver(),
                                                   config);
    }
}
