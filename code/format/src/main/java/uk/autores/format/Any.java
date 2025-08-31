package uk.autores.format;

import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;

final class Any {
    private Any() {}

    static void format(Locale l, FormatVariable v, StringBuffer buf, Object... args) {
        Object arg = args[v.index()];
        if (arg instanceof Number) {
            Numbers.format(l, v, buf, args);
        } else if (arg instanceof Date) {
            new MessageFormat(v.toString(), l).format(args, buf, new FieldPosition(0));
        } else {
            buf.append(arg);
        }
    }
}
