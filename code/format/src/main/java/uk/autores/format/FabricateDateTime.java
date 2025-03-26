package uk.autores.format;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

final class FabricateDateTime {
    private FabricateDateTime() {}

    static void date(Locale l, FormatVariable variable, StringBuffer buf, Object... args) {
        formatDate(variable, buf, df(l, variable), args);
    }

    static void time(Locale l, FormatVariable variable, StringBuffer buf, Object... args) {
        formatDate(variable, buf, tf(l, variable), args);
    }

    private static DateFormat df(Locale l, FormatVariable variable) {
        switch (variable.style()) {
            case SHORT:
                return DateFormat.getDateInstance(DateFormat.SHORT, l);
            case MEDIUM:
                return DateFormat.getDateInstance(DateFormat.MEDIUM, l);
            case LONG:
                return DateFormat.getDateInstance(DateFormat.LONG, l);
            case FULL:
                return DateFormat.getDateInstance(DateFormat.FULL, l);
            case SUBFORMAT:
                return new SimpleDateFormat(variable.subformat(), l);
        }
        return DateFormat.getDateInstance(DateFormat.DEFAULT, l);
    }

    private static DateFormat tf(Locale l, FormatVariable variable) {
        switch (variable.style()) {
            case SHORT:
                return DateFormat.getTimeInstance(DateFormat.SHORT, l);
            case MEDIUM:
                return DateFormat.getTimeInstance(DateFormat.MEDIUM, l);
            case LONG:
                return DateFormat.getTimeInstance(DateFormat.LONG, l);
            case FULL:
                return DateFormat.getTimeInstance(DateFormat.FULL, l);
            case SUBFORMAT:
                return new SimpleDateFormat(variable.subformat(), l);
        }
        return DateFormat.getTimeInstance(DateFormat.DEFAULT, l);
    }

    private static void formatDate(FormatVariable v, StringBuffer buf, DateFormat df, Object... args) {
        Object value = args[v.index()];
        if (value instanceof ZonedDateTime) {
            ZonedDateTime zdt = (ZonedDateTime) value;
            ZoneId zoneId = zdt.getZone();
            TimeZone zone = TimeZone.getTimeZone(zoneId);
            df.setTimeZone(zone);
            value = new Date(zdt.toEpochSecond());
        }
        df.format(value, buf, new FieldPosition(0));
    }
}
