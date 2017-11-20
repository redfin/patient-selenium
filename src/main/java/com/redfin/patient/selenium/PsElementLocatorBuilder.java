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

import com.redfin.patience.PatientWait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.function.Predicate;

public interface PsElementLocatorBuilder<W extends WebElement,
        C extends PsConfig<W, C, THIS, L, E>,
        THIS extends PsElementLocatorBuilder<W, C, THIS, L, E>,
        L extends PsElementLocator<W, C, THIS, L, E>,
        E extends PsElement<W, C, THIS, L, E>> {

    THIS withWait(PatientWait wait);

    THIS withTimeout(Duration timeout);

    THIS withAssertNotPresentTimeout(Duration timeout);

    THIS withFilter(Predicate<W> elementFilter);

    L by(By locator);
}
