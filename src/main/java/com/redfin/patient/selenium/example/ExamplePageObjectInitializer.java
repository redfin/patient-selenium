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

package com.redfin.patient.selenium.example;

import com.redfin.patient.selenium.internal.AbstractPsPageObjectInitializer;
import com.redfin.patient.selenium.internal.FindsElements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.redfin.validity.Validity.expect;
import static com.redfin.validity.Validity.validate;

public class ExamplePageObjectInitializer
     extends AbstractPsPageObjectInitializer<WebDriver,
                                             WebElement,
                                             ExampleConfig,
                                             ExampleDriver,
                                             ExampleElementLocatorBuilder,
                                             ExampleElementLocator,
                                             ExampleElement> {

    public ExamplePageObjectInitializer(ExampleDriver driver) {
        super(driver, ExampleElementLocator.class);
    }

    @Override
    protected ExampleElementLocator buildElementLocator(FindsElements<WebDriver,
                                                                      WebElement,
                                                                      ExampleConfig,
                                                                      ExampleDriver,
                                                                      ExampleElementLocatorBuilder,
                                                                      ExampleElementLocator,
                                                                      ExampleElement> searchContext,
                                                        List<Field> fields) {
        validate().withMessage("Cannot use a null search context.")
                  .that(searchContext)
                  .isNotNull();
        validate().withMessage("Cannot use a null or empty fields list.")
                  .that(fields)
                  .isNotEmpty();
        By[] bys = fields.stream()
                         .flatMap(field -> Arrays.stream(field.getAnnotationsByType(ExampleFind.class)))
                         .filter(Objects::nonNull)
                         .map(this::findToBy)
                         .toArray(By[]::new);
        expect().withMessage("Cannot initialize an element locator that doesn't have any ExampleFind annotations.")
                .that(bys)
                .isNotEmpty();
        if (bys.length == 1) {
            return searchContext.find().by(bys[0]);
        } else {
            return searchContext.find().by(new ByChained(bys));
        }
    }

    private By findToBy(ExampleFind find) {
        validate().withMessage("Cannot use a null ExampleFind annotation.")
                  .that(find)
                  .isNotNull();
        int counter = 0;
        By by = null;
        if (!find.id().isEmpty()) {
            counter++;
            by = By.id(find.id());
        }
        if (!find.className().isEmpty()) {
            counter++;
            by = By.className(find.className());
        }
        if (!find.name().isEmpty()) {
            counter++;
            by = By.name(find.name());
        }
        if (!find.tagName().isEmpty()) {
            counter++;
            by = By.tagName(find.tagName());
        }
        if (!find.css().isEmpty()) {
            counter++;
            by = By.cssSelector(find.css());
        }
        if (!find.xpath().isEmpty()) {
            counter++;
            by = By.xpath(find.xpath());
        }
        if (!find.linkText().isEmpty()) {
            counter++;
            by = By.linkText(find.linkText());
        }
        if (!find.partialLinkText().isEmpty()) {
            counter++;
            by = By.partialLinkText(find.partialLinkText());
        }
        expect().withMessage("A ExampleFind annotation must have exactly 1 locator type selected.")
                .that(counter)
                .isEqualTo(1);
        return expect().withMessage("Error creating a By locator from the ExampleFind annotation: " + find)
                       .that(by)
                       .isNotNull();
    }
}
