package com.redfin.patient.selenium.example;

import com.redfin.patient.selenium.internal.CachingExecutor;
import com.redfin.patient.selenium.internal.AbstractPsElement;
import org.openqa.selenium.WebElement;

public final class ExamplePsElement
        extends AbstractPsElement<WebElement,
        ExamplePsConfig,
        ExamplePsElementLocatorBuilder,
        ExamplePsElementLocator,
        ExamplePsElement> {

    public ExamplePsElement(String description,
                            ExamplePsConfig config,
                            CachingExecutor<WebElement> elementExecutor) {
        super(description, config, elementExecutor);
    }

    @Override
    protected ExamplePsElementLocatorBuilder createElementLocatorBuilder(String elementLocatorBuilderDescription) {
        return new ExamplePsElementLocatorBuilder(elementLocatorBuilderDescription,
                                                  getConfig(),
                                                  by -> withWrappedElement().apply(d -> d.findElements(by)));
    }

    // Add in the API methods desired for the element

    public ExamplePsElement click() {
        withWrappedElement().accept(WebElement::click);
        return this;
    }

    public ExamplePsElement sendKeys(CharSequence... keysToSend) {
        withWrappedElement().accept(e -> e.sendKeys(keysToSend));
        return this;
    }
}
