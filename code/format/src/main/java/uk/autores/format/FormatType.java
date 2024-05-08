// Copyright 2024 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * The format types (none), number, date, time and choice.
 */
public enum FormatType {
    NONE("(none)", String.class, FormatStyle.NONE),
    NUMBER("number", Number.class, FormatStyle.NONE, FormatStyle.INTEGER, FormatStyle.CURRENCY, FormatStyle.PERCENT, FormatStyle.SUBFORMAT),
    DATE("date", ZonedDateTime.class, FormatStyle.NONE, FormatStyle.SHORT, FormatStyle.MEDIUM, FormatStyle.LONG, FormatStyle.FULL, FormatStyle.SUBFORMAT),
    TIME("time", ZonedDateTime.class, FormatStyle.NONE, FormatStyle.SHORT, FormatStyle.MEDIUM, FormatStyle.LONG, FormatStyle.FULL, FormatStyle.SUBFORMAT),
    CHOICE("choice", Number.class, FormatStyle.SUBFORMAT);

    private final String type;
    private final Class<?> argType;
    private final Set<FormatStyle> styles;

    FormatType(String type, Class<?> argType, FormatStyle... styles) {
        this.type = type;
        this.argType = argType;
        this.styles = EnumSet.copyOf(Arrays.asList(styles));
    }

    /**
     * Type string from the {@link java.text.MessageFormat} table.
     *
     * @return "(none)", "number", "date", "time", or "choice"
     */
    public String label() {
        return type;
    }

    /**
     * <ul>
     *     <li>{@link #NONE}: {@link String}</li>
     *     <li>{@link #NUMBER}, {@link #CHOICE}: {@link Number}</li>
     *     <li>{@link #DATE}, {@link #TIME}: {@link ZonedDateTime}</li>
     * </ul>
     *
     * @return the supported type
     */
    public Class<?> argType() {
        return argType;
    }

    /**
     * Supported styles.
     *
     * @return immutable set
     */
    public Set<FormatStyle> styles() {
        return EnumSet.copyOf(styles);
    }
}
