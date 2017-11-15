package com.redfin.patient.selenium;

import com.redfin.patient.selenium.internal.AbstractPsElementLocatorBuilder;
import com.redfin.patient.selenium.internal.Executor;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.validate;

public final class PatientWebElementLocatorBuilder
        extends AbstractPsElementLocatorBuilder<WebElement,
        PatientWebElementLocatorBuilder,
        PatientWebElementLocator,
        PatientWebElement> {

    private static final String BY_FORMAT = "%s.find().by(%s)";

    private final String previousDescription;
    private final Executor<? extends SearchContext> searchExecutor;
    private final PatientWebConfig config;

    public PatientWebElementLocatorBuilder(String previousDescription,
                                           Executor<? extends SearchContext> searchExecutor,
                                           PatientWebConfig config) {
        super(validate().withMessage("Cannot use a null patient web config")
                        .that(config)
                        .isNotNull()
                        .getDefaultIsPresentWait(),
              config.getDefaultIsNotPresentWait(),
              config.getDefaultIsPresentTimeout(),
              config.getDefaultIsNotPresentTimeout());
        this.previousDescription = validate().withMessage("Cannot use a null or empty previous description.")
                                             .that(previousDescription)
                                             .isNotEmpty();
        this.searchExecutor = validate().withMessage("Cannot use a null search executor.")
                                        .that(searchExecutor)
                                        .isNotNull();
        this.config = config;
    }

    @Override
    protected PatientWebElementLocatorBuilder getThis() {
        return this;
    }

    @Override
    public PatientWebElementLocator by(By by) {
        validate().withMessage("Cannot use a null By locator")
                  .that(by)
                  .isNotNull();
        Supplier<List<WebElement>> elementSupplier = () -> searchExecutor.apply(e -> e.findElements(by));
        return new PatientWebElementLocator(String.format(BY_FORMAT,
                                                          previousDescription,
                                                          by),
                                            elementSupplier,
                                            getElementFilter(),
                                            getIsPresentWait(),
                                            getIsNotPresentWait(),
                                            getDefaultIsPresentTimeout(),
                                            getDefaultIsNotPresentTimeout(),
                                            config);
    }
}
