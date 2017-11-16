package com.redfin.patient.selenium.internal;

import org.openqa.selenium.WebElement;

import static com.redfin.validity.Validity.validate;

public abstract class AbstractPsElement<W extends WebElement> {

    private final String elementDescription;
    private final Executor<W> elementExecutor;
    private final PsConfig<W> config;

    public AbstractPsElement(String elementDescription,
                             Executor<W> elementExecutor,
                             PsConfig<W> config) {
        this.elementDescription = validate().withMessage("Cannot use a null or empty element description.")
                                            .that(elementDescription)
                                            .isNotEmpty();
        this.elementExecutor = validate().withMessage("Cannot use a null element executor.")
                                         .that(elementExecutor)
                                         .isNotNull();
        this.config = validate().withMessage("Cannot use a null config.")
                                .that(config)
                                .isNotNull();
    }

    protected final String getElementDescription() {
        return elementDescription;
    }

    protected final PsConfig<W> getConfig() {
        return config;
    }

    public Executor<W> withWrappedElement() {
        return elementExecutor;
    }

    @Override
    public String toString() {
        return elementDescription;
    }
}
