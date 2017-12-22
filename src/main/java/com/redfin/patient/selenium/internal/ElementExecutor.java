package com.redfin.patient.selenium.internal;

import org.openqa.selenium.WebElement;

public interface ElementExecutor<W extends WebElement>
         extends Executor<W> {

    void clearCachedElement();
}
