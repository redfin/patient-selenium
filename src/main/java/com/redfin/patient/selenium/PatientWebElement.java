package com.redfin.patient.selenium;

import com.redfin.patient.selenium.internal.AbstractPsElement;
import com.redfin.patient.selenium.internal.Executor;
import org.openqa.selenium.WebElement;

import static com.redfin.validity.Validity.validate;

public final class PatientWebElement
        extends AbstractPsElement<WebElement> {

    private final PatientWebConfig config;

    public PatientWebElement(String elementDescription,
                             Executor<WebElement> elementExecutor,
                             PatientWebConfig config) {
        super(elementDescription, elementExecutor);
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
