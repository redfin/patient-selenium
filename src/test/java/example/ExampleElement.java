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

package example;

import com.redfin.patient.selenium.AbstractPsElement;
import com.redfin.patient.selenium.ElementExecutor;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;

public class ExampleElement
     extends AbstractPsElement<WebDriver,
                               WebElement,
                               ExampleConfig,
                               ExampleDriver,
                               ExampleElementLocatorBuilder,
                               ExampleElementLocator,
                               ExampleElement> {

    public ExampleElement(String description,
                          ExampleConfig config,
                          ExampleDriver driver,
                          ElementExecutor<WebElement> elementExecutor) {
        super(description,
              config,
              driver,
              elementExecutor);
    }

    @Override
    public ExampleElementLocatorBuilder find() {
        return new ExampleElementLocatorBuilder(String.format("%s.find()",
                                                              getDescription()),
                                                getConfig(),
                                                getDriver(),
                                                   by -> withWrappedElement().apply(e -> e.findElements(by)));
    }

    public ExampleElement click() {
        getConfig().getLogConsumer()
                   .accept(String.format("%s.click()",
                                         this));
        withWrappedElement().accept(WebElement::click);
        return this;
    }

    public ExampleElement submit() {
        getConfig().getLogConsumer()
                   .accept(String.format("%s.submit()",
                                         this));
        withWrappedElement().accept(WebElement::submit);
        return this;
    }

    public ExampleElement sendKeys(CharSequence... keysToSend) {
        getConfig().getLogConsumer()
                   .accept(String.format("%s.sendKeys(%s)",
                                         this,
                                         Arrays.toString(keysToSend)));
        withWrappedElement().accept(e -> e.sendKeys(keysToSend));
        return this;
    }

    public ExampleElement clear() {
        getConfig().getLogConsumer()
                   .accept(String.format("%s.clear()",
                                         this));
        withWrappedElement().accept(WebElement::clear);
        return this;
    }

    public String getTagName() {
        return withWrappedElement().apply(WebElement::getTagName);
    }

    public String getText() {
        return withWrappedElement().apply(WebElement::getText);
    }

    public String getAttribute(String name) {
        return withWrappedElement().apply(e -> e.getAttribute(name));
    }

    public String getCssValue(String propertyName) {
        return withWrappedElement().apply(e -> e.getCssValue(propertyName));
    }

    public Point getLocation() {
        return withWrappedElement().apply(WebElement::getLocation);
    }

    public Dimension getSize() {
        return withWrappedElement().apply(WebElement::getSize);
    }

    public Rectangle getRect() {
        return withWrappedElement().apply(WebElement::getRect);
    }

    public boolean isSelected() {
        return withWrappedElement().apply(WebElement::isSelected);
    }

    public boolean isEnabled() {
        return withWrappedElement().apply(WebElement::isEnabled);
    }

    public boolean isDisplayed() {
        return withWrappedElement().apply(WebElement::isDisplayed);
    }
}
