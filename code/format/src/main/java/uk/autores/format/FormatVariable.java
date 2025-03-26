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
     * {@link FmtType#NONE} if not specified in the expression.
     *
     * @return number, date, time, choice or (none)
     */
    public FmtType type() {
        return type;
    }

    /**
     * {@link FmtStyle#NONE} if not specified in the expression.
     * {@link FmtStyle#SUBFORMAT} if not a match for {@link FmtType#styles()}.
     *
     * @return format type specific style
     */
    public FmtStyle style() {
        return style;
    }

    /**
     * Populated when {@link FmtStyle#SUBFORMAT} is detected.
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
