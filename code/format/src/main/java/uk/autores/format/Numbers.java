// Copyright 2024-2025 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Locale;

final class Numbers {
    private Numbers() {}

    static void format(Locale l, FormatVariable variable, StringBuffer buf, Object... args) {
        Object value = args[variable.index()];
        nf(l, variable).format(value, buf, new FieldPosition(0));
    }

    private static NumberFormat nf(Locale l, FormatVariable variable) {
        switch (variable.style()) {
            case INTEGER:
                return NumberFormat.getIntegerInstance(l);
            case CURRENCY:
                return NumberFormat.getCurrencyInstance(l);
            case PERCENT:
                return NumberFormat.getPercentInstance(l);
            case SUBFORMAT:
                return new DecimalFormat(variable.subformat(), DecimalFormatSymbols.getInstance(l));
            case COMPACT_LONG:
            case COMPACT_SHORT:
                return compact(l, variable.style());
        }
        return NumberFormat.getInstance(l);
    }

    /* These methods require JDK12+. */
    private static NumberFormat compact(Locale l, FmtStyle style) {
        try {
            return (NumberFormat) reflect(l, style);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private static Object reflect(Locale l, FmtStyle style) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        // TODO: efficiency

        Class<?> numberStyle = Numbers.class.getClassLoader().loadClass("java.text.NumberFormat$Style");
        Field field;
        if (style == FmtStyle.COMPACT_LONG) {
            field = numberStyle.getField("LONG");
        } else {
            field = numberStyle.getField("SHORT");
        }
        Object s = field.get(null);

        Class<NumberFormat> nf = NumberFormat.class;
        Method method = nf.getMethod("getCompactNumberInstance", Locale.class, numberStyle);
        return method.invoke(null, l, s);
    }
}
