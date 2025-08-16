// Copyright 2024-2025 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Locale;

final class Numbers {
    private Numbers() {}

    static void format(Locale l, FormatVariable variable, StringBuffer buf, Object... args) {
        var nf = switch (variable.style()) {
            case INTEGER -> NumberFormat.getIntegerInstance(l);
            case CURRENCY -> NumberFormat.getCurrencyInstance(l);
            case PERCENT -> NumberFormat.getPercentInstance(l);
            case SUBFORMAT -> new DecimalFormat(variable.subformat(), DecimalFormatSymbols.getInstance(l));
            case COMPACT_LONG -> NumberFormat.getCompactNumberInstance(l, NumberFormat.Style.LONG);
            case COMPACT_SHORT -> NumberFormat.getCompactNumberInstance(l, NumberFormat.Style.SHORT);
            default -> NumberFormat.getInstance(l);
        };

        Object value = args[variable.index()];
        nf.format(value, buf, new FieldPosition(0));
    }
}
