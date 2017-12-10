package com.redfin.patient.selenium;

import com.redfin.patient.selenium.internal.AbstractPsDriver;
import com.redfin.patient.selenium.internal.CachingExecutor;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;

import java.net.URL;
import java.util.Set;

public class PatientWebDriver
        extends AbstractPsDriver<WebDriver,
        WebElement,
        PatientWebConfig,
        PatientWebDriver,
        PatientWebElementLocatorBuilder,
        PatientWebElementLocator,
        PatientWebElement> {

    private final TargetLocator targetLocator = new TargetLocator();
    private final Navigation navigation = new Navigation();
    private final Cookies cookies = new Cookies();
    private final Window window = new Window();
    private final Logs logs = new Logs();

    public PatientWebDriver(String description,
                            PatientWebConfig config,
                            CachingExecutor<WebDriver> driverExecutor) {
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
    public PatientWebElementLocatorBuilder find() {
        return new PatientWebElementLocatorBuilder(String.format("%s.find()",
                                                                 getDescription()),
                                                   getConfig(),
                                                   this,
                                                   by -> withWrappedDriver().apply(d -> d.findElements(by)));
    }

    public PatientWebDriver get(String url) {
        return navigate().to(url);
    }

    public PatientWebDriver get(URL url) {
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

        public PatientWebDriver frame(int index) {
            log("%s.switchTo().frame(%d)", index);
            withWrappedDriver().accept(d -> d.switchTo().frame(index));
            return PatientWebDriver.this;
        }

        public PatientWebDriver parentFrame() {
            log("%s.switchTo().parentFrame()");
            withWrappedDriver().accept(d -> d.switchTo().parentFrame());
            return PatientWebDriver.this;
        }

        public PatientWebDriver window(String nameOrHandle) {
            log("%s.switchTo().window(%s)", nameOrHandle);
            withWrappedDriver().accept(d -> d.switchTo().window(nameOrHandle));
            return PatientWebDriver.this;
        }
    }

    public Navigation navigate() {
        return navigation;
    }

    public class Navigation {

        public PatientWebDriver back() {
            log("%s.navigate().back()");
            withWrappedDriver().accept(d -> d.navigate().back());
            return PatientWebDriver.this;
        }

        public PatientWebDriver forward() {
            log("%s.navigate().forward()");
            withWrappedDriver().accept(d -> d.navigate().forward());
            return PatientWebDriver.this;
        }

        public PatientWebDriver to(String url) {
            log("%s.navigate().to(%s)", url);
            withWrappedDriver().accept(d -> d.navigate().to(url));
            return PatientWebDriver.this;
        }

        public PatientWebDriver to(URL url) {
            log("%s.navigate().to(%s)", url);
            withWrappedDriver().accept(d -> d.navigate().to(url));
            return PatientWebDriver.this;
        }

        public PatientWebDriver refresh() {
            log("%s.navigate().refresh()");
            withWrappedDriver().accept(d -> d.navigate().refresh());
            return PatientWebDriver.this;
        }
    }

    public Cookies cookies() {
        return cookies;
    }

    public class Cookies {

        public PatientWebDriver add(Cookie cookie) {
            log("%s.cookies().add(%s)", cookie);
            withWrappedDriver().accept(d -> d.manage().addCookie(cookie));
            return PatientWebDriver.this;
        }

        public PatientWebDriver delete(Cookie cookie) {
            log("%s.cookies().delete(%s)", cookie);
            withWrappedDriver().accept(d -> d.manage().deleteCookie(cookie));
            return PatientWebDriver.this;
        }

        public PatientWebDriver delete(String name) {
            log("%s.cookies().delete(%s)", name);
            withWrappedDriver().accept(d -> d.manage().deleteCookieNamed(name));
            return PatientWebDriver.this;
        }

        public PatientWebDriver deleteAll() {
            log("%s.cookies().deleteAll()");
            withWrappedDriver().accept(d -> d.manage().deleteAllCookies());
            return PatientWebDriver.this;
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

        public PatientWebDriver setSize(Dimension targetSize) {
            log("%s.window().setSize(%s)", targetSize);
            withWrappedDriver().accept(d -> d.manage().window().setSize(targetSize));
            return PatientWebDriver.this;
        }

        public PatientWebDriver setPosition(Point targetPosition) {
            log("%s.window().setPosition(%s)", targetPosition);
            withWrappedDriver().accept(d -> d.manage().window().setPosition(targetPosition));
            return PatientWebDriver.this;
        }

        public Dimension getSize() {
            return withWrappedDriver().apply(d -> d.manage().window().getSize());
        }

        public Point getPosition() {
            return withWrappedDriver().apply(d -> d.manage().window().getPosition());
        }

        public PatientWebDriver maximize() {
            log("%s.window().maximize()");
            withWrappedDriver().accept(d -> d.manage().window().maximize());
            return PatientWebDriver.this;
        }

        public PatientWebDriver fullscreen() {
            log("%s.window().fullscreen()");
            withWrappedDriver().accept(d -> d.manage().window().fullscreen());
            return PatientWebDriver.this;
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
