package com.redfin.patient.selenium.internal;

import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;
import java.util.List;

@FunctionalInterface
public interface PageObjectLocatorFunction<W extends WebElement,
        C extends PsConfig<W, C, B, L, E>,
        B extends PsElementLocatorBuilder<W, C, B, L, E>,
        L extends PsElementLocator<W, C, B, L, E>,
        E extends PsElement<W, C, B, L, E>> {

    L buildElementLocator(FindsElements<W, C, B, L, E> currentContext,
                          List<Field> parentFields,
                          Field elementLocatorField);
}
