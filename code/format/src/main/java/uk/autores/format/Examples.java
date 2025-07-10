package uk.autores.format;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

final class Examples {
    private Examples() {}

    private static final List<?> EXAMPLE_LIST = List.of("Pugh", "Pugh", "Barney McGrew", "Cuthbert", "Dibble", "Grub");
    private static final ZonedDateTime EXAMPLE_ZDT = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC"));
    private static final Integer EXAMPLE_NUMBER = 10_000_000;

    static void set(Object[] args, FormatVariable v) {
        int index = v.index();
        switch (v.type()) {
            case NONE:
                args[index] = "De finibus bonorum et malorum";
                break;
            case NUMBER:
            case CHOICE:
                args[index] = EXAMPLE_NUMBER;
                break;
            case DATE:
            case TIME:
                args[index] = new Date(0);
                break;
            case LIST:
                args[index] = EXAMPLE_LIST;
                break;
            default:
                args[index] = EXAMPLE_ZDT;
                break;
        }
    }
}
