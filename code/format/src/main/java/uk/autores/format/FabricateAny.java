package uk.autores.format;

import java.util.Locale;

final class FabricateAny {
    private FabricateAny() {}

    static void format(Locale l, FormatVariable variable, StringBuffer buf, Object... args) {
        Object value = args[variable.index()];
        buf.append(value);
    }
}
