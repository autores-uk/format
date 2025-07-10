[![Build](https://github.com/autores-uk/format/actions/workflows/ci.yaml/badge.svg)](https://github.com/autores-uk/format/actions/workflows/ci.yaml)
[![Document](https://github.com/autores-uk/format/actions/workflows/docs.yaml/badge.svg)](https://github.com/autores-uk/format/actions/workflows/docs.yaml)
[![javadoc](https://javadoc.io/badge2/uk.autores/format/javadoc.svg)](https://javadoc.io/doc/uk.autores/format)

# AutoRes Format

A better
[java.text.MessageFormat](https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/text/MessageFormat.html).

```java
// parse to immutable, thread-safe formatter
var expression = FormatExpression.parse("Hello, {0}!");
// format
String result = expression.format(Locale.getDefault(), "World");
// print "Hello, World!"
System.out.println(result);
```

## Features

 - Immutable, thread-safe parsed expressions
 - Supports JDK23 expressions at lower JDK versions
 - Exposes more parsed expression metadata

## Implementation Notes

 - `FormatExpression` uses the same
   [Format](https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/text/Format.html)
   types under the covers for numbers, lists and date/times as `MessageFormat` to ensure maximum compatibility with the runtime JDK
 - [Format](https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/text/Format.html)
   types attempt to solve two problems - format and parse;
   this library only formats
 - Parsing
   [ListFormat](https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/text/ListFormat.html)
   expressions like `{0,list}` is supported in JDK8+ but formatting such expressions requires a JDK22+ runtime

## Building

Source and target are JDK17.
Requires JDK23 to execute unit tests.

Use Maven wrapper scripts to build:

```shell
code/format/mvnw -f code/format/pom.xml clean install
```

Published artifacts can be found in the
[Maven Central Repository](https://central.sonatype.com/artifact/uk.autores/format).

## Branches & Tags

Development is done on the main branch.
Releases are tagged as `release/$version`.
