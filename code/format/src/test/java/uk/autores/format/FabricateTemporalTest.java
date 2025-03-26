package uk.autores.format;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FabricateTemporalTest {

    @Test
    void date() {
        Object[] args = {new Date(0)};
        Locale l = Locale.CANADA;
        List<FormatSegment> expr = Formatting.parse("{0,dtf_date}");
        assertThrows(RuntimeException.class, () -> {
            Formatting.format(expr, l, args);
        });
    }
}
