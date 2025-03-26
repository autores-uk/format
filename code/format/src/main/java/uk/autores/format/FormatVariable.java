// Copyright 2024 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

import java.util.Locale;

/**
 * Represents indexed variable expression like <code>{1,number,currency}</code>.
 */
public final class FormatVariable extends FormatSegment {

    private final String raw;
    private final int index;
    private final FmtType type;
    private final FmtStyle style;
    private final String subformat;

    FormatVariable(String raw, int index, FmtType type, FmtStyle style, String subformat) {
        this.raw = raw;
        this.index = index;
        this.type = type;
        this.style = style;
        this.subformat = subformat;
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

    @Override
    String raw() {
        return raw;
    }

    @Override
    void formatTo(Locale l, StringBuffer buf,  Object... args) {
        type.formatter().format(l, this, buf, args);
    }
}
