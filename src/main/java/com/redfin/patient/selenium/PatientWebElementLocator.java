package com.redfin.patient.selenium;

import com.redfin.patience.PatientWait;
import com.redfin.patient.selenium.internal.AbstractPsElementLocator;
import com.redfin.patient.selenium.internal.DefaultElementExecutor;
import com.redfin.patient.selenium.internal.PsConfig;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.validate;

public final class PatientWebElementLocator
        extends AbstractPsElementLocator<WebElement,
        PatientWebElement> {

    public PatientWebElementLocator(String elementLocationDescription,
                                    Supplier<List<WebElement>> seleniumElementSupplier,
                                    Predicate<WebElement> elementFilter,
                                    PatientWait isPresentWait,
                                    PatientWait isNotPresentWait,
                                    Duration defaultTimeout,
                                    Duration defaultNotPresentTimeout,
                                    PsConfig<WebElement> config) {
        super(elementLocationDescription,
              seleniumElementSupplier,
              elementFilter,
              isPresentWait,
              isNotPresentWait,
              defaultTimeout,
              defaultNotPresentTimeout,
              config);
    }

    @Override
    protected PatientWebElement buildElement(String elementDescription,
                                             WebElement initialElement,
                                             Supplier<WebElement> elementSupplier) {
        validate().withMessage("Cannot use a null or empty element description.")
                  .that(elementDescription)
                  .isNotEmpty();
        validate().withMessage("Cannot use a null initial element.")
                  .that(initialElement)
                  .isNotNull();
        validate().withMessage("Cannot use a null element supplier.")
                  .that(elementSupplier)
                  .isNotNull();
        return new PatientWebElement(elementDescription,
                                     new DefaultElementExecutor<>(initialElement,
                                                                  elementSupplier),
                                     getConfig());
    }
}
