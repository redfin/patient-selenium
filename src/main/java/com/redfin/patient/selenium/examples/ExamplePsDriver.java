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

import com.redfin.patient.selenium.internal.AbstractPsDriver;
import com.redfin.patient.selenium.internal.CachingExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public final class ExamplePsDriver
        extends AbstractPsDriver<WebDriver,
        WebElement,
        ExamplePsConfig,
        ExamplePsElementLocatorBuilder,
        ExamplePsElementLocator,
        ExamplePsElement> {

    public ExamplePsDriver(String description,
                           ExamplePsConfig config,
                           CachingExecutor<WebDriver> driverExecutor) {
        super(description, config, driverExecutor);
    }

    @Override
    protected ExamplePsElementLocatorBuilder createElementLocatorBuilder(String elementLocatorBuilderDescription,
                                                                         ExamplePsConfig config) {
        return new ExamplePsElementLocatorBuilder(elementLocatorBuilderDescription,
                                                  config,
                                                  by -> withWrappedDriver().apply(d -> d.findElements(by)));
    }

    // implement any other element methods you want to expose as the API for your driver

    public ExamplePsDriver get(String url) {
        System.out.println(String.format("%s.get(%s)",
                                         this,
                                         url));
        withWrappedDriver().accept(d -> d.get(url));
        return this;
    }
}
