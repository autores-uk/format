package uk.autores.format;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class ReflectionTest {

    public static final String OK = "OK";

    public static String ok() {
        return OK;
    }

    public static void throwUp(Exception e) throws Exception {
        throw e;
    }

    @Test
    void type() {
        {
            Class<?> me = Reflection.type(ReflectionTest.class.getName(), "");
            assertSame(ReflectionTest.class, me);
        }
        {
            String msg = "error abc";
            try {
                Reflection.type("DoesNotExist", msg);
                fail();
            } catch (Exception e) {
                assertInstanceOf(RuntimeException.class, e);
                assertEquals(msg, e.getMessage());
            }
        }
    }

    @Test
    void field() {
        Class<?> me = Reflection.type(ReflectionTest.class.getName(), "");
        Object actual = Reflection.field(me, "OK");
        assertEquals(OK, actual);
    }

    @Test
    void invoke() {
        Class<?> me = Reflection.type(ReflectionTest.class.getName(), "");
        {
            Method m = Reflection.meth(me, "ok");
            Object actual = Reflection.invoke(null, m);
            assertEquals(OK, actual);
        }
        {
            Method m = Reflection.meth(me, "throwUp", Exception.class);
            try {
                Reflection.invoke(null, m, new TestException());
                fail();
            } catch (Exception e) {
                assertInstanceOf(InvocationTargetException.class, e.getCause());
            }
        }
        {
            Method m = Reflection.meth(me, "throwUp", Exception.class);
            try {
                Reflection.invoke(null, m, new TestRuntimeException());
                fail();
            } catch (Exception e) {
                assertInstanceOf(TestRuntimeException.class, e);
            }
        }
    }

    public static class TestException extends Exception {
    }

    public static class TestRuntimeException extends RuntimeException {
    }
}