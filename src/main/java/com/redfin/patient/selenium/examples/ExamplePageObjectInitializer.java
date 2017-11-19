/*
 * Copyright: (c) 2017 Redfin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redfin.patient.selenium.examples;

import com.redfin.patient.selenium.internal.AbstractPageObjectInitializer;
import com.redfin.patient.selenium.internal.FindsElements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;
import java.time.Duration;
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
        validate().withMessage("Cannot use a null current search context")
                  .that(currentContext)
                  .isNotNull();
        validate().withMessage("Cannot use a null field.")
                  .that(field)
                  .isNotNull();
        // Check if the page object field on the outer page object has an annotation on it
        ExamplePageObjectFind find = field.getAnnotation(ExamplePageObjectFind.class);
        if (null != find) {
            // There is an annotation, use it to build an element locator
            return Optional.of(currentContext.find()
                                             .withTimeout(buildTimeout(find))
                                             .by(buildBy(find)));
        } else {
            // No annotation, return an empty optional
            return Optional.empty();
        }
    }

    private By buildBy(ExamplePageObjectFind find) {
        validate().withMessage("Cannot use a null find.")
                  .that(find)
                  .isNotNull();
        expect().withMessage("Have an page object find annotation with a null or empty css selector string.")
                .that(find.css())
                .isNotEmpty();
        return By.cssSelector(find.css());
    }

    private Duration buildTimeout(ExamplePageObjectFind find) {
        validate().withMessage("Cannot use a null find.")
                  .that(find)
                  .isNotNull();
        expect().withMessage("Have a page object find annotation with a negative trying for seconds int.")
                .that(find.tryingForSeconds())
                .isAtLeast(0);
        return Duration.ofSeconds(find.tryingForSeconds());
    }
}
