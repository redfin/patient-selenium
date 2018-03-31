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

/**
 * An interface defining execution of JavaScript.
 *
 * @see org.openqa.selenium.JavascriptExecutor
 */
public interface JavaScriptExecutor {

    /**
     * Same as the Selenium JavaScript execution except it will handle
     * "unwrapping" the patient-selenium elements if given as an arguments.
     * This version does not return a value. If you have a "return" statement
     * at the end of your JavaScript, use the method {@link #scriptWithResult(String, Object...)}.
     *
     * @param script the String JavaScript to be executed.
     *               May not be null or empty.
     * @param args   the Array of Object arguments to be used in the script.
     *               May be null or empty.
     *
     * @throws IllegalArgumentException if script is null or empty.
     * @see org.openqa.selenium.JavascriptExecutor#executeScript(String, Object...)
     */
    void script(String script, Object... args);

    /**
     * Same as the Selenium async JavaScript execution except it will handle
     * "unwrapping" the patient-selenium elements if given as an arguments.
     * This version does not return a value. If you have a "return" statement
     * at the end of your JavaScript, use the method {@link #scriptWithResult(String, Object...)}.
     *
     * @param script the String JavaScript to be executed.
     *               May not be null or empty.
     * @param args   the Array of Object arguments to be used in the script.
     *               May be null or empty.
     *
     * @throws IllegalArgumentException if script is null or empty.
     * @see org.openqa.selenium.JavascriptExecutor#executeAsyncScript(String, Object...)
     */
    void asyncScript(String script, Object... args);

    /**
     * Same as the Selenium JavaScript execution except it will handle
     * "unwrapping" the patient-selenium elements if given as an arguments.
     *
     * @param script the String JavaScript to be executed.
     *               May not be null or empty.
     * @param args   the Array of Object arguments to be used in the script.
     *               May be null or empty.
     *
     * @return One of Boolean, Long, Double, String, List, Map or WebElement. Or null.
     *
     * @throws IllegalArgumentException if script is null or empty.
     * @see org.openqa.selenium.JavascriptExecutor#executeScript(String, Object...)
     */
    Object scriptWithResult(String script, Object... args);

    /**
     * Same as the Selenium async JavaScript execution except it will handle
     * "unwrapping" the patient-selenium elements if given as an arguments.
     *
     * @param script the String JavaScript to be executed.
     *               May not be null or empty.
     * @param args   the Array of Object arguments to be used in the script.
     *               May be null or empty.
     *
     * @return One of Boolean, Long, Double, String, List, Map or WebElement. Or null.
     *
     * @throws IllegalArgumentException if script is null or empty.
     * @see org.openqa.selenium.JavascriptExecutor#executeAsyncScript(String, Object...)
     */
    Object asyncScriptWithResult(String script, Object... args);
}
