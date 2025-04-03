package uk.autores.format;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TemporalsTest {

    @Test
    void date() {
        Object[] args = {new Object()};
        Locale l = Locale.CANADA;
        FormatExpression expr = FormatExpression.parse("{0,dtf_date}");
        assertThrows(RuntimeException.class, () -> {
            expr.formatTo(l, new StringBuffer(), args);
        });
    }
}
