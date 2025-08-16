package uk.autores.format;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ArrayIteratorTest {

    @Test
    void next() {
        var it = ArrayIterator.over("foo");
        while (it.hasNext()) {
            it.next();
        }
        assertInstanceOf(ArrayIterator.class, it);
        assertThrowsExactly(NoSuchElementException.class, it::next);
    }
}
