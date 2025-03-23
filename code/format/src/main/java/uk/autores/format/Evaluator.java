package uk.autores.format;

import java.util.Locale;

interface Evaluator {
    FormatType type();
    void format(Locale l, StringBuffer buf, FormatVariable variable, Object value);
}
