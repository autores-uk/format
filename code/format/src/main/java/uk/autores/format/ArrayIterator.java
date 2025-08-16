package uk.autores.format;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class ArrayIterator<E> implements Iterator<E> {
    private final E[] a;
    private int index;

    private ArrayIterator(E[] array) {
        this.a = array;
    }

    static <E> Iterator<E> over(E... array) {
        if (array.length == 0) {
            return Collections.emptyIterator();
        }
        return new ArrayIterator<>(array);
    }

    @Override
    public boolean hasNext() {
        return index < a.length;
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return a[index++];
    }
}
