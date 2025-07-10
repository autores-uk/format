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
    private static final Object MUTEX = new Object();
    private static volatile Method getInstance;
    private static volatile Method format;
    private static volatile Object STANDARD;
    private static volatile Object OR;
    private static volatile Object UNIT;
    private static volatile Object FULL;

    private Lists() {}

    static void format(Locale l, FormatVariable variable, StringBuffer buf, Object... args) {
        tryInit();
        Object type = style(variable.style());
        Object formatter = Reflection.invoke(null, getInstance, l, type, FULL);
        Object list = args[variable.index()];
        Reflection.invoke(formatter, format, list, buf, new FieldPosition(0));
    }

    private static void tryInit() {
        if (getInstance == null) {
            synchronized (MUTEX) {
                if (getInstance == null) {
                    init();
                }
            }
        }
    }

    private static void init() {
        String msg = "Use of java.text.ListFormat requires JDK22 or above";
        Class<?> ListFormat = Reflection.type("java.text.ListFormat", msg);
        Class<?> ListFormatType = Reflection.type("java.text.ListFormat$Type", msg);
        Class<?> ListFormatStyle = Reflection.type("java.text.ListFormat$Style", msg);
        getInstance = Reflection.meth(ListFormat, "getInstance", Locale.class, ListFormatType, ListFormatStyle);
        format = Reflection.meth(ListFormat, "format", Object.class, StringBuffer.class, FieldPosition.class);
        STANDARD = Reflection.field(ListFormatType, "STANDARD");
        OR = Reflection.field(ListFormatType, "OR");
        UNIT = Reflection.field(ListFormatType, "UNIT");
        FULL = Reflection.field(ListFormatStyle, "FULL");
    }

    private static Object style(FmtStyle style) {
        return switch (style) {
            case OR -> OR;
            case UNIT -> UNIT;
            default -> STANDARD;
        };
    }
}
