// Copyright 2024-2025 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

import java.util.Locale;

/**
 * Base format expression type.
 * Implementations are equal if their {@link #toString()} values are equal.
 */
public sealed abstract class Formatter permits FormatVariable, FormatExpression, FormatLiteral {
    Formatter() {}

    /**
     * Formats the expression and appends it to buffer.
     *
     * @param l    the locale
     * @param buf  the target buffer
     * @param args array of arguments containing elements for any indices evaluated
     */
    public abstract void formatTo(Locale l, StringBuffer buf, Object... args);

    /**
     * Formats the expression.
     *
     * @param l    the locale
     * @param args array of arguments containing elements for any indices evaluated
     * @return the evaluated expression
     */
    public String format(Locale l, Object... args) {
        var buf = new StringBuffer();
        formatTo(l, buf, args);
        return buf.toString();
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof Formatter other) {
            return toString().equals(other.toString());
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return toString().hashCode();
    }

    /**
     * The unprocessed expression.
     *
     * @return raw string
     */
    @Override
    public abstract String toString();
}
