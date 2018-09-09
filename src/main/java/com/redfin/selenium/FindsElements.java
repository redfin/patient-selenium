package com.redfin.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * A FindsElements is a type defining that the implementing class
 * is able to create element locator instances.
 *
 * @param <W> the type of {@link WebElement} for the implementing subclass.
 * @param <C> the concrete type of {@link AbstractPatientConfig} for the implementing subclass.
 * @param <L> the type of {@link AbstractPatientElementLocator} for the implementing subclass.
 * @param <E> the type of {@link AbstractPatientElement} for the implementing subclass.
 */
public interface FindsElements<W extends WebElement,
                               C extends AbstractPatientConfig<W>,
                               L extends AbstractPatientElementLocator<W, C, L, E>,
                               E extends AbstractPatientElement<W, C, L, E>> {

    /**
     * Return a new element locator instance for the given {@link By} locator.
     *
     * @param by a {@link By} used to locate elements.
     *           May not be null.
     *
     * @return a new instance of the element locator type for this.
     */
    L find(By by);
}
