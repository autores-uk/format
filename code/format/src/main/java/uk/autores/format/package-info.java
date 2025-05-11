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
 *   // parse to immutable formatter
 *   var expression = FormatExpression.parse("Hello, {0}!");
 *   // format
 *   var l = Locale.getDefault();
 *   String result = expression.format(l, "World");
 *   // output
 *   System.out.println(result);
 * </code></pre>
 * <p>
 *     All
 *     <a href="https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/text/MessageFormat.html" target="_blank">JDK23
 *     expressions</a> are supported under JDK8+ runtimes with the following exceptions.
 *     <ul>
 *         <li>Formatting compact "number" expressions like <code>{0,number,compact_short}</code> requires a JDK12+ runtime</li>
 *         <li>Formatting "list" expressions like <code>{0,list}</code> requires a JDK22+ runtime</li>
 *     </ul>
 * </p>
 * <p>
 *     See <a href="https://github.com/autores-uk/format" target="_blank">GitHub</a> for more information.
 * </p>
 */
package uk.autores.format;
