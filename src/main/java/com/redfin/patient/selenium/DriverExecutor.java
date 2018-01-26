/*
 * Copyright: (c) 2017 Redfin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redfin.patient.selenium;

import org.openqa.selenium.WebDriver;

/**
 * An extension of the {@link Executor} interface meant to be used
 * for wrapping {@link WebDriver} types.
 *
 * @param <D> the type of {@link WebDriver} to be wrapped.
 */
public interface DriverExecutor<D extends WebDriver>
         extends Executor<D> {

    /**
     * Call {@link WebDriver#close()} on the contained
     * {@link WebDriver} instance. No effect if there is no
     * initialized driver.
     */
    void close();

    /**
     * Call {@link WebDriver#quit()} on the contained
     * {@link WebDriver} instance. No effect if there is no
     * initialized driver.
     */
    void quit();
}
