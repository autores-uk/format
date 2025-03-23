package uk.autores.format;

import java.util.Locale;

final class EvalAny implements Evaluator {
    @Override
    public FormatType type() {
        return FormatType.NONE;
    }

    @Override
    public void format(Locale l, StringBuffer buf, FormatVariable variable, Object value) {
        buf.append(value);
    }
}
