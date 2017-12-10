package com.redfin.patient.selenium.internal;

public interface JavaScriptExecutor {

    Object script(String script, Object... args);

    Object asyncScript(String script, Object... args);
}
