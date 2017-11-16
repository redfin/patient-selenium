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

import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

public interface PsElementLocator<W extends WebElement,
        C extends PsConfig<W, C, B, THIS, E>,
        B extends PsElementLocatorBuilder<W, C, B, THIS, E>,
        THIS extends PsElementLocator<W, C, B, THIS, E>,
        E extends PsElement<W, C, B, THIS, E>> {

    boolean isPresent();

    boolean isPresent(Duration timeout);

    boolean isNotPresent();

    boolean isNotPresent(Duration timeout);

    E get();

    E get(Duration timeout);

    E get(int index);

    E get(int index,
          Duration timeout);

    List<E> getAll();

    List<E> getAll(Duration timeout);
}
