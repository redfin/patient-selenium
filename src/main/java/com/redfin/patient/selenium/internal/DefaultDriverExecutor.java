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

import org.openqa.selenium.WebDriver;

import java.util.function.Supplier;

public final class DefaultDriverExecutor<D extends WebDriver>
           extends AbstractExecutor<D>
        implements DriverExecutor<D> {

    public DefaultDriverExecutor(Supplier<D> driverSupplier) {
        super(driverSupplier);
    }

    @Override
    public void quit() {
        if (null != object) {
            try {
                object.quit();
            } finally {
                object = null;
            }
        }
    }

    @Override
    public void close() {
        if (null != object) {
            int numHandles = 0;
            try {
                numHandles = object.getWindowHandles()
                                   .size();
                object.close();
            } finally {
                if (numHandles <= 1) {
                    object = null;
                }
            }
        }
    }
}
