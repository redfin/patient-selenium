package com.redfin.patient.selenium.internal;

import org.openqa.selenium.WebDriver;

public interface DriverExecutor<D extends WebDriver>
         extends Executor<D> {

    void close();

    void quit();
}
