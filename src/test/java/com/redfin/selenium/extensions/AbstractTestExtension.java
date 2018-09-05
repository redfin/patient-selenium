package com.redfin.selenium.extensions;

import org.junit.jupiter.api.extension.ExtensionContext;

public abstract class AbstractTestExtension {

    protected void saveToStore(ExtensionContext context,
                               Object key,
                               Object value) {
        getStore(context).put(key, value);
    }

    protected <T> T getFromStore(ExtensionContext context,
                                 Class<T> type,
                                 Object key) {
        return getStore(context).get(key, type);
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(getClass(), context.getRequiredTestMethod()));
    }
}
