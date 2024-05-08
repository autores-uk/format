// Copyright 2024 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format.testing;

import java.util.Objects;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

public final class TestEquality {
    private TestEquality() {}

    public static void test(Object...objects) {
        for (Object o1 : objects) {
            nullCheck(Object::equals, o1);
            reflexive(o1, o1);
            for (Object o2 : objects) {
                hashCode(o1, o2);
                symmetric(o1, o2);
                consistent(Object::equals, o1, o2);
                for (Object o3 : objects) {
                    transitive(o1, o2, o3);
                }
            }
        }
    }

    private static void reflexive(Object o1, Object o2) {
        assertEquals(o1, o2);
        assertSame(o1, o2);
    }

    private static void symmetric(Object o1, Object o2) {
        assertEquals(o1.equals(o2), o2.equals(o1), o1 + " " + o2);
    }

    private static void transitive(Object o1, Object o2, Object o3) {
        if (o1.equals(o2) && o2.equals(o3)) {
            assertEquals(o1, o3, o1 + " " + o2 + " " + o3);
        } else if (o1.equals(o2) && !o2.equals(o3)) {
            assertNotEquals(o1, o3, o1 + " " + o2 + " " + o3);
        }
    }

    private static void consistent(BiFunction<Object, Object, Boolean> fn, Object o1, Object o2) {
        assertEquals(fn.apply(o1, o2), fn.apply(o1, o2));
    }

    private static void nullCheck(BiFunction<Object, Object, Boolean> fn, Object o) {
        boolean actual = fn.apply(o, null);
        assertFalse(actual);
    }

    private static void hashCode(Object o1, Object o2) {
        if (Objects.equals(o1, o2)) {
            assertEquals(o1.hashCode(), o2.hashCode());
        }
    }
}
