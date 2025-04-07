// Copyright 2024-2025 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

/**
 * <p>
 *     Format styles supported by the various {@link FmtType}s.
 *     Not all expression types support all styles.
 * </p>
 * <p>
 *     <code>"{0,number,currency}"</code> is an example expression that results in {@link #CURRENCY} when parsed.
 * </p>
 *
 * @see FmtType#styles()
 */
public enum FmtStyle {
    /** Indicates that no style has been specified. */
    NONE("(none)"),
    /** Indicates a custom style expression has been provided. */
    SUBFORMAT("(subformat)"),
    /** Number "integer" style. */
    INTEGER("integer"),
    /** Number "currency" style. */
    CURRENCY("currency"),
    /** Number "percent" style. */
    PERCENT("percent"),
    /** Date/time formatters' "short" style. */
    SHORT("short"),
    /** Date/time formatters' "medium" style. */
    MEDIUM("medium"),
    /** Date/time formatters' "long" style. */
    LONG("long"),
    /** Date/time formatters' "full" style. */
    FULL("full"),
    /** List formatter "or" style. */
    OR("or"),
    /** List formatter "unit" style. */
    UNIT("unit");

    private final String style;

    FmtStyle(String style) {
        this.style = style;
    }

    /**
     * Typically, the style string from the {@link java.text.MessageFormat} table.
     *
     * @return string used in expressions, "(subformat)", or "(none)"
     */
    public String label() {
        return style;
    }
}
