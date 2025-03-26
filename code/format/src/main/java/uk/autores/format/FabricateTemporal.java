package uk.autores.format;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.function.Function;

final class FabricateTemporal {
    private FabricateTemporal() {}

    static void date(Locale l, FormatVariable v, StringBuffer buf, Object... args) {
        DateTimeFormatter dtf = formatter(DateTimeFormatter::ofLocalizedDate, l, v);
        format(dtf, v, buf, args);
    }

    static void time(Locale l, FormatVariable v, StringBuffer buf, Object... args) {
        DateTimeFormatter dtf = formatter(DateTimeFormatter::ofLocalizedTime, l, v);
        format(dtf, v, buf, args);
    }

    static void datetime(Locale l, FormatVariable v, StringBuffer buf, Object... args) {
        DateTimeFormatter dtf = formatter(DateTimeFormatter::ofLocalizedDateTime, l, v);
        format(dtf, v, buf, args);
    }

    static void ISO_LOCAL_DATE(Locale l, FormatVariable v, StringBuffer buf, Object... args) {
        format(DateTimeFormatter.ISO_LOCAL_DATE, v, buf, args);
    }

    static void ISO_OFFSET_DATE(Locale l, FormatVariable v, StringBuffer buf, Object... args) {
        format(DateTimeFormatter.ISO_OFFSET_DATE, v, buf, args);
    }

    static void ISO_DATE(Locale l, FormatVariable v, StringBuffer buf, Object... args) {
        format(DateTimeFormatter.ISO_OFFSET_DATE, v, buf, args);
    }

    static void ISO_LOCAL_TIME(Locale l, FormatVariable v, StringBuffer buf, Object... args) {
        format(DateTimeFormatter.ISO_LOCAL_TIME, v, buf, args);
    }

    static void ISO_OFFSET_TIME(Locale l, FormatVariable v, StringBuffer buf, Object... args) {
        format(DateTimeFormatter.ISO_OFFSET_TIME, v, buf, args);
    }

    static void ISO_TIME(Locale l, FormatVariable v, StringBuffer buf, Object... args) {
        format(DateTimeFormatter.ISO_TIME, v, buf, args);
    }

    static void ISO_LOCAL_DATE_TIME(Locale l, FormatVariable v, StringBuffer buf, Object... args) {
        format(DateTimeFormatter.ISO_LOCAL_DATE_TIME, v, buf, args);
    }

    static void ISO_OFFSET_DATE_TIME(Locale l, FormatVariable v, StringBuffer buf, Object... args) {
        format(DateTimeFormatter.ISO_OFFSET_DATE_TIME, v, buf, args);
    }

    static void ISO_ZONED_DATE_TIME(Locale l, FormatVariable v, StringBuffer buf, Object... args) {
        format(DateTimeFormatter.ISO_ZONED_DATE_TIME, v, buf, args);
    }

    static void ISO_DATE_TIME(Locale l, FormatVariable v, StringBuffer buf, Object... args) {
        format(DateTimeFormatter.ISO_DATE_TIME, v, buf, args);
    }

    static void ISO_ORDINAL_DATE(Locale l, FormatVariable v, StringBuffer buf, Object... args) {
        format(DateTimeFormatter.ISO_ORDINAL_DATE, v, buf, args);
    }

    static void ISO_WEEK_DATE(Locale l, FormatVariable v, StringBuffer buf, Object... args) {
        format(DateTimeFormatter.ISO_WEEK_DATE, v, buf, args);
    }

    static void ISO_INSTANT(Locale l, FormatVariable v, StringBuffer buf, Object... args) {
        format(DateTimeFormatter.ISO_INSTANT, v, buf, args);
    }

    static void BASIC_ISO_DATE(Locale l, FormatVariable v, StringBuffer buf, Object... args) {
        format(DateTimeFormatter.BASIC_ISO_DATE, v, buf, args);
    }

    static void RFC_1123_DATE_TIME(Locale l, FormatVariable v, StringBuffer buf, Object... args) {
        format(DateTimeFormatter.RFC_1123_DATE_TIME, v, buf, args);
    }

    private static void format(DateTimeFormatter f, FormatVariable variable, StringBuffer buf, Object... args) {
        Object arg = args[variable.index()];
        if (!(arg instanceof TemporalAccessor)) {
            String msg = variable.raw()
                    + " requires "
                    + TemporalAccessor.class.getName();
            throw new IllegalArgumentException(msg);
        }
        TemporalAccessor t = (TemporalAccessor) arg;
        f.formatTo(t, buf);
    }

    private static DateTimeFormatter formatter(Function<FormatStyle, DateTimeFormatter> fn, Locale l, FormatVariable v) {
        if (v.style() == FmtStyle.SUBFORMAT) {
            return DateTimeFormatter.ofPattern(v.subformat(), l);
        }
        FormatStyle style = toDtfStyle(v.style());
        return fn.apply(style).withLocale(l);
    }

    private static FormatStyle toDtfStyle(FmtStyle style) {
        switch (style) {
            case SHORT:
                return FormatStyle.SHORT;
            case LONG:
                return FormatStyle.LONG;
            case FULL:
                return FormatStyle.FULL;
            default:
                return FormatStyle.MEDIUM;
        }
    }
}
