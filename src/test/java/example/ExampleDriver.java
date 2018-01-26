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

package example;

import com.redfin.patient.selenium.AbstractPsDriver;
import com.redfin.patient.selenium.DriverExecutor;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;

import java.net.URL;
import java.util.Set;

public class ExampleDriver
     extends AbstractPsDriver<WebDriver,
                              WebElement,
                              ExampleConfig,
                              ExampleDriver,
                              ExampleElementLocatorBuilder,
                              ExampleElementLocator,
                              ExampleElement> {

    private final TargetLocator targetLocator = new TargetLocator();
    private final Navigation navigation = new Navigation();
    private final Cookies cookies = new Cookies();
    private final Window window = new Window();
    private final Logs logs = new Logs();

    public ExampleDriver(String description,
                         ExampleConfig config,
                         DriverExecutor<WebDriver> driverExecutor) {
        super(description, config, driverExecutor);
    }

    private void log(String formatString, Object... args) {
        Object[] newArgs = new Object[args.length + 1];
        newArgs[0] = this;
        System.arraycopy(args, 0, newArgs, 1, args.length);
        getConfig().getLogConsumer()
                   .accept(String.format(formatString, newArgs));
    }

    @Override
    public ExampleElementLocatorBuilder find() {
        return new ExampleElementLocatorBuilder(String.format("%s.find()",
                                                              getDescription()),
                                                getConfig(),
                                                this,
                                                by -> withWrappedDriver().apply(d -> d.findElements(by)));
    }

    public ExampleDriver get(String url) {
        return navigate().to(url);
    }

    public ExampleDriver get(URL url) {
        return navigate().to(url);
    }

    public String getCurrentUrl() {
        return withWrappedDriver().apply(WebDriver::getCurrentUrl);
    }

    public String getTitle() {
        return withWrappedDriver().apply(WebDriver::getTitle);
    }

    public String getPageSource() {
        return withWrappedDriver().apply(WebDriver::getPageSource);
    }

    public Set<String> getWindowHandles() {
        return withWrappedDriver().apply(WebDriver::getWindowHandles);
    }

    public String getWindowHandle() {
        return withWrappedDriver().apply(WebDriver::getWindowHandle);
    }

    public void close() {
        log("%s.close()");
        super.close();
    }

    public void quit() {
        log("%s.quit()");
        super.quit();
    }

    public TargetLocator switchTo() {
        return targetLocator;
    }

    public class TargetLocator {

        public ExampleDriver frame(int index) {
            log("%s.switchTo().frame(%d)", index);
            withWrappedDriver().accept(d -> d.switchTo().frame(index));
            return ExampleDriver.this;
        }

        public ExampleDriver parentFrame() {
            log("%s.switchTo().parentFrame()");
            withWrappedDriver().accept(d -> d.switchTo().parentFrame());
            return ExampleDriver.this;
        }

        public ExampleDriver window(String nameOrHandle) {
            log("%s.switchTo().window(%s)", nameOrHandle);
            withWrappedDriver().accept(d -> d.switchTo().window(nameOrHandle));
            return ExampleDriver.this;
        }
    }

    public Navigation navigate() {
        return navigation;
    }

    public class Navigation {

        public ExampleDriver back() {
            log("%s.navigate().back()");
            withWrappedDriver().accept(d -> d.navigate().back());
            return ExampleDriver.this;
        }

        public ExampleDriver forward() {
            log("%s.navigate().forward()");
            withWrappedDriver().accept(d -> d.navigate().forward());
            return ExampleDriver.this;
        }

        public ExampleDriver to(String url) {
            log("%s.navigate().to(%s)", url);
            withWrappedDriver().accept(d -> d.navigate().to(url));
            return ExampleDriver.this;
        }

        public ExampleDriver to(URL url) {
            log("%s.navigate().to(%s)", url);
            withWrappedDriver().accept(d -> d.navigate().to(url));
            return ExampleDriver.this;
        }

        public ExampleDriver refresh() {
            log("%s.navigate().refresh()");
            withWrappedDriver().accept(d -> d.navigate().refresh());
            return ExampleDriver.this;
        }
    }

    public Cookies cookies() {
        return cookies;
    }

    public class Cookies {

        public ExampleDriver add(Cookie cookie) {
            log("%s.cookies().add(%s)", cookie);
            withWrappedDriver().accept(d -> d.manage().addCookie(cookie));
            return ExampleDriver.this;
        }

        public ExampleDriver delete(Cookie cookie) {
            log("%s.cookies().delete(%s)", cookie);
            withWrappedDriver().accept(d -> d.manage().deleteCookie(cookie));
            return ExampleDriver.this;
        }

        public ExampleDriver delete(String name) {
            log("%s.cookies().delete(%s)", name);
            withWrappedDriver().accept(d -> d.manage().deleteCookieNamed(name));
            return ExampleDriver.this;
        }

        public ExampleDriver deleteAll() {
            log("%s.cookies().deleteAll()");
            withWrappedDriver().accept(d -> d.manage().deleteAllCookies());
            return ExampleDriver.this;
        }

        public Cookie get(String name) {
            return withWrappedDriver().apply(d -> d.manage().getCookieNamed(name));
        }

        public Set<Cookie> getAll() {
            return withWrappedDriver().apply(d -> d.manage().getCookies());
        }
    }

    public Window window() {
        return window;
    }

    public class Window {

        public ExampleDriver setSize(Dimension targetSize) {
            log("%s.window().setSize(%s)", targetSize);
            withWrappedDriver().accept(d -> d.manage().window().setSize(targetSize));
            return ExampleDriver.this;
        }

        public ExampleDriver setPosition(Point targetPosition) {
            log("%s.window().setPosition(%s)", targetPosition);
            withWrappedDriver().accept(d -> d.manage().window().setPosition(targetPosition));
            return ExampleDriver.this;
        }

        public Dimension getSize() {
            return withWrappedDriver().apply(d -> d.manage().window().getSize());
        }

        public Point getPosition() {
            return withWrappedDriver().apply(d -> d.manage().window().getPosition());
        }

        public ExampleDriver maximize() {
            log("%s.window().maximize()");
            withWrappedDriver().accept(d -> d.manage().window().maximize());
            return ExampleDriver.this;
        }

        public ExampleDriver fullscreen() {
            log("%s.window().fullscreen()");
            withWrappedDriver().accept(d -> d.manage().window().fullscreen());
            return ExampleDriver.this;
        }
    }

    public Logs logs() {
        return logs;
    }

    public class Logs {

        public LogEntries get(String logType) {
            return withWrappedDriver().apply(d -> d.manage().logs().get(logType));
        }

        public Set<String> getAvailableLogTypes() {
            return withWrappedDriver().apply(d -> d.manage().logs().getAvailableLogTypes());
        }
    }
}
