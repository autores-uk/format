package uk.autores.format;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;

final class Examples {
    private Examples() {}

    static final List<?> EXAMPLE_LIST = Immutable.list(asList("Pugh", "Pugh", "Barney McGrew", "Cuthbert", "Dibble", "Grub"));
    static final ZonedDateTime EXAMPLE_ZDT = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC"));
    static final Integer EXAMPLE_NUMBER = 10_000_000;
}
