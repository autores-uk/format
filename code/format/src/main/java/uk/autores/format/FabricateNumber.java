package uk.autores.format;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Locale;

final class FabricateNumber {
    private FabricateNumber() {}

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
