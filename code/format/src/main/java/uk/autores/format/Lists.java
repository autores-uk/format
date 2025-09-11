// Copyright 2024-2025 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0

package uk.autores.format;

import java.lang.reflect.Method;
import java.text.FieldPosition;
import java.util.Locale;

/*
 * Compile target is less than JDK22 so uses reflection.
 */
final class Lists {
    private static volatile LF cached;

    private Lists() {}

    static void format(Locale l, FormatVariable variable, StringBuffer buf, Object... args) {
        if (cached == null) {
            cached = init();
        }
        LF ListFormat = cached;
        Object type = style(ListFormat, variable.style());
        Object formatter = Reflect.invoke(null, ListFormat.getInstance, l, type, ListFormat.FULL);
        Object list = args[variable.index()];
        variable.requireNonNull(list);
        Reflect.invoke(formatter, ListFormat.format, list, buf, new FieldPosition(0));
    }

    private static LF init() {
        var msg = "Use of java.text.ListFormat requires JDK22 or above";
        Class<?> ListFormat = Reflect.type("java.text.ListFormat", msg);
        Class<?> ListFormatType = Reflect.type("java.text.ListFormat$Type", msg);
        Class<?> ListFormatStyle = Reflect.type("java.text.ListFormat$Style", msg);
        Method getInstance = Reflect.meth(ListFormat, "getInstance", Locale.class, ListFormatType, ListFormatStyle);
        Method format = Reflect.meth(ListFormat, "format", Object.class, StringBuffer.class, FieldPosition.class);
        Object STANDARD = Reflect.field(ListFormatType, "STANDARD");
        Object OR = Reflect.field(ListFormatType, "OR");
        Object UNIT = Reflect.field(ListFormatType, "UNIT");
        Object FULL = Reflect.field(ListFormatStyle, "FULL");

        return new LF(getInstance, format, STANDARD, OR, UNIT, FULL);
    }

    private static Object style(LF ListFormat, FmtStyle style) {
        return switch (style) {
            case OR -> ListFormat.OR;
            case UNIT -> ListFormat.UNIT;
            default -> ListFormat.STANDARD;
        };
    }

    private record LF(Method getInstance,
                      Method format,
                      Object STANDARD,
                      Object OR,
                      Object UNIT,
                      Object FULL) {}
}
