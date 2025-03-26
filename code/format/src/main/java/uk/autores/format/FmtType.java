// Copyright 2024 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.EnumSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * The format types supported by {@link java.text.MessageFormat}.
 */
public enum FmtType {
    /** Indicates that no style has been specified. */
    NONE(AnyObject::format, "(none)", String.class, FmtStyle.NONE),
    /** {@see java.text.NumberFormat} */
    NUMBER(Numbers::format, "number", Number.class, FmtStyle.NONE, FmtStyle.INTEGER, FmtStyle.CURRENCY, FmtStyle.PERCENT, FmtStyle.SUBFORMAT),
    /** {@see java.text.DateFormat} */
    DATE(Temporals::date, "date", TemporalAccessor.class, FmtStyle.NONE, FmtStyle.SHORT, FmtStyle.MEDIUM, FmtStyle.LONG, FmtStyle.FULL, FmtStyle.SUBFORMAT),
    /** {@see java.text.DateFormat} */
    TIME(Temporals::time, "time", TemporalAccessor.class, FmtStyle.NONE, FmtStyle.SHORT, FmtStyle.MEDIUM, FmtStyle.LONG, FmtStyle.FULL, FmtStyle.SUBFORMAT),
    /** {@see java.text.ChoiceFormat} */
    CHOICE(Choices::format, "choice", Number.class, FmtStyle.SUBFORMAT),
    /** {@see java.time.format.DateTimeFormatter} */
    DTF_DATE(Temporals::date, "dtf_date", TemporalAccessor.class, FmtStyle.NONE, FmtStyle.SHORT, FmtStyle.MEDIUM, FmtStyle.LONG, FmtStyle.FULL, FmtStyle.SUBFORMAT),
    /** {@see java.time.format.DateTimeFormatter} */
    DTF_TIME(Temporals::time, "dtf_time", TemporalAccessor.class, FmtStyle.NONE, FmtStyle.SHORT, FmtStyle.MEDIUM, FmtStyle.LONG, FmtStyle.FULL, FmtStyle.SUBFORMAT),
    /** {@see java.time.format.DateTimeFormatter} */
    DTF_DATETIME(Temporals::datetime, "dtf_datetime", TemporalAccessor.class, FmtStyle.NONE, FmtStyle.SHORT, FmtStyle.MEDIUM, FmtStyle.LONG, FmtStyle.FULL, FmtStyle.SUBFORMAT),
    /** {@see java.time.format.DateTimeFormatter} */
    BASIC_ISO_DATE(Temporals::BASIC_ISO_DATE, "BASIC_ISO_DATE", TemporalAccessor.class, FmtStyle.NONE),
    /** {@see java.time.format.DateTimeFormatter} */
    ISO_LOCAL_DATE(Temporals::ISO_LOCAL_DATE, "ISO_LOCAL_DATE", TemporalAccessor.class, FmtStyle.NONE),
    /** {@see java.time.format.DateTimeFormatter} */
    ISO_OFFSET_DATE(Temporals::ISO_OFFSET_DATE, "ISO_OFFSET_DATE", TemporalAccessor.class, FmtStyle.NONE),
    /** {@see java.time.format.DateTimeFormatter} */
    ISO_DATE(Temporals::ISO_DATE, "ISO_DATE", TemporalAccessor.class, FmtStyle.NONE),
    /** {@see java.time.format.DateTimeFormatter} */
    ISO_LOCAL_TIME(Temporals::ISO_LOCAL_TIME, "ISO_LOCAL_TIME", TemporalAccessor.class, FmtStyle.NONE),
    /** {@see java.time.format.DateTimeFormatter} */
    ISO_OFFSET_TIME(Temporals::ISO_OFFSET_TIME, "ISO_OFFSET_TIME", TemporalAccessor.class, FmtStyle.NONE),
    /** {@see java.time.format.DateTimeFormatter} */
    ISO_TIME(Temporals::ISO_TIME, "ISO_TIME", TemporalAccessor.class, FmtStyle.NONE),
    /** {@see java.time.format.DateTimeFormatter} */
    ISO_LOCAL_DATE_TIME(Temporals::ISO_LOCAL_DATE_TIME, "ISO_LOCAL_DATE_TIME", TemporalAccessor.class, FmtStyle.NONE),
    /** {@see java.time.format.DateTimeFormatter} */
    ISO_OFFSET_DATE_TIME(Temporals::ISO_OFFSET_DATE_TIME, "ISO_OFFSET_DATE_TIME", TemporalAccessor.class, FmtStyle.NONE),
    /** {@see java.time.format.DateTimeFormatter} */
    ISO_ZONED_DATE_TIME(Temporals::ISO_ZONED_DATE_TIME, "ISO_ZONED_DATE_TIME", TemporalAccessor.class, FmtStyle.NONE),
    /** {@see java.time.format.DateTimeFormatter} */
    ISO_DATE_TIME(Temporals::ISO_DATE_TIME, "ISO_DATE_TIME", TemporalAccessor.class, FmtStyle.NONE),
    /** {@see java.time.format.DateTimeFormatter} */
    ISO_ORDINAL_DATE(Temporals::ISO_ORDINAL_DATE, "ISO_ORDINAL_DATE", TemporalAccessor.class, FmtStyle.NONE),
    /** {@see java.time.format.DateTimeFormatter} */
    ISO_WEEK_DATE(Temporals::ISO_WEEK_DATE, "ISO_WEEK_DATE", TemporalAccessor.class, FmtStyle.NONE),
    /** {@see java.time.format.DateTimeFormatter} */
    ISO_INSTANT(Temporals::ISO_INSTANT, "ISO_INSTANT", TemporalAccessor.class, FmtStyle.NONE),
    /** {@see java.time.format.DateTimeFormatter} */
    RFC_1123_DATE_TIME(Temporals::RFC_1123_DATE_TIME, "RFC_1123_DATE_TIME", TemporalAccessor.class, FmtStyle.NONE);

    private final Fabricator fn;
    private final String type;
    private final Class<?> argType;
    private final Set<FmtStyle> styles;

    FmtType(Fabricator fn, String type, Class<?> argType, FmtStyle... styles) {
        this.fn = fn;
        this.type = type;
        this.argType = argType;
        this.styles = EnumSet.copyOf(asList(styles));
    }

    /**
     * Typically, the type string from the {@link java.text.MessageFormat} table.
     *
     * @return string used in expressions or "(none)"
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
    public Set<FmtStyle> styles() {
        return EnumSet.copyOf(styles);
    }

    Fabricator formatter() {
        return fn;
    }
}
