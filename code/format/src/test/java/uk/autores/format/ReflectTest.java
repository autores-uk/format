package uk.autores.format;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class ReflectTest {

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
            Class<?> me = Reflect.type(ReflectTest.class.getName(), "");
            assertSame(ReflectTest.class, me);
        }
        {
            String msg = "error abc";
            try {
                Reflect.type("DoesNotExist", msg);
                fail();
            } catch (Exception e) {
                assertInstanceOf(RuntimeException.class, e);
                assertEquals(msg, e.getMessage());
            }
        }
    }

    @Test
    void field() {
        Class<?> me = Reflect.type(ReflectTest.class.getName(), "");
        Object actual = Reflect.field(me, "OK");
        assertEquals(OK, actual);
    }

    @Test
    void invoke() {
        Class<?> me = Reflect.type(ReflectTest.class.getName(), "");
        {
            Method m = Reflect.meth(me, "ok");
            Object actual = Reflect.invoke(null, m);
            assertEquals(OK, actual);
        }
        {
            Method m = Reflect.meth(me, "throwUp", Exception.class);
            try {
                Reflect.invoke(null, m, new TestException());
                fail();
            } catch (Exception e) {
                assertInstanceOf(InvocationTargetException.class, e.getCause());
            }
        }
        {
            Method m = Reflect.meth(me, "throwUp", Exception.class);
            try {
                Reflect.invoke(null, m, new TestRuntimeException());
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