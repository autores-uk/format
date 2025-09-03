package uk.autores.format;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.util.Date;
import java.util.Locale;

final class Any {
    private Any() {}

    static void format(Locale l, FormatVariable v, StringBuffer buf, Object... args) {
        Object arg = args[v.index()];
        if (arg instanceof Number) {
            Numbers.format(l, v, buf, args);
        } else if (arg instanceof Date d) {
            DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, l)
                    .format(d, buf, new FieldPosition(0));
        } else {
            buf.append(arg);
        }
    }
}
