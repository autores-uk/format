[![Build](https://github.com/autores-uk/format/actions/workflows/ci.yaml/badge.svg)](https://github.com/autores-uk/format/actions/workflows/ci.yaml)
[![Document](https://github.com/autores-uk/format/actions/workflows/docs.yaml/badge.svg)](https://github.com/autores-uk/format/actions/workflows/docs.yaml)
[![javadoc](https://javadoc.io/badge2/uk.autores/format/javadoc.svg)](https://javadoc.io/doc/uk.autores/format)

# AutoRes Format

An alternative implementation of `java.text.MessageFormat`.
This code is not intended for direct use.
It has been written for use by `uk.autores:annotations`.

Published artifacts can be found in the
[Maven Central Repository](https://central.sonatype.com/artifact/uk.autores/format).

## Building

Source and target are JDK8.
Requires JDK23 to execute unit tests.

Use Maven wrapper scripts to build:

```shell
code/format/mvnw -f code/format/pom.xml clean install
```
