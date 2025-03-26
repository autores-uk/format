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
        }
        return NumberFormat.getInstance(l);
    }
}
