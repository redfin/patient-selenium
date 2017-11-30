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
import java.util.Arrays;
import java.util.List;

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
    public ExamplePsElementLocator buildElementLocator(FindsElements<WebElement,
            ExamplePsConfig,
            ExamplePsElementLocatorBuilder,
            ExamplePsElementLocator,
            ExamplePsElement> currentContext,
                                                       List<Field> parentFields,
                                                       Field elementLocatorField) {
        validate().withMessage("Cannot use a null search context.")
                  .that(currentContext)
                  .isNotNull();
        validate().withMessage("Cannot use a null parent field list.")
                  .that(parentFields)
                  .isNotNull();
        validate().withMessage("Cannot use a null element locator field.")
                  .that(elementLocatorField)
                  .isNotNull();
        List<Field> fields = new ArrayList<>(parentFields);
        fields.add(elementLocatorField);
        By by = buildBy(fields);
        Duration timeout = buildTimeout(fields);
        if (null != timeout) {
            return currentContext.find()
                                 .withTimeout(timeout)
                                 .by(by);
        } else {
            return currentContext.find()
                                 .by(by);
        }
    }

    private By buildBy(List<Field> fields) {
        ExampleFindBy[] finds = fields.stream()
                                      .flatMap(field -> Arrays.stream(field.getAnnotationsByType(ExampleFindBy.class)))
                                      .toArray(ExampleFindBy[]::new);
        expect().withMessage("Unable to create an element locator without any ExampleFindBy annotations.")
                .that(finds)
                .isNotEmpty();
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
            expect().withMessage("Should have exactly 1 locator strategy defined by any one ExampleFindBy annotation.")
                    .that(counter)
                    .isEqualTo(1);
        }
        return new ByChained(bys.toArray(new By[bys.size()]));
    }

    private Duration buildTimeout(List<Field> fields) {
        Field field = fields.get(fields.size() - 1);
        ExampleTimeouts durationAnnotation = field.getAnnotation(ExampleTimeouts.class);
        if (null != durationAnnotation) {
            expect().withMessage("Cannot have an ExampleTimeouts annotation with a negative tryingForSeconds")
                    .that(durationAnnotation.tryingForSeconds())
                    .isAtLeast(0);
            return Duration.ofSeconds(durationAnnotation.tryingForSeconds());
        } else {
            return null;
        }
    }
}
