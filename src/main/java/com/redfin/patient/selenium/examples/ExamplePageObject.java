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

package com.redfin.patient.selenium.examples;

import com.redfin.patient.selenium.internal.AbstractPageObject;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;

public final class ExamplePageObject
        extends AbstractPageObject<WebDriver,
        WebElement,
        ExamplePsDriver,
        ExamplePsConfig,
        ExamplePsElementLocatorBuilder,
        ExamplePsElementLocator,
        ExamplePsElement> {

    @ExamplePageObjectFind(css = "div#rcnt")
    private ExamplePageSearchResults resultsPage;

    @ExamplePageObjectFind(css = "[name='q']")
    private ExamplePsElementLocator searchField;

    public ExamplePageSearchResults searchGoogle(String searchString) {
        getPatientDriver().get("https://www.google.com");
        searchField.get(Duration.ofSeconds(10))
                   .clear()
                   .sendKeys("hello, world", Keys.ENTER);
        return resultsPage;
    }

    public static final class ExamplePageSearchResults
            extends AbstractPageObject<WebDriver,
            WebElement,
            ExamplePsDriver,
            ExamplePsConfig,
            ExamplePsElementLocatorBuilder,
            ExamplePsElementLocator,
            ExamplePsElement> {

        @ExamplePageObjectFind(css = ".srg > .g", tryingForSeconds = 10)
        private ExamplePsElementLocator searchResultList;

        public int getNumberOfSearchResultsPerPage() {
            return searchResultList.getAll()
                                   .size();
        }

        public void clickNthResult(int index) {
            searchResultList.get(index)
                            .find()
                            .byCss(".r")
                            .get()
                            .click();
        }
    }
}
