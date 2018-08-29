[![Build Status](https://travis-ci.org/redfin/patient-selenium.svg?branch=master)](https://travis-ci.org/redfin/patient-selenium)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

# Patient-Selenium

## Overview

Patient-Selenium is designed to be a wrapper around [Selenium](https://github.com/SeleniumHQ/selenium) which allows you to automate user interactions with a web browser.
It has a fluent API and has built in support for waiting for elements to be in a usable state before interacting with them.
It also makes a best effort at retrying executing commands if a StaleElementReferenceException is caught.
These features together make it useful when trying to write automated tests for a site that heavily use asynchronous JavaScript.
The library has a minimal set of methods exposed and is intended to be sub-classed by users.
This allows the end users to expose which methods make sense for the type of browser and/or tests they are writing while still gaining the previously mentioned benefits.

## Installation
Via maven:
```xml
<dependency>
    <groupId>com.redfin</groupId>
    <artifactId>patient-selenium</artifactId>
    <version>1.5.0</version>
</dependency>
```

## Patient Elements

Global default state is created by creating a sub class of the `AbstractPsConfig` type.
This includes such things as a default timeout and a default filter for designating if an element is a match in
 addition to the By locators that Selenium uses, this can include such things as only returning visible elements,
 only enabled elements, etc.
A `AbstractPsDriver` is the starting point of use and allows for lazily initializing the actual web driver used to
 control the web browser.
This allows it to be created during test setup without actually paying the cost if the test is unable to create needed
 data as well as not holding on to a possibly limited resource longer than necessary (if running against a Selenium Grid
 or with a cloud service like SauceLabs or BrowserStack).

```java
driver.find().by(By.id("foo")).get(Duration.ofSeconds(20)).withWrappedElement(WebElement::click);
```

In the above example, the driver would look for an element that has an id of `foo`.
It will attempt to find it for up to 20 seconds (with retries occurring as setup by the default
`PatientWait` object in the config ... see the [Patience](https://github.com/redfin/patience) library).
If a matching element is found within the timeout it will send it the `click` command.
If not, it will throw a NoSuchElementException.

## Page Objects

The library also defines a set of types for use as page objects.
They are fairly generic so that the end user has the ability to implement the locators for their page
 objects with whatever types of annotations they wish.
This also includes the idea of a `Widget` which is a special type of page object which also represents
 a web element.
Widgets are useful for creating small abstractions that are re-used across many pages.
For example, a contact form that might show up on multiple pages of a web site.

## Example end user implementation

There are example classes for the implementation of the library in the examples package of the tests directory.
Looking through these will give you a better idea of what can be done with the page objects.
