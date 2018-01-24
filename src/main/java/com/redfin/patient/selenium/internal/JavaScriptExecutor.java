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

package com.redfin.patient.selenium.internal;

/**
 * An interface defining execution of JavaScript.
 *
 * @see org.openqa.selenium.JavascriptExecutor
 */
public interface JavaScriptExecutor {

    /**
     * Same as the Selenium JavaScript execution except it will handle
     * "unwrapping" the patient-selenium elements if given as an arguments.
     *
     * @see org.openqa.selenium.JavascriptExecutor#executeScript(String, Object...)
     */
    Object script(String script, Object... args);

    /**
     * Same as the Selenium async JavaScript execution except it will handle
     * "unwrapping" the patient-selenium elements if given as an arguments.
     *
     * @see org.openqa.selenium.JavascriptExecutor#executeAsyncScript(String, Object...)
     */
    Object asyncScript(String script, Object... args);
}
