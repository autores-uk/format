package uk.autores.format;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;

final class Dates {
    private Dates() {}

    static void date(Locale l, FormatVariable v, StringBuffer buf, Object... args) {
        Temporals.date(l, v, buf, handleLegacy(v, args));
    }

    static void time(Locale l, FormatVariable v, StringBuffer buf, Object... args) {
        Temporals.time(l, v, buf, handleLegacy(v, args));
    }

    private static Object[] handleLegacy(FormatVariable v, Object... args) {
        Object[] result = args;
        FmtType type = v.type();
        if (type == FmtType.DATE || type == FmtType.TIME) {
            Object value = args[v.index()];
            if (value instanceof Date d) {
                var zid = ZoneId.systemDefault();
                var zdt = ZonedDateTime.ofInstant(d.toInstant(), zid);

                result = args.clone();
                result[v.index()] = zdt;
            }
        }
        return result;
    }
}
