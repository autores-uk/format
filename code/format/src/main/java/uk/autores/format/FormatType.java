// Copyright 2024 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.EnumSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * The format types supported by {@link java.text.MessageFormat}.
 */
public enum FormatType {
    NONE("(none)", String.class, FormatStyle.NONE),
    NUMBER("number", Number.class, FormatStyle.NONE, FormatStyle.INTEGER, FormatStyle.CURRENCY, FormatStyle.PERCENT, FormatStyle.SUBFORMAT),
    DATE("date", TemporalAccessor.class, FormatStyle.NONE, FormatStyle.SHORT, FormatStyle.MEDIUM, FormatStyle.LONG, FormatStyle.FULL, FormatStyle.SUBFORMAT),
    TIME("time", TemporalAccessor.class, FormatStyle.NONE, FormatStyle.SHORT, FormatStyle.MEDIUM, FormatStyle.LONG, FormatStyle.FULL, FormatStyle.SUBFORMAT),
    CHOICE("choice", Number.class, FormatStyle.SUBFORMAT),
    DTF_DATE("dtf_date", TemporalAccessor.class, FormatStyle.NONE, FormatStyle.SHORT, FormatStyle.MEDIUM, FormatStyle.LONG, FormatStyle.FULL, FormatStyle.SUBFORMAT),
    DTF_TIME("dtf_time", TemporalAccessor.class, FormatStyle.NONE, FormatStyle.SHORT, FormatStyle.MEDIUM, FormatStyle.LONG, FormatStyle.FULL, FormatStyle.SUBFORMAT),
    DTF_DATETIME("dtf_datetime", TemporalAccessor.class, FormatStyle.NONE, FormatStyle.SHORT, FormatStyle.MEDIUM, FormatStyle.LONG, FormatStyle.FULL, FormatStyle.SUBFORMAT),
    BASIC_ISO_DATE("BASIC_ISO_DATE", TemporalAccessor.class, FormatStyle.NONE),
    ISO_LOCAL_DATE("ISO_LOCAL_DATE", TemporalAccessor.class, FormatStyle.NONE),
    ISO_OFFSET_DATE("ISO_OFFSET_DATE", TemporalAccessor.class, FormatStyle.NONE),
    ISO_DATE("ISO_DATE", TemporalAccessor.class, FormatStyle.NONE),
    ISO_LOCAL_TIME("ISO_LOCAL_TIME", TemporalAccessor.class, FormatStyle.NONE),
    ISO_OFFSET_TIME("ISO_OFFSET_TIME", TemporalAccessor.class, FormatStyle.NONE),
    ISO_TIME("ISO_TIME", TemporalAccessor.class, FormatStyle.NONE),
    ISO_LOCAL_DATE_TIME("ISO_LOCAL_DATE_TIME", TemporalAccessor.class, FormatStyle.NONE),
    ISO_OFFSET_DATE_TIME("ISO_OFFSET_DATE_TIME", TemporalAccessor.class, FormatStyle.NONE),
    ISO_ZONED_DATE_TIME("ISO_ZONED_DATE_TIME", TemporalAccessor.class, FormatStyle.NONE),
    ISO_DATE_TIME("ISO_DATE_TIME", TemporalAccessor.class, FormatStyle.NONE),
    ISO_ORDINAL_DATE("ISO_ORDINAL_DATE", TemporalAccessor.class, FormatStyle.NONE),
    ISO_WEEK_DATE("ISO_WEEK_DATE", TemporalAccessor.class, FormatStyle.NONE),
    ISO_INSTANT("ISO_INSTANT", TemporalAccessor.class, FormatStyle.NONE),
    RFC_1123_DATE_TIME("RFC_1123_DATE_TIME", TemporalAccessor.class, FormatStyle.NONE);

    private final String type;
    private final Class<?> argType;
    private final Set<FormatStyle> styles;

    FormatType(String type, Class<?> argType, FormatStyle... styles) {
        this.type = type;
        this.argType = argType;
        this.styles = EnumSet.copyOf(asList(styles));
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
