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

import com.redfin.patient.selenium.internal.AbstractPsElementLocatorBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.redfin.validity.Validity.validate;

public class ExampleElementLocatorBuilder
     extends AbstractPsElementLocatorBuilder<WebDriver,
                                             WebElement,
                                             ExampleConfig,
                                             ExampleDriver,
                                             ExampleElementLocatorBuilder,
                                             ExampleElementLocator,
                                             ExampleElement> {

    public ExampleElementLocatorBuilder(String description,
                                        ExampleConfig config,
                                        ExampleDriver driver,
                                        Function<By, List<WebElement>> baseSeleniumLocatorFunction) {
        super(description,
              config,
              driver,
              baseSeleniumLocatorFunction);
    }

    @Override
    protected ExampleElementLocatorBuilder getThis() {
        return this;
    }

    @Override
    protected ExampleElementLocator build(String description,
                                          Supplier<List<WebElement>> elementSupplier) {
        return new ExampleElementLocator(description,
                                         getConfig(),
                                         getDriver(),
                                         getWait(),
                                         getDefaultTimeout(),
                                         getDefaultNotPresentTimeout(),
                                         elementSupplier,
                                         getElementFilter());
    }

    public ExampleElementLocator byId(String id) {
        validate().withMessage("Cannot locate elements with a null or empty locator string.")
                  .that(id)
                  .isNotEmpty();
        return by(By.id(id));
    }

    public ExampleElementLocator byClassName(String className) {
        validate().withMessage("Cannot locate elements with a null or empty locator string.")
                  .that(className)
                  .isNotEmpty();
        return by(By.className(className));
    }

    public ExampleElementLocator byName(String name) {
        validate().withMessage("Cannot locate elements with a null or empty locator string.")
                  .that(name)
                  .isNotEmpty();
        return by(By.name(name));
    }

    public ExampleElementLocator byTagName(String tagName) {
        validate().withMessage("Cannot locate elements with a null or empty locator string.")
                  .that(tagName)
                  .isNotEmpty();
        return by(By.tagName(tagName));
    }

    public ExampleElementLocator byCss(String selector) {
        validate().withMessage("Cannot locate elements with a null or empty locator string.")
                  .that(selector)
                  .isNotEmpty();
        return by(By.cssSelector(selector));
    }

    public ExampleElementLocator byXpath(String expression) {
        validate().withMessage("Cannot locate elements with a null or empty locator string.")
                  .that(expression)
                  .isNotEmpty();
        return by(By.xpath(expression));
    }

    public ExampleElementLocator byLinkText(String linkText) {
        validate().withMessage("Cannot locate elements with a null or empty locator string.")
                  .that(linkText)
                  .isNotEmpty();
        return by(By.linkText(linkText));
    }

    public ExampleElementLocator byPartialLinkText(String partialLinkText) {
        validate().withMessage("Cannot locate elements with a null or empty locator string.")
                  .that(partialLinkText)
                  .isNotEmpty();
        return by(By.partialLinkText(partialLinkText));
    }
}
