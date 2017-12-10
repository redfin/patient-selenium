package com.redfin.patient.selenium.internal;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.redfin.validity.Validity.expect;

public abstract class AbstractPsWidgetObject<D extends WebDriver,
        W extends WebElement,
        C extends AbstractPsConfig<D, W, C, P, B, L, E>,
        P extends AbstractPsDriver<D, W, C, P, B, L, E>,
        B extends AbstractPsElementLocatorBuilder<D, W, C, P, B, L, E>,
        L extends AbstractPsElementLocator<D, W, C, P, B, L, E>,
        E extends AbstractPsElement<D, W, C, P, B, L, E>>
        extends AbstractPsPageObject<D, W, C, P, B, L, E> {

    private final L baseElement = null;

    protected final L getBaseElement() {
        expect().withMessage("This widget object has not been initialized by a page object initializer.")
                .that(baseElement)
                .isNotNull();
        return baseElement;
    }
}
