/**
 * <h2>AutoRes Format</h2>
 *
 * <p>
 *     An alternative to {@link java.text.MessageFormat}.
 * </p>
 * <p>
 *     Use
 *     {@link uk.autores.format.Formatting#parse(java.lang.CharSequence)}
 *     to parse expressions.
 *     Use
 *     {@link uk.autores.format.Formatting#format(java.util.List, java.util.Locale, java.lang.Object...)}
 *     to format strings.
 * </p>
 * <pre><code>
 *     // parse to immutable expression
 *     List&lt;FormatSegment&gt; expression = Formatting.parse("Hello, {0}!");
 *     // format
 *     Locale l = Locale.getDefault();
 *     String result = Formatting.format(expression, l, "World");
 * </code></pre>
 */
package uk.autores.format;
