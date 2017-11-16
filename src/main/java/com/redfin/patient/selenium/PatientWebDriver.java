package com.redfin.patient.selenium;

import com.redfin.patient.selenium.internal.AbstractPsDriver;
import com.redfin.patient.selenium.internal.Executor;
import com.redfin.patient.selenium.internal.PsConfig;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.redfin.validity.Validity.validate;

public final class PatientWebDriver
        extends AbstractPsDriver<WebDriver,
        WebElement> {

    private final PsConfig<WebElement> config;

    public PatientWebDriver(String driverDescription,
                            Executor<WebDriver> driverExecutor,
                            PsConfig<WebElement> config) {
        super(driverDescription, driverExecutor, config);
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
