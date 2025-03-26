// Copyright 2024 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

import java.util.Locale;

/**
 * Base type for {@link FormatVariable} and {@link FormatLiteral}.
 */
public abstract class FormatSegment {
    FormatSegment() {}

    /**
     * String segment from the source expression.
     *
     * @return original string
     */
    abstract String raw();

    /**
     * Formats the expression and appends it to buffer.
     *
     * @param l    the locale
     * @param buf  the target buffer
     * @param args array of arguments containing elements for any indices evaluated
     */
    abstract void formatTo(Locale l, StringBuffer buf, Object... args);

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof FormatSegment) {
            FormatSegment other = (FormatSegment) obj;
            return raw().equals(other.raw());
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return raw().hashCode();
    }

    @Override
    public String toString() {
        return raw();
    }
}
