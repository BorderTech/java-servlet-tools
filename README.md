# Servlet Tools

## Status

[![Build Status](https://github.com/BorderTech/java-servlet-tools/actions/workflows/github-actions-build.yml/badge.svg)](https://github.com/BorderTech/java-servlet-tools/actions/workflows/github-actions-build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=BorderTech_java-servlet-tools&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=BorderTech_java-servlet-tools)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=BorderTech_java-servlet-tools&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=BorderTech_java-servlet-tools)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=BorderTech_java-servlet-tools&metric=coverage)](https://sonarcloud.io/summary/new_code?id=BorderTech_java-servlet-tools)

## Content

- [What is Servlet Tools](#what-is-servlet-tools)
- [Why use Servlet Tools](#why-use-servlet-tools)
- [Getting started](#getting-started)
- [Contributing](#contributing)

## What is Servlet Tools

Servlet Tools provides helper filters and listeners to make it easier to use Servlet annotations.

## Why use Servlet Tools

A project may have existing Servlet filters/listeners or 3rd party filters/listeners that are not annotated.

If the project is not using a web.xml and relying on Servlet annotations, the Servlet Tools combo filter/listener makes it easier for projects to combine these filters/listeners into a single class that can be annotated.

Without Servlet Tools the project would need to (1) define each filter/listener in a web.xml or (2) extend the existing filters/listeners and annotate the new classes.

## Getting started

Add dependency:

``` xml
<project>
  ....
  <dependency>
    <groupId>com.github.bordertech.taskmaster</groupId>
    <artifactId>java-servlet-tools</artifactId>
    <version>1.0.0</version>
  </dependency>
  ....
</project>
```

### Combo Filter

How to create a combo filter that combines multiple servlet filters into one annotated class:
- Create a new class that extends [AbstractComboFilter](https://github.com/BorderTech/java-servlet-tools/blob/main/src/main/java/com/github/bordertech/taskmaster/servlet/combo/AbstractComboFilter.java)
- In the constructor, define the filter classes to be combined
- Annotate the new class with @WebFilter

``` java
  @WebFilter
  public class MyFilter extends AbstractComboFilter {

    public MyFilter() {
      super(new ExistingFilter(), new AnotherFilter());
    }

  }
```

### Combo Listener

How to create a combo listener that combines multiple servlet listeners into one annotated class:
- Create a new class that extends [AbstractComboServletListener](https://github.com/BorderTech/java-servlet-tools/blob/main/src/main/java/com/github/bordertech/taskmaster/servlet/combo/AbstractComboServletListener.java)
- In the constructor, define the listener classes to be combined
- Annotate the new class with @WebListener

``` java
  @WebListener
  public class MyListener extends AbstractComboServletListener {

    public MyListener() {
      super(new ExistingListener(), new AnotherListener());
    }

  }
```

## Contributing

Refer to these guidelines for [Workflow](https://github.com/BorderTech/java-common/wiki/Workflow) and [Releasing](https://github.com/BorderTech/java-common/wiki/Releasing).
