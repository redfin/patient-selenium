package com.redfin.selenium;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class TestPageObjectInitializer
           extends AbstractPageObjectInitializer {

    public static class WidgetObject implements PageObject {

        private List<Field> widgetFieldList = null;
    }

    @Override
    protected void preProcessPage(PageObject page,
                                  List<Field> fieldsList) {
        if (page instanceof WidgetObject) {
            ((WidgetObject) page).widgetFieldList = new ArrayList<>(fieldsList);
        }
    }

    @Override
    protected Optional<Object> getValue(List<Field> fieldsList) {
        Field field = fieldsList.get(fieldsList.size() - 1);
        if (TestElementFactory.class.isAssignableFrom(field.getType())) {
            TestElementFactory factory = new TestElementFactory();
            factory.setFieldsList(fieldsList);
            return Optional.of(factory);
        }
        return Optional.empty();
    }
}
