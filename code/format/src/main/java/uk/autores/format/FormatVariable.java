// Copyright 2024-2025 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

import java.util.Locale;
import java.util.stream.IntStream;

/**
 * Represents indexed variable expression like <code>{1,number,currency}</code>.
 */
public final class FormatVariable extends Formatter {
    private static final FormatVariable[] INTERNED = IntStream.range(0, 5)
            .mapToObj(FormatVariable::intern)
            .toArray(FormatVariable[]::new);

    private final String raw;
    private final int index;
    private final FmtType type;
    private final FmtStyle style;
    private final String subformat;

    private FormatVariable(String raw, int index, FmtType type, FmtStyle style, String subformat) {
        this.raw = raw;
        this.index = index;
        this.type = type;
        this.style = style;
        this.subformat = subformat;
    }

    private static FormatVariable intern(int index) {
        return new FormatVariable("{" + index + "}", index, FmtType.NONE, FmtStyle.NONE, "");
    }

    static FormatVariable from(String raw, int index, FmtType type, FmtStyle style, String subformat) {
        if (type == FmtType.NONE && index < INTERNED.length) {
            return INTERNED[index];
        }
        return new FormatVariable(raw, index, type, style, subformat);
    }

    /**
     * Ordinal of the argument used to populate this variable.
     *
     * @return zero-based index
     */
    public int index() {
        return index;
    }

    /**
     * The format type like "number", "dtf_datetime", etc.
     * {@link FmtType#NONE} if no type specified in expression.
     *
     * @return format type
     */
    public FmtType type() {
        return type;
    }

    /**
     * Format style like "integer", "currency", etc.
     * {@link FmtStyle#NONE} if not specified in the expression.
     * {@link FmtStyle#SUBFORMAT} if not a match for {@link FmtType#styles()}.
     *
     * @return format type specific style
     */
    public FmtStyle style() {
        return style;
    }

    /**
     * A custom style expression.
     * Populated when {@link FmtStyle#SUBFORMAT} is returned by {@link #style()}.
     *
     * @return sub-format pattern or empty string
     */
    public String subformat() {
        return subformat;
    }

    /**
     * <p>
     *     Formats the variable and appends it to the buffer.
     * </p>
     * <p>
     *     The argument array must contain a suitable element at {@link #index()}.
     * </p>
     *
     * @param l    the locale
     * @param buf  the target buffer
     * @param args array of arguments containing elements for any indices evaluated
     */
    @Override
    public void formatTo(Locale l, StringBuffer buf,  Object... args) {
        type.formatter().format(l, this, buf, args);
    }

    @Override
    public String toString() {
        return raw;
    }
}
