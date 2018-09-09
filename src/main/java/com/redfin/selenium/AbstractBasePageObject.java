package com.redfin.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Base class for a widget page object type.
 *
 * @param <D> the type of {@link WebDriver} for this instance.
 * @param <W> the type of {@link WebElement} for this instance.
 * @param <C> the concrete type of {@link AbstractPatientConfig} for the implementing subclass.
 * @param <P> the concrete type of {@link AbstractPatientDriver} for the implementing subclass.
 * @param <L> the concrete type of {@link AbstractPatientElementLocator} for the implementing subclass.
 * @param <E> the concrete type of {@link AbstractPatientElement} for the implementing subclass.
 */
public abstract class AbstractBasePageObject<D extends WebDriver,
                                             W extends WebElement,
                                             C extends AbstractPatientConfig<W>,
                                             P extends AbstractPatientDriver<D, W, C, L, E>,
                                             L extends AbstractPatientElementLocator<W, C, L, E>,
                                             E extends AbstractPatientElement<W, C, L, E>> {

    private P driver;

    protected final P getDriver() {
        if (null == driver) {
            throw new PageObjectInitializationException("This page object was never initialized");
        }
        return driver;
    }

    /*
     * Only intended for use by the AbstractPageObjectInitializer so make
     * the visibility package private.
     */

    void setDriver(P driver) {
        if (null != this.driver) {
            throw new PageObjectInitializationException("This page object was already initialized");
        }
        this.driver = driver;
    }
}
