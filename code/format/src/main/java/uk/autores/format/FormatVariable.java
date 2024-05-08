// Copyright 2024 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

/**
 * Represents indexed variable expression like <code>{1,number,currency}</code>.
 */
public final class FormatVariable extends FormatSegment {

    private final String raw;
    private final int index;
    private final FormatType type;
    private final FormatStyle style;
    private final String subformat;

    FormatVariable(String raw, int index, FormatType type, FormatStyle style, String subformat) {
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
     * {@link FormatType#NONE} if not specified in the expression.
     *
     * @return number, date, time, choice or (none)
     */
    public FormatType type() {
        return type;
    }

    /**
     * {@link FormatStyle#NONE} if not specified in the expression.
     * {@link FormatStyle#SUBFORMAT} if not a match for {@link FormatType#styles()}.
     *
     * @return format type specific style
     */
    public FormatStyle style() {
        return style;
    }

    /**
     * Populated when {@link FormatStyle#SUBFORMAT} is detected.
     *
     * @return sub-format pattern or empty string
     */
    public String subformat() {
        return subformat;
    }

    @Override
    public String raw() {
        return raw;
    }
}
