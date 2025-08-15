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
    /**
     * Indicates that no style has been specified.
     * <p>
     *     Example: <code>{0,number}</code>
     * </p>
     */
    NONE("(none)"),
    /**
     * Indicates a custom style expression has been provided.
     * <p>
     *     Example: <code>{0,number,#,##0.##}</code>
     * </p>
     */
    SUBFORMAT("(subformat)"),
    /**
     * Number "integer" style.
     * <p>
     *     Example: <code>{0,number,integer}</code>
     * </p>
     */
    INTEGER("integer"),
    /**
     * Number "currency" style.
     * <p>
     *     Example: <code>{0,number,currency}</code>
     * </p>
     */
    CURRENCY("currency"),
    /**
     * Number "percent" style.
     * <p>
     *     Example: <code>{0,number,percent}</code>
     * </p>
     */
    PERCENT("percent"),
    /**
     * Date/time formatters' "short" style.
     * <p>
     *     Example: <code>{0,dtf_datetime,short}</code>
     * </p>
     */
    SHORT("short"),
    /**
     * Date/time formatters' "medium" style.
     * <p>
     *     Example: <code>{0,dtf_datetime,medium}</code>
     * </p>
     */
    MEDIUM("medium"),
    /**
     * Date/time formatters' "long" style.
     * <p>
     *     Example: <code>{0,dtf_datetime,long}</code>
     * </p>
     */
    LONG("long"),
    /**
     * Date/time formatters' "full" style.
     * <p>
     *     Example: <code>{0,dtf_datetime,full}</code>
     * </p>
     */
    FULL("full"),
    /**
     * List formatter "or" style.
     * <p>
     *     Example: <code>{0,list,or}</code>
     * </p>
     */
    OR("or"),
    /**
     * List formatter "unit" style.
     * <p>
     *     Example: <code>{0,list,unit}</code>
     * </p>
     */
    UNIT("unit"),
    /**
     * Number "compact_short" style.
     * <p>
     *     Example: <code>{0,number,compact_short}</code>
     * </p>
     */
    COMPACT_SHORT("compact_short"),
    /**
     * Number "compact_long" style.
     * <p>
     *     Example: <code>{0,number,compact_long}</code>
     * </p>
     */
    COMPACT_LONG("compact_long");

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
