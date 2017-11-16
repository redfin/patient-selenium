package com.redfin.patient.selenium;

import com.redfin.patient.selenium.internal.AbstractPsElement;
import com.redfin.patient.selenium.internal.Executor;
import com.redfin.patient.selenium.internal.PsConfig;
import org.openqa.selenium.WebElement;

import static com.redfin.validity.Validity.validate;

public final class PatientWebElement
        extends AbstractPsElement<WebElement> {

    private final PsConfig<WebElement> config;

    public PatientWebElement(String elementDescription,
                             Executor<WebElement> elementExecutor,
                             PsConfig<WebElement> config) {
        super(elementDescription, elementExecutor, config);
        this.config = validate().withMessage("Cannot use a null patient web config.")
                                .that(config)
                                .isNotNull();
    }

    public PatientWebElementLocatorBuilder find() {
        return new PatientWebElementLocatorBuilder(getElementDescription(),
                                                   withWrappedElement(),
                                                   config);
    }
}
