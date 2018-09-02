package com.redfin.selenium;

public abstract class TestWidget
           implements WidgetPageObject<TestElementFactory> {

    private TestElementFactory widgetFactory = null;

    @Override
    public TestElementFactory getWidgetObject() {
        return widgetFactory;
    }

    @Override
    public void setWidgetObject(TestElementFactory newValue) {
        this.widgetFactory = newValue;
    }
}
