package com.redfin.patient.selenium.example;

import com.redfin.patient.selenium.internal.FindsElements;
import com.redfin.patient.selenium.internal.AbstractPageObjectInitializer;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.redfin.validity.Validity.expect;
import static com.redfin.validity.Validity.validate;

public final class ExamplePageObjectInitializer
        extends AbstractPageObjectInitializer<WebDriver,
        WebElement,
        ExamplePsDriver,
        ExamplePsConfig,
        ExamplePsElementLocatorBuilder,
        ExamplePsElementLocator,
        ExamplePsElement> {

    public ExamplePageObjectInitializer(ExamplePsDriver driver) {
        super(driver);
    }

    @Override
    protected Optional<ExamplePsElementLocator> buildElementLocatorOptional(FindsElements<WebElement,
            ExamplePsConfig,
            ExamplePsElementLocatorBuilder,
            ExamplePsElementLocator,
            ExamplePsElement> currentContext,
                                                                            Field field) {
        validate().withMessage("Cannot use a null search context.")
                  .that(currentContext)
                  .isNotNull();
        validate().withMessage("Cannot use a null field.")
                  .that(field)
                  .isNotNull();
        ExampleFindBy[] finds = field.getAnnotationsByType(ExampleFindBy.class);
        if (null == finds || finds.length == 0) {
            return Optional.empty();
        }
        List<By> bys = new ArrayList<>();
        for (ExampleFindBy find : finds) {
            int counter = 0;
            if (!find.id().isEmpty()) {
                bys.add(By.id(find.id()));
                counter++;
            }
            if (!find.css().isEmpty()) {
                bys.add(By.cssSelector(find.css()));
                counter++;
            }
            if (!find.xpath().isEmpty()) {
                bys.add(By.xpath(find.xpath()));
                counter++;
            }
            expect().withMessage("Should have exactly 1 locator strategy defined by a ExampleFindBy annotation.")
                    .that(counter)
                    .isEqualTo(1);
        }
        ByChained by = new ByChained(bys.toArray(new By[bys.size()]));
        Duration timeout = null;
        ExampleFindDuration durationAnnotation = field.getAnnotation(ExampleFindDuration.class);
        if (null != durationAnnotation) {
            timeout = Duration.ofSeconds(durationAnnotation.tryingForSeconds());
        }
        if (null != timeout) {
            return Optional.of(currentContext.find()
                                             .withTimeout(timeout)
                                             .by(by));
        } else {
            return Optional.empty();
        }
    }
}
