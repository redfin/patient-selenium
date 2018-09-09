package com.redfin.selenium.implementation;

import com.redfin.selenium.AbstractBasePageObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class TestBasePageObject
              extends AbstractBasePageObject<WebDriver,
                                             WebElement,
                                             TestPatientConfig,
                                             TestPatientDriver,
                                             TestPatientElementLocator,
                                             TestPatientElement> {}
