package com.redfin.selenium;


public interface WidgetPageObject<T>
         extends PageObject {

    T getWidgetObject();

    void setWidgetObject(T newValue);
}
