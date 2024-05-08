// Copyright 2024 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

/**
 * Format styles supported by the various {@link FormatType}s.
 *
 * @see FormatType#styles()
 */
public enum FormatStyle {
    NONE("(none)"),
    SUBFORMAT("(subformat)"),
    INTEGER("integer"),
    CURRENCY("currency"),
    PERCENT("percent"),
    SHORT("short"),
    MEDIUM("medium"),
    LONG("long"),
    FULL("full");

    private final String style;

    FormatStyle(String style) {
        this.style = style;
    }

    public String label() {
        return style;
    }
}
