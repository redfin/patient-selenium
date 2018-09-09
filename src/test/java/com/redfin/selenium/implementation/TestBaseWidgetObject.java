package com.redfin.selenium.implementation;

import com.redfin.selenium.AbstractBaseWidgetObject;
import org.openqa.selenium.WebElement;

public abstract class TestBaseWidgetObject
              extends AbstractBaseWidgetObject<WebElement,
                                               TestPatientConfig,
                                               TestPatientElementLocator,
                                               TestPatientElement> {}
