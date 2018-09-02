package com.redfin.selenium;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class TestPageObjectInitializer
           extends AbstractPageObjectInitializer<TestElementFactory,
                                                 TestWidget> {

    @Override
    protected Class<TestElementFactory> getElementFactoryClass() {
        return TestElementFactory.class;
    }

    @Override
    protected Class<TestWidget> getWidgetClass() {
        return TestWidget.class;
    }

    @Override
    protected TestElementFactory buildValue(List<Field> fields) {
        TestElementFactory factory = mock(TestElementFactory.class);
        when(factory.getFieldsList()).thenReturn(new ArrayList<>(fields));
        return factory;
    }
}
