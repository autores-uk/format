package uk.autores.format;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;

class ListsTest {

    @Test
    void list() {
        check("{0,list}", new Object[]{Arrays.asList(1, 2, 3)});
    }

    @Test
    void arrays() {
        check("{0,list}", new Object[]{new Integer[]{1, 2, 3}});
    }

    private void check(String pattern, Object[] args) {
        Locale l = Locale.getDefault();
        MessageFormat mf = new MessageFormat(pattern, l);
        String expected = mf.format(args, new StringBuffer(), new FieldPosition(0))
                .toString();
        FormatExpression expr = FormatExpression.parse(pattern);
        String actual = expr.format(l, args);
        Assertions.assertEquals(expected, actual);
    }
}
