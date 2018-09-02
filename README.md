[![Build Status](https://travis-ci.org/redfin/patient-selenium.svg?branch=master)](https://travis-ci.org/redfin/patient-selenium)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

# Patient-Selenium

## Overview

[Selenium](https://github.com/SeleniumHQ/selenium) is a library which allows you to automate user interactions with a web browser.
Patient-Selenium is a wrapper built around Selenium which allows for lazily located elements, waiting for elements to be in a desired state,
customizable automatic retries  of located elements, and customizable page object definition.
These features together make it useful when trying to write automated tests for a site that heavily use asynchronous JavaScript.
It is intended to be used by a single thread (the same one that is driving the implementing Selenium web driver) and the classes are not
intended to be shared across multiple threads.
The library has a minimal set of methods exposed and is intended to be sub-classed by users.
This allows the end users to expose which methods make sense for the type of browser and/or tests they are writing while still gaining the previously mentioned benefits.
See the [Patience](https://github.com/redfin/patience) library for more details regarding the specifics of how waiting occurs.

## Installation
Via maven:
```xml
<dependency>
    <groupId>com.redfin</groupId>
    <artifactId>patient-selenium</artifactId>
    <version>2.0.2-RC1</version>
</dependency>
```

## Element

The `AbstractElement` type is the wrapper of a Selenium `WebElement`. It has a cached reference to a Selenium
element to allow for fewer network requests due to element location which is useful in the case of a networked
Selenium grid architecture. Multiple actions on the same `AbstractElement` will continue to use the same web element
reference object. In the case of an exception being thrown by the web element, the cache will be cleared, the Selenium
web element will be relocated, and the action attempted again (up to a customizable number of times). Interacting with
the selenium element is done via two methods `accept(Consumer)` and `apply(Function)`, sub classes that want to
expose additional methods can simply call into those methods directly and the cache manipulation will be done for them.

If the actual backing Selenium `WebElement` needs to be looked up and it can't find a matching one,
a `NoSuchElementException` will be thrown. There are also two methods on the `AbstractElement` type that will not
check the cache but trigger another Selenium element location call. These are `isPresent` and `isAbsent(Duration)`
which will return true or false depending on the page state and will never cause a `NoSuchElementException`.

```java
if (element.isPresent()) {
    element.accept(WebElement::click);
}
```

## ElementFactory

The `AbstractElementFactory` type is the base class for a type used on page objects and whose job is to
create concrete `AbstractElement` instances. It allows for customization of the `PatientWait` and `Duration` timeout
to be used by the `AbstractElement` instance when it is locating Selenium `WebElement` objects. The `atIndex(int)`
method creates individual element instances that are lazily initialized (e.g. the actual Selenium element hasn't
been looked up yet). The `getAll()` method returns a list of element instances that are eagerly initialized (e.g. the
actual Selenium element list has been located). Note that for an individual element, it will know the particular index
on the page it is for. So, if there are 4 `<div>` tags on the page, and you have an `AbstractElement` instance that has
the index of `1` attached to it, it will correspond to the second matching Selenium element that is found (0 based
indexing). If there is a filter that is applied to located elements to further refine if they are matches in addition
to the base Selenium element location (e.g. you only want elements that return true for `WebElement::isDisplayed`, etc)
then the located elements will only keep applying the filter until the desired n-th matching element is found. This
also reduces network calls in a remote web driver situation if the Selenium `By` locator used returns a lot of matches
and you only need the first one that `isDisplayed()`.

```java
elementFactory.atIndex(1).accept(WebElement::click);
elementFactory.getAll().size();
```

## PageObjectInitializer

The `AbstractPageObjectInitializer` type is the base class for an instance that will be used to initialize fields
of an already created instance of a specific type. It is intended to be used to create concrete `AbstractElementFactory`
instances on a page object based upon custom annotations on the fields, but that is entirely customizable. There are two
types that it interacts with (both defined by an interface). Any field in the root, or in the ecursively initialized tree,
that is a `PageObject` will be included in the initialization. Any field that is of the type `WidgetPageObject` will be
both considered a page object and initialized, but will have the desired element factory given to the set widget object
method as well. If a field is of those two page object types and is null, however, it will be ignored.
