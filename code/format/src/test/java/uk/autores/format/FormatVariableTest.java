package uk.autores.format;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class FormatVariableTest {

    @Test
    void laxMatch() {
        var zero = index(0);
        var one = index(1);
        assertThrowsExactly(IllegalArgumentException.class, () -> FormatVariable.laxMatch(zero, one));
    }

    @Test
    void strictMatch() {
        var zero = index(0);
        var one = index(1);
        assertThrowsExactly(IllegalArgumentException.class, () -> FormatVariable.strictMatch(zero, one));
    }

    private FormatVariable index(int i) {
        for (Formatter f : FormatExpression.parse("{0}{1}")) {
            if (f instanceof FormatVariable v && i == v.index()) {
                return v;
            }
        }
        throw new AssertionError("" + i);
    }
}