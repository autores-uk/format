// Copyright 2024-2025 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0

/**
 * <h2>AutoRes Format</h2>
 *
 * <p>
 *     An alternative to {@link java.text.MessageFormat}.
 * </p>
 * <p>
 *     Use
 *     {@link uk.autores.format.FormatExpression#parse(java.lang.CharSequence)}
 *     to parse expressions. Use
 *     {@link uk.autores.format.FormatExpression#format(java.util.Locale, java.lang.Object...)}
 *     to format strings.
 * </p>
 * <pre><code>
 *     // parse to immutable expression
 *     List&lt;FormatSegment&gt; expression = Formatting.parse("Hello, {0}!");
 *     // format
 *     Locale l = Locale.getDefault();
 *     String result = Formatting.format(expression, l, "World");
 * </code></pre>
 * <p>
 *     All
 *     <a href="https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/text/MessageFormat.html">JDK23
 *     expressions</a> are supported under JDK8+ runtimes
 *     except formatting "list" expressions which requires a JDK22+ runtime.
 * </p>
 */
package uk.autores.format;
