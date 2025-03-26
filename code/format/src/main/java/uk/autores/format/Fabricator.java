package uk.autores.format;

import java.util.Locale;

@FunctionalInterface
interface Fabricator {
    void format(Locale l, FormatVariable variable, StringBuffer buf, Object... args);
}
