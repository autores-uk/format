// Copyright 2024-2025 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

import java.time.temporal.TemporalAccessor;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 *
 * <p>
 *     The format types supported by {@link java.text.MessageFormat}.
 * </p>
 * <p>
 *     <code>"{0,number}"</code> is an example expression that results in {@link #NUMBER} when parsed.
 * </p>
 *
 * @see FormatVariable#type()
 */
public enum FmtType {
    /** Indicates that no type has been specified. */
    NONE(AnyObject::format, "(none)", Object.class, FmtStyle.NONE),
    /** Maps to {@link java.text.NumberFormat} */
    NUMBER(Numbers::format, "number", Number.class, FmtStyle.NONE, FmtStyle.INTEGER, FmtStyle.CURRENCY, FmtStyle.PERCENT, FmtStyle.SUBFORMAT),
    /** Maps to {@link java.time.format.DateTimeFormatter} */
    DATE(Temporals::date, "date", TemporalAccessor.class, FmtStyle.NONE, FmtStyle.SHORT, FmtStyle.MEDIUM, FmtStyle.LONG, FmtStyle.FULL, FmtStyle.SUBFORMAT),
    /** Maps to {@link java.time.format.DateTimeFormatter} */
    TIME(Temporals::time, "time", TemporalAccessor.class, FmtStyle.NONE, FmtStyle.SHORT, FmtStyle.MEDIUM, FmtStyle.LONG, FmtStyle.FULL, FmtStyle.SUBFORMAT),
    /** Maps to {@link java.text.ChoiceFormat} */
    CHOICE(Choices::format, "choice", Number.class, FmtStyle.SUBFORMAT),
    /** Maps to {@link java.time.format.DateTimeFormatter} */
    DTF_DATE(Temporals::date, "dtf_date", TemporalAccessor.class, FmtStyle.NONE, FmtStyle.SHORT, FmtStyle.MEDIUM, FmtStyle.LONG, FmtStyle.FULL, FmtStyle.SUBFORMAT),
    /** Maps to {@link java.time.format.DateTimeFormatter} */
    DTF_TIME(Temporals::time, "dtf_time", TemporalAccessor.class, FmtStyle.NONE, FmtStyle.SHORT, FmtStyle.MEDIUM, FmtStyle.LONG, FmtStyle.FULL, FmtStyle.SUBFORMAT),
    /** Maps to {@link java.time.format.DateTimeFormatter} */
    DTF_DATETIME(Temporals::datetime, "dtf_datetime", TemporalAccessor.class, FmtStyle.NONE, FmtStyle.SHORT, FmtStyle.MEDIUM, FmtStyle.LONG, FmtStyle.FULL, FmtStyle.SUBFORMAT),
    /** Maps to {@link java.time.format.DateTimeFormatter#BASIC_ISO_DATE} */
    BASIC_ISO_DATE(Temporals::BASIC_ISO_DATE, "BASIC_ISO_DATE", TemporalAccessor.class, FmtStyle.NONE),
    /** Maps to {@link java.time.format.DateTimeFormatter#ISO_LOCAL_DATE} */
    ISO_LOCAL_DATE(Temporals::ISO_LOCAL_DATE, "ISO_LOCAL_DATE", TemporalAccessor.class, FmtStyle.NONE),
    /** Maps to {@link java.time.format.DateTimeFormatter#ISO_OFFSET_DATE} */
    ISO_OFFSET_DATE(Temporals::ISO_OFFSET_DATE, "ISO_OFFSET_DATE", TemporalAccessor.class, FmtStyle.NONE),
    /** Maps to {@link java.time.format.DateTimeFormatter#ISO_DATE} */
    ISO_DATE(Temporals::ISO_DATE, "ISO_DATE", TemporalAccessor.class, FmtStyle.NONE),
    /** Maps to {@link java.time.format.DateTimeFormatter#ISO_LOCAL_TIME} */
    ISO_LOCAL_TIME(Temporals::ISO_LOCAL_TIME, "ISO_LOCAL_TIME", TemporalAccessor.class, FmtStyle.NONE),
    /** Maps to {@link java.time.format.DateTimeFormatter#ISO_OFFSET_TIME} */
    ISO_OFFSET_TIME(Temporals::ISO_OFFSET_TIME, "ISO_OFFSET_TIME", TemporalAccessor.class, FmtStyle.NONE),
    /** Maps to {@link java.time.format.DateTimeFormatter#ISO_TIME} */
    ISO_TIME(Temporals::ISO_TIME, "ISO_TIME", TemporalAccessor.class, FmtStyle.NONE),
    /** Maps to {@link java.time.format.DateTimeFormatter#ISO_LOCAL_DATE_TIME} */
    ISO_LOCAL_DATE_TIME(Temporals::ISO_LOCAL_DATE_TIME, "ISO_LOCAL_DATE_TIME", TemporalAccessor.class, FmtStyle.NONE),
    /** Maps to {@link java.time.format.DateTimeFormatter#ISO_OFFSET_DATE_TIME} */
    ISO_OFFSET_DATE_TIME(Temporals::ISO_OFFSET_DATE_TIME, "ISO_OFFSET_DATE_TIME", TemporalAccessor.class, FmtStyle.NONE),
    /** Maps to {@link java.time.format.DateTimeFormatter#ISO_ZONED_DATE_TIME} */
    ISO_ZONED_DATE_TIME(Temporals::ISO_ZONED_DATE_TIME, "ISO_ZONED_DATE_TIME", TemporalAccessor.class, FmtStyle.NONE),
    /** Maps to {@link java.time.format.DateTimeFormatter#ISO_DATE_TIME} */
    ISO_DATE_TIME(Temporals::ISO_DATE_TIME, "ISO_DATE_TIME", TemporalAccessor.class, FmtStyle.NONE),
    /** Maps to {@link java.time.format.DateTimeFormatter#ISO_ORDINAL_DATE} */
    ISO_ORDINAL_DATE(Temporals::ISO_ORDINAL_DATE, "ISO_ORDINAL_DATE", TemporalAccessor.class, FmtStyle.NONE),
    /** Maps to {@link java.time.format.DateTimeFormatter#ISO_WEEK_DATE} */
    ISO_WEEK_DATE(Temporals::ISO_WEEK_DATE, "ISO_WEEK_DATE", TemporalAccessor.class, FmtStyle.NONE),
    /** Maps to {@link java.time.format.DateTimeFormatter#ISO_INSTANT} */
    ISO_INSTANT(Temporals::ISO_INSTANT, "ISO_INSTANT", TemporalAccessor.class, FmtStyle.NONE),
    /** Maps to {@link java.time.format.DateTimeFormatter#RFC_1123_DATE_TIME} */
    RFC_1123_DATE_TIME(Temporals::RFC_1123_DATE_TIME, "RFC_1123_DATE_TIME", TemporalAccessor.class, FmtStyle.NONE),
    /** Maps to <a href="https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/text/ListFormat.html">java.text.ListFormat</a> */
    LIST(Lists::format, "list", List.class, FmtStyle.NONE, FmtStyle.OR, FmtStyle.UNIT);

    private final Fmt fn;
    private final String label;
    private final Class<?> argType;
    private final Set<FmtStyle> styles;

    FmtType(Fmt fn, String label, Class<?> argType, FmtStyle... styles) {
        this.fn = fn;
        this.label = label;
        this.argType = argType;
        this.styles = EnumSet.copyOf(asList(styles));
    }

    /**
     * Typically, the type string from the {@link java.text.MessageFormat} table.
     *
     * @return string used in expressions or "(none)"
     */
    public String label() {
        return label;
    }

    /**
     * <ul>
     *     <li>{@link #NONE}: {@link String}</li>
     *     <li>{@link #NUMBER}, {@link #CHOICE}: {@link Number}</li>
     *     <li>Others: {@link TemporalAccessor}</li>
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

    Fmt formatter() {
        return fn;
    }
}
