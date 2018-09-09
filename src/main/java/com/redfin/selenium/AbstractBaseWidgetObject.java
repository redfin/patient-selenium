package com.redfin.selenium;

import org.openqa.selenium.WebElement;

/**
 * Base class for a widget type.
 *
 * @param <W> the type of {@link WebElement} for this instance.
 * @param <C> the concrete type of {@link AbstractPatientConfig} for the implementing subclass.
 * @param <L> the concrete type of {@link AbstractPatientElementLocator} for the implementing subclass.
 * @param <E> the concrete type of {@link AbstractPatientElement} for the implementing subclass.
 */
public abstract class AbstractBaseWidgetObject<W extends WebElement,
                                               C extends AbstractPatientConfig<W>,
                                               L extends AbstractPatientElementLocator<W, C, L, E>,
                                               E extends AbstractPatientElement<W, C, L, E>> {

    private E widgetElement = null;

    /**
     * @return the set {@link E} element for this widget.
     */
    protected final E getWidgetElement() {
        if (null == widgetElement) {
            throw new PageObjectInitializationException("This widget was never initialized");
        }
        return widgetElement;
    }

    /*
     * Only intended for use by the AbstractPageObjectInitializer so make
     * the visibility package private.
     */

    void setWidgetElement(E value) {
        if (null != widgetElement) {
            throw new PageObjectInitializationException("This widget was already initialized");
        }
        this.widgetElement = value;
    }
}
